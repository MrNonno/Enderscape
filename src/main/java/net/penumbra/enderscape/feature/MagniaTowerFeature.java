package net.penumbra.enderscape.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviders;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SpeleothemUtils;
import net.penumbra.enderscape.block.BlisteredMagniaBlock;
import net.penumbra.enderscape.block.HasMagniaPolarity;
import net.penumbra.enderscape.block.MagniaBlock;
import net.penumbra.enderscape.block.MagniaSproutBlock;
import net.penumbra.enderscape.block.properties.MagniaPolarity;
import net.penumbra.enderscape.block.state.StateProperties;
import net.penumbra.enderscape.registry.block.EnderscapeBlocks;
import net.penumbra.enderscape.registry.tag.EnderscapeBlockTags;

import java.util.List;
import java.util.Optional;

public record MagniaTowerFeature(FloatProvider floor_replacement_radius, IntProvider pillar_height, IntProvider sphere_radius, IntProvider ring_radius, FloatProvider alluring_magnia_sprout_placement_chance, FloatProvider repulsive_magnia_sprout_placement_chance) implements Feature {
    public static final MapCodec<MagniaTowerFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    FloatProviders.codec(1, 64).fieldOf("floor_replacement_radius").forGetter(config -> config.floor_replacement_radius),
                    IntProviders.codec(1, 64).fieldOf("height").forGetter(config -> config.pillar_height),
                    IntProviders.codec(0, 64).fieldOf("sphere_radius").forGetter(config -> config.sphere_radius),
                    IntProviders.codec(0, 64).fieldOf("ring_radius").forGetter(config -> config.ring_radius),
                    FloatProviders.codec(0, 1).fieldOf("alluring_magnia_sprout_placement_chance").forGetter(config -> config.alluring_magnia_sprout_placement_chance),
                    FloatProviders.codec(0, 1).fieldOf("repulsive_magnia_sprout_placement_chance").forGetter(config -> config.repulsive_magnia_sprout_placement_chance))
            .apply(instance, MagniaTowerFeature::new));

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        if (level.getBlockState(origin).isAir() && hasSolidBase(level, origin, 3)) {
            List<BlockPos> placedBlocks = Lists.newArrayList();

            replaceFloor(level, origin, random, placedBlocks);
            generateTower(level, origin, random, placedBlocks);
            generateSprouts(level, origin, random, placedBlocks);

            return true;
        }

        return false;
    }

    private void replaceFloor(WorldGenLevel level, BlockPos origin, RandomSource random, List<BlockPos> placedBlocks) {
        float radius = this.floor_replacement_radius().sample(random);

        for (float y = -radius; y <= radius; y++) {
            for (float x = -radius; x <= radius; x++) {
                for (float z = -radius; z <= radius; z++) {
                    BlockPos offset = origin.offset((int) x, (int) y, (int) z);
                    float distance = (float) Math.sqrt(x * x + y * y + z * z);

                    if (distance <= (radius + Mth.nextFloat(random, -2.5F, 0.5F)) && level.getBlockState(offset).is(EnderscapeBlockTags.MAGNIA_TOWER_REPLACEABLE)) {
                        level.setBlock(offset, EnderscapeBlocks.REPULSIVE_MAGNIA.defaultBlockState(), 2);
                        placedBlocks.add(offset);
                    }
                }
            }
        }
    }

    private void generateTower(WorldGenLevel level, BlockPos origin, RandomSource random, List<BlockPos> placedBlocks) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                boolean atEdgeX = x == -1 || x == 1;
                boolean atEdgeZ = z == -1 || z == 1;

                int height = getPillarHeight(random, x, z, atEdgeX, atEdgeZ);

                for (int y = 0; y < height; y++) {
                    BlockPos currentPos = origin.offset(x, y, z);

                    if (level.isStateAtPosition(currentPos, SpeleothemUtils::isEmptyOrWater)) {
                        level.setBlock(currentPos, EnderscapeBlocks.REPULSIVE_MAGNIA.defaultBlockState(), 2);
                        placedBlocks.add(currentPos);
                    }

                    if (x == 0 && z == 0) generateSphere(level, origin.above(height), placedBlocks, random);
                }
            }
        }
    }

    private void generateSphere(WorldGenLevel level, BlockPos origin, List<BlockPos> placedBlocks, RandomSource random) {
        int radius = this.sphere_radius().sample(random);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -radius; y <= radius; y++) {
                    BlockPos spherePos = origin.offset(x, y, z);
                    double sizeReduction = y == 2 ? 1.75 : 1.5;
                    double threshold = (radius * radius) / sizeReduction;

                    if (x * x + y * y + z * z <= threshold) {
                        level.setBlock(spherePos, EnderscapeBlocks.ALLURING_MAGNIA.defaultBlockState(), 2);
                        placedBlocks.add(spherePos);

                        if (y == 0) {
                            int ringRadius = this.ring_radius().sample(random);

                            generateRing(level, origin, placedBlocks, ringRadius, 1.5);
                            generateRing(level, origin, placedBlocks, (int) (ringRadius * 0.65F), 1.7);
                        }
                    }
                }
            }
        }
    }

    private void generateRing(WorldGenLevel level, BlockPos origin, List<BlockPos> placedBlocks, int radius, double thickness) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos offset = origin.offset(x, 0, z);

                if (x * x + z * z > radius * radius || x * x + z * z < (radius * radius) / thickness) continue;

                if (level.isStateAtPosition(offset, SpeleothemUtils::isEmptyOrWater)) {
                    level.setBlock(offset, EnderscapeBlocks.ALLURING_MAGNIA.defaultBlockState(), 2);
                    placedBlocks.add(offset);
                }
            }
        }
    }

    private void generateSprouts(WorldGenLevel level, BlockPos origin, RandomSource random, List<BlockPos> placedBlocks) {
        for (BlockPos pos : placedBlocks) {
            for (Direction direction : Direction.values()) {
                BlockState floor = level.getBlockState(pos);

                if (floor.getBlock() instanceof MagniaBlock) {
                    Optional<MagniaPolarity> type = HasMagniaPolarity.optional(floor);
                    if (type.isEmpty()) continue;

                    float chance = type.get() == MagniaPolarity.ALLURING ? this.alluring_magnia_sprout_placement_chance().sample(random) : this.repulsive_magnia_sprout_placement_chance().sample(random);

                    if (random.nextFloat() <= chance) {
                        BlockPos relative = pos.relative(direction);

                        if (level.getBlockState(relative).canBeReplaced() && level.getBlockState(pos.relative(direction, 2)).canBeReplaced()) {
                            double dx = pos.getX() - origin.getX();
                            double dz = pos.getZ() - origin.getZ();
                            boolean nearCenter = (dx * dx + dz * dz) <= 6 * 6;

                            if (random.nextInt(200) == 0 && nearCenter) {
                                BlockState state = EnderscapeBlocks.BLISTERED_MAGNIA.defaultBlockState().setValue(StateProperties.OPTIONAL_MAGNIA_POLARITY, BlisteredMagniaBlock.selectPolarity(level, relative));

                                level.setBlock(relative, state, 2);
                                level.scheduleTick(relative, state.getBlock(), 1);
                            } else {
                                Block sprout = type.get() == MagniaPolarity.ALLURING ? EnderscapeBlocks.ALLURING_MAGNIA_SPROUT : EnderscapeBlocks.REPULSIVE_MAGNIA_SPROUT;

                                level.setBlock(relative, sprout.defaultBlockState().setValue(MagniaSproutBlock.FACING, direction), 2);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean hasSolidBase(WorldGenLevel level, BlockPos origin, int radius) {
        boolean hasSolidGround = false;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (level.getBlockState(origin.offset(x, -1, z)).isAir()) return false;

                hasSolidGround = true;
            }
        }

        return hasSolidGround;
    }

    private int getPillarHeight(RandomSource random, int x, int z, boolean corner, boolean edge) {
        int height = this.pillar_height().sample(random);

        if (height <= this.pillar_height().maxInclusive() - 2 && random.nextInt(10) == 0) height *= 2;

        if (corner && edge) {
            height /= Mth.nextInt(random, 4, 6);
        } else if (!(x == 0 && z == 0)) {
            height /= Mth.nextInt(random, 2, 4);

            if (random.nextBoolean()) height += 3;
        }

        return height;
    }

    @Override
    public MapCodec<? extends Feature> codec() {
        return CODEC;
    }
}
