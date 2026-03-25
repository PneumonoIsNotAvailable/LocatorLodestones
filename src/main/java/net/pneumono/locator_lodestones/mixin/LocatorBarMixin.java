package net.pneumono.locator_lodestones.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.client.gui.contextualbar.LocatorBarRenderer;
import net.pneumono.locator_lodestones.WaypointNameRendering;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?} else {
/*import net.minecraft.client.gui.GuiGraphics;
*///?}

@Mixin(LocatorBarRenderer.class)
public abstract class LocatorBarMixin implements ContextualBarRenderer {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(
            method = /*? if >=26.1 {*/"extractRenderState"/*?} else {*//*"render"*//*?}*/,
            at = @At("RETURN")
    )
    private void renderClientWaypoints(/*? if >=26.1 {*/GuiGraphicsExtractor/*?} else {*//*GuiGraphics*//*?}*/ graphics, DeltaTracker tickCounter, CallbackInfo ci) {
        WaypointNameRendering.renderNames(this.minecraft, graphics, tickCounter, this.top(this.minecraft.getWindow()));
    }
}
