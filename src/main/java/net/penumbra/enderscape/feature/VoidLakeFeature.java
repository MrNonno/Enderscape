package net.penumbra.enderscape.feature;

import com.google.common.base.Predicate;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.*;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.penumbra.enderscape.registry.block.EnderscapeBlocks;

import java.util.ArrayList;
import java.util.List;

public record VoidLakeFeature(
        Holder<BlockStateProvider> fluid,
        Holder<BlockStateProvider> barrier,
        IntProvider size,
        IntProvider barrierRadius,
        IntProvider barrierPillarHeight,
        FloatProvider barrierPlacementChance
) implements Feature {
    public static final MapCodec<VoidLakeFeature> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    BlockStateProvider.CODEC.fieldOf("fluid").forGetter(VoidLakeFeature::fluid),
                    BlockStateProvider.CODEC.fieldOf("barrier").forGetter(VoidLakeFeature::barrier),
                    IntProviders.CODEC.fieldOf("size").forGetter(VoidLakeFeature::size),
                    IntProviders.CODEC.fieldOf("barrier_radius").forGetter(VoidLakeFeature::barrierRadius),
                    IntProviders.CODEC.fieldOf("barrier_pillar_height").forGetter(VoidLakeFeature::barrierPillarHeight),
                    FloatProviders.CODEC.fieldOf("barrier_placement_chance").forGetter(VoidLakeFeature::barrierPlacementChance)
            ).apply(instance, VoidLakeFeature::new)
    );

    public static final VoidLakeFeature DEFAULT = new VoidLakeFeature(
            BlockStateProvider.holderOf(EnderscapeBlocks.VOID_LACHRYMA.defaultBlockState()),
            BlockStateProvider.holderOf(EnderscapeBlocks.VOID_SHALE.defaultBlockState()),
            UniformInt.of(5, 9),
            ConstantInt.of(4),
            TrapezoidInt.of(0, 4, 0),
            UniformFloat.of(0.25F, 0.5F)
    );

    private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {

        if (origin.getY() <= level.getMinY() + 4) {
            return false;
        } else {
            var newOrigin = origin.offset(-8, -4, -8);
            boolean[] grid = makeGrid(level, chunkGenerator, random, origin);

            if (canPlace(origin, level, chunkGenerator, random, origin, grid)) {
                placeFluidOrAir(newOrigin, level, chunkGenerator, random, origin, grid);
                placeBarrier(newOrigin, level, chunkGenerator, random, origin, grid);

                return true;
            } else {
                return false;
            }
        }
    }

    private boolean [] makeGrid(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        boolean[] grid = new boolean[2048];
        int size = this.size().sample(random);

        for (int i = 0; i < size; i++) {
            double xr = random.nextDouble() * 6.0 + 3.0;
            double yr = random.nextDouble() * 4.0 + 2.0;
            double zr = random.nextDouble() * 6.0 + 3.0;
            double xp = random.nextDouble() * (16.0 - xr - 2.0) + 1.0 + xr / 2.0;
            double yp = random.nextDouble() * (8.0 - yr - 4.0) + 2.0 + yr / 2.0;
            double zp = random.nextDouble() * (16.0 - zr - 2.0) + 1.0 + zr / 2.0;

            for (int x = 1; x < 15; x++) {
                for (int z = 1; z < 15; z++) {
                    for (int y = 1; y < 7; y++) {
                        double xd = (x - xp) / (xr / 2.0);
                        double yd = (y - yp) / (yr / 2.0);
                        double zd = (z - zp) / (zr / 2.0);

                        double distance = xd * xd + yd * yd + zd * zd;

                        if (distance < 1.0) {
                            grid[(x * 16 + z) * 8 + y] = true;
                        }
                    }
                }
            }
        }
        return grid;
    }

    private boolean canPlace(BlockPos origin, WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos featureOrigin, boolean[] grid) {
        BlockState fluid = this.fluid().value().getState(level, random, origin);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 8; y++) {
                    boolean check = !grid[(x * 16 + z) * 8 + y] && (x < 15 && grid[((x + 1) * 16 + z) * 8 + y]
                                    || x > 0 && grid[((x - 1) * 16 + z) * 8 + y]
                                    || z < 15 && grid[(x * 16 + z + 1) * 8 + y]
                                    || z > 0 && grid[(x * 16 + (z - 1)) * 8 + y]
                                    || y < 7 && grid[(x * 16 + z) * 8 + y + 1]
                                    || y > 0 && grid[(x * 16 + z) * 8 + (y - 1)]
                    );
                    if (check) {
                        BlockState state = level.getBlockState(origin.offset(x, y, z));

                        if (y >= 4 && state.liquid()) {
                            return false;
                        }

                        if (y < 4 && !state.isSolid() && level.getBlockState(origin.offset(x, y, z)) != fluid) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private void placeFluidOrAir(BlockPos origin, WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos baseOrigin, boolean[] grid) {
        BlockState fluid = this.fluid().value().getState(level, random, origin);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 8; y++) {
                    if (grid[(x * 16 + z) * 8 + y]) {
                        BlockPos placePos = origin.offset(x, y, z);

                        if (canReplaceBlock(level.getBlockState(placePos))) {
                            boolean placeAir = y >= 4;
                            level.setBlock(placePos, placeAir ? AIR : fluid, 2);

                            if (placeAir) {
                                level.scheduleTick(placePos, AIR.getBlock(), 0);
                                markAboveForPostProcessing(level, placePos);
                            }
                        }
                    }
                }
            }
        }
    }

    private void placeBarrier(BlockPos origin, WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos baseOrigin, boolean[] grid) {
        List<BarrierPart> parts = new ArrayList<>();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 8; y++) {
                    boolean check = !grid[(x * 16 + z) * 8 + y]
                            && (
                            x < 15 && grid[((x + 1) * 16 + z) * 8 + y]
                                    || x > 0 && grid[((x - 1) * 16 + z) * 8 + y]
                                    || z < 15 && grid[(x * 16 + z + 1) * 8 + y]
                                    || z > 0 && grid[(x * 16 + (z - 1)) * 8 + y]
                                    || y < 7 && grid[(x * 16 + z) * 8 + y + 1]
                                    || y > 0 && grid[(x * 16 + z) * 8 + (y - 1)]
                    );
                    if (check && (y < 4 || random.nextInt(2) != 0)) {
                        int radius = this.barrierRadius().sample(random);

                        for (int xr = -radius; xr <= radius; xr++) {
                            for (int zr = -radius; zr <= radius; zr++) {
                                BlockPos offset = origin.offset(x + xr, y, z + zr);
                                float distance = Mth.sqrt(xr * xr + zr * zr);

                                if (distance <= radius && random.nextFloat() <= this.barrierPlacementChance().sample(random) && placeBarrierBlock(level, offset, this.barrier().value().getState(level, random, origin), (state) -> state.isSolid())) {
                                    parts.add(new BarrierPart(offset, radius, distance));
                                }
                            }
                        }
                    }
                }
            }
        }

        placeBarrierPillars(level, chunkGenerator, random, baseOrigin, parts);
    }

    private void placeBarrierPillars(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin, List<BarrierPart> parts) {
        parts.forEach((part) -> {
            BlockPos position = part.position();

            float radius = part.radius();
            float distance = part.distance();

            double normalized = distance / (radius * Mth.sqrt(2));
            double multiplier = Mth.cos(normalized * Mth.HALF_PI);

            int rimHeight = (int) (this.barrierPillarHeight().sample(random) * multiplier);

            for (int i = 1; i < rimHeight; i++) {
                BlockPos above = position.above(i);

                if (!placeBarrierBlock(level, above, this.barrier().value().getState(level, random, above), (state) -> !state.isSolid())) {
                    break;
                }
            }
        });
    }

    private boolean placeBarrierBlock(WorldGenLevel level, BlockPos offset, BlockState barrier, Predicate<BlockState> predicate) {
        BlockState offsetState = level.getBlockState(offset);

        if (predicate.test(offsetState) && level.getFluidState(offset).isEmpty() && !offsetState.is(BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE)) {
            level.setBlock(offset, barrier, 2);
            markAboveForPostProcessing(level, offset);
            return true;
        } else {
            return false;
        }
    }

    private boolean canReplaceBlock(final BlockState state) {
        return !state.is(BlockTags.FEATURES_CANNOT_REPLACE);
    }

    @Override
    public MapCodec<? extends Feature> codec() {
        return CODEC;
    }

    private record BarrierPart(BlockPos position, int radius, float distance) {
    }
}
