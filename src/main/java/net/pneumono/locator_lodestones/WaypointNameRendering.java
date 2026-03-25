package net.pneumono.locator_lodestones;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.waypoints.TrackedWaypoint;
import net.pneumono.locator_lodestones.config.ConfigManager;

import java.util.Optional;

//? if >=1.21.9 {
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.waypoints.PartialTickSupplier;
//?}

public class WaypointNameRendering {
    public static void renderNames(Minecraft client, GuiGraphics context, DeltaTracker tickCounter, int centerY) {
        if (!ConfigManager.tabShowsNames() || !client.options.keyPlayerList.isDown()) return;

        //? if >=1.21.9 {
        Entity cameraEntity = client.getCameraEntity();
        if (cameraEntity == null) return;
        Level world = cameraEntity.level();
        PartialTickSupplier entityTickProgress = (tickedEntity) -> tickCounter.getGameTimeDeltaPartialTick(
                !world.tickRateManager().isEntityFrozen(tickedEntity)
        );
        //?}

        TrackedWaypoint bestWaypoint = null;
        double bestYaw = 61;
        for (TrackedWaypoint waypoint : WaypointTracking.getWaypoints()) {
            //? if >=1.21.9 {
            double yaw = waypoint.yawAngleToCamera(client.level, client.gameRenderer.getMainCamera(), entityTickProgress);
            //?} else {
            /*double yaw = waypoint.yawAngleToCamera(client.level, client.gameRenderer.getMainCamera());
            *///?}
            double absYaw = Math.abs(yaw);
            if (absYaw < Math.abs(bestYaw)) {
                bestYaw = yaw;
                bestWaypoint = waypoint;
            }
        }

        if (bestWaypoint != null) {
            Optional<Component> textOptional = WaypointTracking.getWaypointName(bestWaypoint.id());
            if (textOptional.isPresent()) {
                Component text = textOptional.get();
                Font textRenderer = client.font;

                int width = textRenderer.width(text);
                int x = getXFromYaw(context, bestYaw) - width / 2;

                context.fill(x + 5 - 2, centerY - 10 - 2, x + width + 5 + 2, centerY - 10 + 9 + 2, ARGB.color(0.5F, CommonColors.BLACK));

                context.drawString(
                        textRenderer,
                        text,
                        x + 5,
                        centerY - 10,
                        CommonColors.WHITE
                );
            }
        }
    }

    private static int getXFromYaw(GuiGraphics context, double relativeYaw) {
        return Mth.ceil((context.guiWidth() - 9) / 2.0F) + (int)(relativeYaw * 173.0 / 2.0 / 60.0);
    }
}
