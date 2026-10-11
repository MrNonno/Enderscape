package net.penumbra.enderscape.mixin.server;

import com.google.common.collect.Iterables;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;
import net.penumbra.enderscape.entity.EnderscapeMobCapCalculator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheMixin {

    @Shadow
    @Final
    private ServerLevel level;

    @WrapOperation(
            method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/server/level/ChunkMap;)Lnet/minecraft/world/level/LocalMobCapCalculator;"
            )
    )
    private LocalMobCapCalculator Enderscape$replaceMobCapCalculator(ChunkMap chunkMap, Operation<LocalMobCapCalculator> original) {
        return EnderscapeMobCapCalculator.isEnd(level) ? new EnderscapeMobCapCalculator(chunkMap, level) : original.call(chunkMap);
    }

    @WrapOperation(
            method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/NaturalSpawner;getFilteredSpawningCategories(Lnet/minecraft/world/level/NaturalSpawner$SpawnState;ZZ)Ljava/util/List;"
            )
    )
    private List<MobCategory> Enderscape$useEnderscapeMobCap(NaturalSpawner.SpawnState state, boolean spawnEnemies, boolean spawnPersistent, Operation<List<MobCategory>> original) {
        List<MobCategory> categories = original.call(state, spawnEnemies, spawnPersistent);

        if (EnderscapeMobCapCalculator.isEnd(level) && spawnEnemies && !categories.contains(MobCategory.MONSTER) && EnderscapeMobCapCalculator.shouldSpawnMoreMonsters(level, state)) {
            List<MobCategory> raised = new ArrayList<>(categories);
            raised.add(MobCategory.MONSTER);

            return raised;
        } else {
            return categories;
        }
    }
}