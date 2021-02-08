package com.skaggsm.mumblelinkmod.mixin;

import com.skaggsm.mumblelinkmod.ServerOnChangeWorldCallback;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created by Mitchell Skaggs on 9/15/2019.
 */
@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity {
    @Shadow @Final public MinecraftServer server;

    @Inject(method = "moveToWorld", at = @At(value = "RETURN"))
    private void onChangeDimension(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
        if (server.isDedicated())
            ServerOnChangeWorldCallback.EVENT.invoker().onChangeDimension(destination.getRegistryKey(), (ServerPlayerEntity) (Object) this);
    }
}
