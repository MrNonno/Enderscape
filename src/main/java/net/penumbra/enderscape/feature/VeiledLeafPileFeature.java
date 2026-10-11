package net.penumbra.enderscape.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviders;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.penumbra.enderscape.block.VeiledLeafPileBlock;
import net.penumbra.enderscape.registry.block.EnderscapeBlocks;
import org.joml.SimplexNoise;

public record VeiledLeafPileFeature(FloatProvider radius, FloatProvider density, IntProvider layers) implements Feature {
    public static final MapCodec<VeiledLeafPileFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FloatProviders.codec(1, 64).fieldOf("radius").forGetter(config -> config.radius),
            FloatProviders.codec(0, 1).fieldOf("density").forGetter(config -> config.density),
            IntProviders.codec(1, 8).fieldOf("layers").forGetter(config -> config.layers)
    ).apply(instance, VeiledLeafPileFeature::new));

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {

        boolean result = false;

        float radius = this.radius().sample(random);
        float noiseScale = 0.1F;

        for (float x = -radius; x <= radius; x++) {
            for (float z = -radius; z <= radius; z++) {
                BlockPos offset = origin.offset((int) x, 0, (int) z);

                float noiseValue = SimplexNoise.noise(x * noiseScale, z * noiseScale);
                float distance = (float) Math.sqrt(x * x + z * z);

                if (distance <= radius * (0.8F + noiseValue * 0.4F) && random.nextFloat() > this.density().sample(random)) {
                    BlockPos.MutableBlockPos mutable = offset.above(3).mutable();

                    for (int i = 0; i < 6; i++) {
                        Block block = EnderscapeBlocks.VEILED_LEAF_PILE;
                        if (level.isEmptyBlock(mutable) && level.isEmptyBlock(mutable.above()) && VeiledLeafPileBlock.canSurvive(level, mutable, block)) {
                            level.setBlock(mutable, block.defaultBlockState().setValue(VeiledLeafPileBlock.LAYERS, this.layers().sample(random)), 2);
                            result = true;
                        }
                        mutable.move(Direction.DOWN);
                    }
                }
            }
        }

        return result;
    }

    @Override
    public MapCodec<? extends Feature> codec() {
        return CODEC;
    }
}