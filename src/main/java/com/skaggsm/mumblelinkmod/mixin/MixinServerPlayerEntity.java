package com.skaggsm.mumblelinkmod.mixin;

import com.skaggsm.mumblelinkmod.ServerOnChangeDimensionCallback;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.dimension.DimensionType;
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
    private void onChangeDimension(DimensionType dimensionType_1, CallbackInfoReturnable<Entity> cir) {
        ServerOnChangeDimensionCallback.EVENT.invoker().onChangeDimension(dimensionType_1, (ServerPlayerEntity) (Object) this);
    }
}
