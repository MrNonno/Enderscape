package net.penumbra.enderscape.feature;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.penumbra.enderscape.block.MurublightBracketBlock;

import java.util.Optional;

public class MurublightBracketFeature implements Feature {
    public static final MapCodec<MurublightBracketFeature> CODEC = MapCodec.unit(MurublightBracketFeature::new);

    @Override
    public MapCodec<? extends Feature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        Optional<BlockPos> optional = level.findBlocksInBoxByManhattanDistance(origin, 8, 8).filterPos((pos) -> MurublightBracketBlock.canGenerate(level, pos, true)).findFirst();
        return optional.isPresent() && MurublightBracketBlock.generate(level, optional.get(), random);
    }
}