package net.pneumono.locator_lodestones.mixin;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.pneumono.locator_lodestones.WaypointTracking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public abstract class PlayerInventoryMixin {
    @Inject(
            method = "setChanged",
            at = @At("RETURN")
    )
    private void updateWaypoints(CallbackInfo ci) {
        WaypointTracking.markWaypointsDirty();
    }

    @Inject(
            method = "removeFromSelected",
            at = @At("RETURN")
    )
    private void updateWaypointsOnItemDrop(boolean entireStack, CallbackInfoReturnable<ItemStack> cir) {
        WaypointTracking.markWaypointsDirty();
    }
}
