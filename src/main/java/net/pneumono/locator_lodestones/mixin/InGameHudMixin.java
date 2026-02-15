package net.pneumono.locator_lodestones.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.pneumono.locator_lodestones.config.ConfigManager;
import net.pneumono.locator_lodestones.config.DisplaySetting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(
            method = "getCurrentBarType",
            at = @At("HEAD"),
            cancellable = true
    )
    private void forceLocatorBarWhenNeeded(CallbackInfoReturnable<InGameHud.BarType> info) {
        if (ConfigManager.tabDisplaySetting() != DisplaySetting.TAB_FORCES) return;

        boolean canShow = ConfigManager.shouldShowInSpectator() || (client.player != null && !client.player.isSpectator());
        if (canShow && client.options.playerListKey.isPressed()) {
            info.setReturnValue(InGameHud.BarType.LOCATOR);
        }
    }

    @WrapOperation(
            method = "getCurrentBarType",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;shouldShowExperienceBar()Z"
            )
    )
    private boolean blockLocatorBarWhenNeeded(InGameHud instance, Operation<Boolean> original) {
        if (ConfigManager.tabDisplaySetting() == DisplaySetting.TAB_ONLY) {
            return !client.options.playerListKey.isPressed();
        } else {
            return original.call(instance);
        }
    }
}
