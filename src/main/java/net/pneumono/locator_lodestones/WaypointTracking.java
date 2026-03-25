package net.pneumono.locator_lodestones;

import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.waypoints.ClientWaypointManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import net.minecraft.world.waypoints.TrackedWaypoint;
import net.minecraft.world.waypoints.Waypoint;
import net.pneumono.locator_lodestones.config.ConfigManager;

import java.util.*;

public class WaypointTracking {
    private static final Map<Either<UUID, String>, TrackedWaypoint> WAYPOINTS = new HashMap<>();
    private static final Map<Either<UUID, String>, Optional<Component>> WAYPOINT_NAMES = new HashMap<>();
    private static boolean dirty = false;
    private static long lastUpdateTime = 0;

    public static Collection<TrackedWaypoint> getWaypoints() {
        return WAYPOINTS.values();
    }

    public static Optional<Component> getWaypointName(Either<UUID, String> source) {
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

    public static void updateWaypoints(LocalPlayer player) {
        if (!dirty || player == null || (lastUpdateTime + 20 > player.tickCount && lastUpdateTime < player.tickCount)) return;
        lastUpdateTime = player.tickCount;
        dirty = false;

        Map<Either<UUID, String>, TrackedWaypoint> oldWaypoints = new HashMap<>(WAYPOINTS);
        WAYPOINTS.clear();
        getWaypointsFromPlayer(player).forEach(waypoint -> WAYPOINTS.put(waypoint.id(), waypoint));

        ClientWaypointManager waypointHandler = player.connection.getWaypointManager();

        for (TrackedWaypoint newWaypoint : WAYPOINTS.values()) {
            if (oldWaypoints.containsKey(newWaypoint.id()) && waypointHandler.waypoints.containsKey(newWaypoint.id())) {
                waypointHandler.updateWaypoint(newWaypoint);
            } else {
                waypointHandler.trackWaypoint(newWaypoint);
            }
        }

        for (TrackedWaypoint oldWaypoint : oldWaypoints.values()) {
            if (!WAYPOINTS.containsKey(oldWaypoint.id())) {
                waypointHandler.untrackWaypoint(oldWaypoint);
            }
        }
    }

    private static List<TrackedWaypoint> getWaypointsFromPlayer(Player player) {
        WAYPOINT_NAMES.clear();

        List<ItemStack> stacks = new ArrayList<>();
        NonNullList<ItemStack> mainStacks = player.getInventory().getNonEquipmentItems();
        if (mainStacks != null) {
            stacks.addAll(player.getInventory().getNonEquipmentItems());
        }
        ItemStack offHandStack = player.getOffhandItem();
        if (offHandStack != null) {
            stacks.add(player.getOffhandItem());
        }

        List<TrackedWaypoint> waypoints = new ArrayList<>();
        for (ItemStack stack : stacks) {
            ResourceKey<Level> dimension = player.level().dimension();
            waypoints.addAll(getWaypointsFromStack(player, dimension, stack));
        }
        return waypoints;
    }

    private static List<TrackedWaypoint> getWaypointsFromStack(Player player, ResourceKey<Level> dimension, ItemStack stack) {
        List<TrackedWaypoint> waypoints = new ArrayList<>();

        if (ConfigManager.shouldShowRecovery()) {
            Optional<GlobalPos> lastDeathPos = player.getLastDeathLocation();
            if (lastDeathPos.isPresent() && stack.is(Items.RECOVERY_COMPASS)) {
                GlobalPos pos = lastDeathPos.get();
                if (pos.dimension() == dimension && pos.pos() != null) {
                    Waypoint.Icon config = new Waypoint.Icon();
                    config.style = LocatorLodestones.DEATH_STYLE;
                    config.color = Optional.ofNullable(
                            ColorHandler.getColor(stack).orElse(ConfigManager.getRecoveryColor().getColorWithAlpha())
                    );
                    Either<UUID, String> source = Either.right("death_" + pos);
                    waypoints.add(new TrackedWaypoint.Vec3iWaypoint(
                            source,
                            config,
                            bufFromPos(pos.pos())
                    ));
                    WAYPOINT_NAMES.put(source, getText(stack));
                }
            }
        }

        LodestoneTracker trackerComponent = stack.get(DataComponents.LODESTONE_TRACKER);
        if (trackerComponent != null && trackerComponent.target().isPresent()) {

            GlobalPos pos = trackerComponent.target().get();
            if (pos.dimension() == dimension && pos.pos() != null) {
                Waypoint.Icon config = new Waypoint.Icon();
                config.style = LocatorLodestones.LODESTONE_STYLE;
                config.color = Optional.ofNullable(
                        ColorHandler.getColor(stack).orElse(ConfigManager.getLodestoneColor().getColorWithAlpha())
                );
                Either<UUID, String> source = Either.right("lodestone_" + pos);
                waypoints.add(new TrackedWaypoint.Vec3iWaypoint(
                        source,
                        config,
                        bufFromPos(pos.pos())
                ));
                WAYPOINT_NAMES.put(source, getText(stack));
            }
        }

        if (ConfigManager.shouldShowBundled()) {
            BundleContents contentsComponent = stack.get(DataComponents.BUNDLE_CONTENTS);
            if (contentsComponent != null) {
                contentsComponent.itemCopyStream().forEach(
                        bundledStack -> waypoints.addAll(getWaypointsFromStack(player, dimension, bundledStack))
                );
            }
        }

        return waypoints;
    }

    private static FriendlyByteBuf bufFromPos(BlockPos pos) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(pos.getX());
        buf.writeVarInt(pos.getY());
        buf.writeVarInt(pos.getZ());
        return buf;
    }

    private static Optional<Component> getText(ItemStack stack) {
        Component text = stack.get(DataComponents.CUSTOM_NAME);
        if (text == null) {
            text = stack.get(DataComponents.ITEM_NAME);
        }
        return ColorHandler.removeColorCode(text);
    }
}
