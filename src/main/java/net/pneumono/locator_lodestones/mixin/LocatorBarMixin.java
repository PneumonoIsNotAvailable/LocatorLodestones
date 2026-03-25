package net.pneumono.locator_lodestones.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.client.gui.contextualbar.LocatorBarRenderer;
import net.pneumono.locator_lodestones.WaypointNameRendering;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocatorBarRenderer.class)
public abstract class LocatorBarMixin implements ContextualBarRenderer {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(
            method = "render",
            at = @At("RETURN")
    )
    private void renderClientWaypoints(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        WaypointNameRendering.renderNames(this.minecraft, context, tickCounter, this.top(this.minecraft.getWindow()));
    }
}
