package net.penumbra.enderscape.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.BlockReplacement;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.penumbra.enderscape.registry.tag.EnderscapeBlockTags;
import org.joml.SimplexNoise;

import java.util.List;
import java.util.Optional;

public record VoidShaleFeature(List<BlockReplacement> targets, IntProvider maxTerrainDepth) implements Feature {
    public static final MapCodec<VoidShaleFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.list(BlockReplacement.CODEC).fieldOf("targets").forGetter(config -> config.targets),
                    (IntProviders.CODEC.fieldOf("terrain_depth")).forGetter(config -> config.maxTerrainDepth))
            .apply(instance, VoidShaleFeature::new));

    public static final int RADIUS = 8;
    public static final int HEIGHT = 3;

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        boolean bl = false;

        Optional<BlockPos> closest = level.findBlocksInBoxByManhattanDistance(origin, 4, 32).filterPos((pos) -> passesExtraConditions(level, pos, this.maxTerrainDepth().sample(random))).findFirst();

        if (closest.isPresent()) {
            BlockPos center = closest.get();
            for (BlockReplacement target : this.targets()) {
                if (target.target().test(level.getBlockState(center), center, random)) {

                    for (float x = -RADIUS; x <= RADIUS; x++) {
                        for (float z = -RADIUS; z <= RADIUS; z++) {
                            BlockPos offset = origin.offset((int) x, 0, (int) z);

                            float noiseValue = SimplexNoise.noise(x * 0.1F, z * 0.1F);
                            float distance = (float) Math.sqrt(x * x + z * z);

                            if (distance <= RADIUS * (0.8F + noiseValue * 0.4F)) {
                                BlockPos.MutableBlockPos mutable = offset.above(3).mutable();

                                for (int i = 0; i < 6; i++) {
                                    BlockState replaced = level.getBlockState(mutable);
                                    if (target.target().test(replaced, mutable, random)) {
                                        level.setBlock(mutable, target.state(), 2);
                                        bl = true;
                                    }
                                    mutable.move(Direction.DOWN);
                                }
                            }
                        }
                    }
                }
            }
        }

        return bl;
    }

    public static boolean passesExtraConditions(LevelAccessor level, BlockPos pos, int maxDepth) {
        if (!level.getBlockState(pos).is(EnderscapeBlockTags.ORE_REPLACEABLE)) return false;
        if (!level.isEmptyBlock(pos.above())) return false;
        return isWithinMaxDepth(level, pos, maxDepth) && countValidBlocks(level, pos);
    }

    public static boolean countValidBlocks(LevelAccessor level, BlockPos pos) {
        int count = 0;

        for (int y = -HEIGHT; y <= HEIGHT; y++) {
            for (int x = -RADIUS; x <= RADIUS; x++) {
                for (int z = -RADIUS; z <= RADIUS; z++) {
                    BlockPos offset = pos.offset(x, y, z);

                    if (level.getBlockState(offset).is(EnderscapeBlockTags.ORE_REPLACEABLE)) {
                        count++;
                    }
                }
            }
        }

        return count > 20;
    }

    public static boolean isWithinMaxDepth(LevelAccessor level, BlockPos pos, int maxDepth) {
        BlockPos.MutableBlockPos mutable = pos.mutable();

        for (int i = 0; i < maxDepth; i++) {
            mutable.move(Direction.DOWN);
            if (level.getBlockState(mutable).isAir()) return true;
        }

        return false;
    }

    @Override
    public MapCodec<? extends Feature> codec() {
        return CODEC;
    }
}