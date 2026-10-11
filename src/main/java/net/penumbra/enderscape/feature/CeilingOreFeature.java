package net.penumbra.enderscape.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.BlockReplacement;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.penumbra.enderscape.registry.tag.EnderscapeBlockTags;
import net.penumbra.enderscape.util.BlockUtil;

import java.util.List;
import java.util.Optional;

public record CeilingOreFeature(List<BlockReplacement> targets, IntProvider terrainDepth) implements Feature {
    public static final MapCodec<CeilingOreFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.list(BlockReplacement.CODEC).fieldOf("targets").forGetter(config -> config.targets),
                    (IntProviders.CODEC.fieldOf("terrain_depth")).forGetter(config -> config.terrainDepth))
            .apply(instance, CeilingOreFeature::new));

    @Override
    public MapCodec<? extends Feature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        boolean generated = false;

        Optional<BlockPos> closest = level.findBlocksInBoxByManhattanDistance(origin, 4, 32).filterPos((pos) -> level.getBlockState(pos).is(EnderscapeBlockTags.ORE_REPLACEABLE) && level.isEmptyBlock(pos.below()) && BlockUtil.hasTerrainDepth(level, pos, this.terrainDepth().sample(random), Direction.UP)).findFirst();

        if (closest.isPresent()) {
            for (BlockReplacement target : this.targets()) {
                if (target.target().test(level.getBlockState(closest.get()), closest.get(), random)) {
                    level.setBlock(closest.get(), target.state(), 2);
                    generated = true;
                }
            }
        }

        return generated;
    }
}