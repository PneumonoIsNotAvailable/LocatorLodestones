package net.pneumono.locator_lodestones.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.pneumono.locator_lodestones.config.ConfigManager;
import net.pneumono.locator_lodestones.config.DisplaySetting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if >=26.2 {
import net.minecraft.client.gui.Hud;
//?} else {
/*import net.minecraft.client.gui.Gui;
*///?}

@Mixin(/*? if >=26.2 {*/Hud/*?} else {*//*Gui*//*?}*/.class)
public abstract class HudMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(
            method = "nextContextualInfoState",
            at = @At("HEAD"),
            cancellable = true
    )
    private void forceLocatorBarWhenNeeded(CallbackInfoReturnable</*? if >=26.2 {*/Hud/*?} else {*//*Gui*//*?}*/.ContextualInfo> info) {
        if (ConfigManager.tabDisplaySetting() != DisplaySetting.TAB_FORCES) return;

        boolean canShow = ConfigManager.shouldShowInSpectator() || (minecraft.player != null && !minecraft.player.isSpectator());
        if (canShow && minecraft.options.keyPlayerList.isDown()) {
            info.setReturnValue(/*? if >=26.2 {*/Hud/*?} else {*//*Gui*//*?}*/.ContextualInfo.LOCATOR);
        }
    }

    @WrapOperation(
            method = "nextContextualInfoState",
            at = @At(
                    value = "INVOKE",
                    //? if >=26.2 {
                    target = "Lnet/minecraft/client/gui/Hud;willPrioritizeExperienceInfo()Z"
                    //?} else {
                    /*target = "Lnet/minecraft/client/gui/Gui;willPrioritizeExperienceInfo()Z"
                    *///?}
            )
    )
    private boolean blockLocatorBarWhenNeeded(/*? if >=26.2 {*/Hud/*?} else {*//*Gui*//*?}*/ instance, Operation<Boolean> original) {
        if (ConfigManager.tabDisplaySetting() == DisplaySetting.TAB_ONLY) {
            return !minecraft.options.keyPlayerList.isDown();
        } else {
            return original.call(instance);
        }
    }
}
