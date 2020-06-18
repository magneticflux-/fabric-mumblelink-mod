package com.skaggsm.mumblelinkmod.mixin;

import com.skaggsm.mumblelinkmod.ServerOnChangeWorldCallback;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created by Mitchell Skaggs on 9/15/2019.
 */
@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity {
    @Inject(method = "changeDimension", at = @At(value = "RETURN"))
    private void onChangeDimension(ServerWorld serverWorld, CallbackInfoReturnable<Entity> cir) {
        ServerOnChangeWorldCallback.EVENT.invoker().onChangeDimension(serverWorld.getRegistryKey(), (ServerPlayerEntity) (Object) this);
    }
}
