package net.penumbra.enderscape.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.BlockReplacement;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.List;

public record ScatteredOreFeature(List<BlockReplacement> targets, IntProvider tries, IntProvider scatter_increase, IntProvider size) implements Feature {
    public static final MapCodec<ScatteredOreFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.list(BlockReplacement.CODEC).fieldOf("targets").forGetter(config -> config.targets),
                    IntProviders.codec(1, 200).fieldOf("tries").forGetter(config -> config.tries),
                    IntProviders.codec(-5, 5).fieldOf("scatter_increase").forGetter(config -> config.scatter_increase),
                    IntProviders.codec(0, 5).fieldOf("size").forGetter(config -> config.size))
            .apply(instance, ScatteredOreFeature::new));

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        ChunkPos center = ChunkPos.containing(origin);
        BlockPos next = origin;

        int size = this.size().sample(random) / 2;
        boolean bl = false;

        for (int i = 0; i < this.tries().sample(random); i++) {
            for (BlockPos pos : BlockPos.withinBoxByManhattanDistance(next, size, size, size)) {
                if (!canWrite(center, pos)) continue;

                for (BlockReplacement target : this.targets()) {
                    if (target.target().test(level.getBlockState(pos), pos, random)) {
                        level.setBlock(pos, target.state(), 2);
                        bl = true;
                    }
                }
            }

            next = next.offset(this.scatter_increase().sample(random), this.scatter_increase().sample(random), this.scatter_increase().sample(random));
        }

        return bl;
    }

    private boolean canWrite(ChunkPos center, BlockPos pos) {
        int distanceX = Math.abs(center.x() - SectionPos.blockToSectionCoord(pos.getX()));
        int distanceZ = Math.abs(center.z() - SectionPos.blockToSectionCoord(pos.getZ()));

        return distanceX <= 1 && distanceZ <= 1;
    }

    @Override
    public MapCodec<? extends Feature> codec() {
        return CODEC;
    }
}