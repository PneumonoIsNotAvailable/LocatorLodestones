package net.pneumono.locator_lodestones.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.pneumono.locator_lodestones.config.ConfigManager;
import net.pneumono.locator_lodestones.config.DisplaySetting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Gui.class)
public abstract class InGameHudMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(
            method = "nextContextualInfoState",
            at = @At("HEAD"),
            cancellable = true
    )
    private void forceLocatorBarWhenNeeded(CallbackInfoReturnable<Gui.ContextualInfo> info) {
        if (ConfigManager.tabDisplaySetting() != DisplaySetting.TAB_FORCES) return;

        boolean canShow = ConfigManager.shouldShowInSpectator() || (minecraft.player != null && !minecraft.player.isSpectator());
        if (canShow && minecraft.options.keyPlayerList.isDown()) {
            info.setReturnValue(Gui.ContextualInfo.LOCATOR);
        }
    }

    @WrapOperation(
            method = "nextContextualInfoState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Gui;willPrioritizeExperienceInfo()Z"
            )
    )
    private boolean blockLocatorBarWhenNeeded(Gui instance, Operation<Boolean> original) {
        if (ConfigManager.tabDisplaySetting() == DisplaySetting.TAB_ONLY) {
            return !minecraft.options.keyPlayerList.isDown();
        } else {
            return original.call(instance);
        }
    }
}
