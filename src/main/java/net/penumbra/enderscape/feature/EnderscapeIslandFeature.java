package net.penumbra.enderscape.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;


import java.util.ArrayList;
import java.util.List;

public class EnderscapeIslandFeature implements Feature {
    public static final MapCodec<EnderscapeIslandFeature> CODEC = MapCodec.unit(EnderscapeIslandFeature::new);

    @Override
    public MapCodec<? extends Feature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        float f = (float) random.nextInt(3) + 4.0F;

        List<BlockPos> list = new ArrayList<>();

        for (int i = 0; f > 0.5F; i--) {
            for (int j = Mth.floor(-f); j <= Mth.ceil(f); j++) {
                for (int k = Mth.floor(-f); k <= Mth.ceil(f); k++) {
                    if ((float) (j * j + k * k) <= (f + 1.0F) * (f + 1.0F)) {
                        list.add(origin.offset(j, i, k));
                    }
                }
            }

            f -= (float) random.nextInt(2) + 0.5F;
        }

        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;

        for (BlockPos pos : list) {
            setBlock(level, pos, Blocks.END_STONE.defaultBlockState());
            if (pos.getY() < lowest) lowest = pos.getY();
            if (pos.getY() > highest) highest = pos.getY();
        }

        return true;
    }
}