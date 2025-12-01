package net.pneumono.locator_lodestones;

import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWaypointHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.minecraft.world.waypoint.Waypoint;
import net.pneumono.locator_lodestones.config.ConfigManager;

import java.util.*;

public class WaypointTracking {
    private static final Map<Either<UUID, String>, TrackedWaypoint> WAYPOINTS = new HashMap<>();
    private static final Map<Either<UUID, String>, Optional<Text>> WAYPOINT_NAMES = new HashMap<>();
    private static boolean dirty = false;
    private static long lastUpdateTime = 0;

    public static Collection<TrackedWaypoint> getWaypoints() {
        return WAYPOINTS.values();
    }

    public static Optional<Text> getWaypointName(Either<UUID, String> source) {
        return WAYPOINT_NAMES.get(source);
    }

    public static void markWaypointsDirty() {
        dirty = true;
    }

    public static void resetWaypoints() {
        WAYPOINTS.clear();
        WAYPOINT_NAMES.clear();
        lastUpdateTime = 0;
        markWaypointsDirty();
    }

    public static void updateWaypoints(ClientPlayerEntity player) {
        if (!dirty || player == null || (lastUpdateTime + 20 > player.age && lastUpdateTime < player.age)) return;
        lastUpdateTime = player.age;
        dirty = false;

        Map<Either<UUID, String>, TrackedWaypoint> oldWaypoints = new HashMap<>(WAYPOINTS);
        WAYPOINTS.clear();
        getWaypointsFromPlayer(player).forEach(waypoint -> WAYPOINTS.put(waypoint.getSource(), waypoint));

        ClientWaypointHandler waypointHandler = player.networkHandler.getWaypointHandler();

        for (TrackedWaypoint newWaypoint : WAYPOINTS.values()) {
            if (oldWaypoints.containsKey(newWaypoint.getSource()) && waypointHandler.waypoints.containsKey(newWaypoint.getSource())) {
                waypointHandler.onUpdate(newWaypoint);
            } else {
                waypointHandler.onTrack(newWaypoint);
            }
        }

        for (TrackedWaypoint oldWaypoint : oldWaypoints.values()) {
            if (!WAYPOINTS.containsKey(oldWaypoint.getSource())) {
                waypointHandler.onUntrack(oldWaypoint);
            }
        }
    }

    private static List<TrackedWaypoint> getWaypointsFromPlayer(PlayerEntity player) {
        WAYPOINT_NAMES.clear();

        List<ItemStack> stacks = new ArrayList<>();
        DefaultedList<ItemStack> mainStacks = player.getInventory().getMainStacks();
        if (mainStacks != null) {
            stacks.addAll(player.getInventory().getMainStacks());
        }
        ItemStack offHandStack = player.getOffHandStack();
        if (offHandStack != null) {
            stacks.add(player.getOffHandStack());
        }

        List<TrackedWaypoint> waypoints = new ArrayList<>();
        for (ItemStack stack : stacks) {
            //? if >=1.21.9 {
            RegistryKey<World> dimension = player.getEntityWorld().getRegistryKey();
            //?} else {
            /*RegistryKey<World> dimension = player.getWorld().getRegistryKey();
            *///?}
            waypoints.addAll(getWaypointsFromStack(player, dimension, stack));
        }
        return waypoints;
    }

    private static List<TrackedWaypoint> getWaypointsFromStack(PlayerEntity player, RegistryKey<World> dimension, ItemStack stack) {
        List<TrackedWaypoint> waypoints = new ArrayList<>();

        if (ConfigManager.shouldShowRecovery()) {
            Optional<GlobalPos> lastDeathPos = player.getLastDeathPos();
            if (lastDeathPos.isPresent() && stack.isOf(Items.RECOVERY_COMPASS)) {
                GlobalPos pos = lastDeathPos.get();
                if (pos.dimension() == dimension && pos.pos() != null) {
                    Waypoint.Config config = new Waypoint.Config();
                    config.style = LocatorLodestones.DEATH_STYLE;
                    config.color = Optional.ofNullable(
                            ColorHandler.getColor(stack).orElse(ConfigManager.getRecoveryColor().getColorWithAlpha())
                    );
                    Either<UUID, String> source = Either.right("death_" + pos);
                    waypoints.add(new TrackedWaypoint.Positional(
                            source,
                            config,
                            bufFromPos(pos.pos())
                    ));
                    WAYPOINT_NAMES.put(source, getText(stack));
                }
            }
        }

        LodestoneTrackerComponent trackerComponent = stack.get(DataComponentTypes.LODESTONE_TRACKER);
        if (trackerComponent != null && trackerComponent.target().isPresent()) {

            GlobalPos pos = trackerComponent.target().get();
            if (pos.dimension() == dimension && pos.pos() != null) {
                Waypoint.Config config = new Waypoint.Config();
                config.style = LocatorLodestones.LODESTONE_STYLE;
                config.color = Optional.ofNullable(
                        ColorHandler.getColor(stack).orElse(ConfigManager.getLodestoneColor().getColorWithAlpha())
                );
                Either<UUID, String> source = Either.right("lodestone_" + pos);
                waypoints.add(new TrackedWaypoint.Positional(
                        source,
                        config,
                        bufFromPos(pos.pos())
                ));
                WAYPOINT_NAMES.put(source, getText(stack));
            }
        }

        if (ConfigManager.shouldShowBundled()) {
            BundleContentsComponent contentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
            if (contentsComponent != null) {
                contentsComponent.stream().forEach(
                        bundledStack -> waypoints.addAll(getWaypointsFromStack(player, dimension, bundledStack))
                );
            }
        }

        return waypoints;
    }

    private static PacketByteBuf bufFromPos(BlockPos pos) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(pos.getX());
        buf.writeVarInt(pos.getY());
        buf.writeVarInt(pos.getZ());
        return buf;
    }

    private static Optional<Text> getText(ItemStack stack) {
        Text text = stack.get(DataComponentTypes.CUSTOM_NAME);
        if (text == null) {
            text = stack.get(DataComponentTypes.ITEM_NAME);
        }
        return ColorHandler.removeColorCode(text);
    }
}
