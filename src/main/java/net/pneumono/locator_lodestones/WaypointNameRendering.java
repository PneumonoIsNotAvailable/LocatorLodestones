package net.pneumono.locator_lodestones;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.waypoints.TrackedWaypoint;
import net.pneumono.locator_lodestones.config.ConfigManager;

import java.util.Optional;

//? if >=26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?} else {
/*import net.minecraft.client.gui.GuiGraphics;
*///?}

//? if >=1.21.9 {
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.waypoints.PartialTickSupplier;
//?}

public class WaypointNameRendering {
    public static void renderNames(Minecraft client, /*? if >=26.1 {*/GuiGraphicsExtractor/*?} else {*//*GuiGraphics*//*?}*/ graphics, DeltaTracker tickCounter, int centerY) {
        if (!ConfigManager.tabShowsNames() || !client.options.keyPlayerList.isDown()) return;

        //? if >=1.21.9 {
        Entity camera = client.getCameraEntity();
        if (camera == null) return;
        Level world = camera.level();
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
                Component component = textOptional.get();
                Font font = client.font;

                int width = font.width(component);
                int x = getXFromYaw(graphics, bestYaw) - width / 2;

                graphics.fill(x + 5 - 2, centerY - 10 - 2, x + width + 5 + 2, centerY - 10 + 9 + 2, ARGB.color(0.5F, CommonColors.BLACK));

                graphics./*? if >=26.1 {*/text/*?} else {*//*drawString*//*?}*/(
                        font,
                        component,
                        x + 5,
                        centerY - 10,
                        CommonColors.WHITE
                );
            }
        }
    }

    private static int getXFromYaw(/*? if >=26.1 {*/GuiGraphicsExtractor/*?} else {*//*GuiGraphics*//*?}*/ graphics, double relativeYaw) {
        return Mth.ceil((graphics.guiWidth() - 9) / 2.0F) + (int)(relativeYaw * 173.0 / 2.0 / 60.0);
    }
}
