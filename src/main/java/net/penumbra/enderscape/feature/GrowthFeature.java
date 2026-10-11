package net.penumbra.enderscape.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.penumbra.enderscape.block.AbstractGrowthBlock;

public record GrowthFeature(BlockState state, IntProvider base_height, IntProvider added_height, float added_height_chance) implements Feature {
    public static final MapCodec<GrowthFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    (BlockState.CODEC.fieldOf("state")).forGetter(config -> config.state),
                    (IntProviders.codec(1, 64).fieldOf("base_height")).forGetter(config -> config.base_height),
                    (IntProviders.codec(0, 64).fieldOf("added_height")).forGetter(config -> config.added_height),
                    (Codec.floatRange(0, 1).fieldOf("added_height_chance")).forGetter(config -> config.added_height_chance))
            .apply(instance, GrowthFeature::new));

    public GrowthFeature(BlockState state, IntProvider height) {
        this(state, height, ConstantInt.ZERO, 0);
    }

    @Override
    public MapCodec<? extends Feature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        return AbstractGrowthBlock.generate(level, origin, random, this);
    }
}