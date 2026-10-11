package net.penumbra.enderscape.mixin.server;

import com.google.common.collect.Iterables;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.NaturalSpawner;
import net.penumbra.enderscape.entity.EnderscapeMobCapCalculator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NaturalSpawner.class)
public class NaturalSpawnerMixin {

    @ModifyExpressionValue(
            method = "createState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getAllEntities()Ljava/lang/Iterable;"
            )
    )
    private static Iterable<Entity> Enderscape$onlyUseTickingEntities(Iterable<Entity> entities, @Local(argsOnly = true) ServerLevel level) {
        if (EnderscapeMobCapCalculator.isEnd(level)) {
            return Iterables.filter(entities, entity -> ((ServerChunkCacheAccessor) level.getChunkSource()).Enderscape$getDistanceManager().inEntityTickingRange(entity.chunkPosition().pack()));
        } else {
            return entities;
        }
    }
}
