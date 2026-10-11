package net.penumbra.enderscape.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.penumbra.enderscape.block.AbstractVineBlock;
import net.penumbra.enderscape.block.state.StateProperties;
import net.penumbra.enderscape.registry.block.EnderscapeBlocks;
import net.penumbra.enderscape.registry.tag.EnderscapeBlockTags;
import net.penumbra.enderscape.util.BlockUtil;

import static net.penumbra.enderscape.util.BlockUtil.replace;
import static net.penumbra.enderscape.util.BlockUtil.set;

public record LargeCelestialChanterelleFeature(IntProvider height, float cap_radius_division,
                                               float cap_droop_percentage, float excess_vine_discard_chance,
                                               int vine_generation_tries, int tries) implements Feature {

    public static final MapCodec<LargeCelestialChanterelleFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    (IntProviders.codec(1, 64).fieldOf("height")).forGetter(config -> config.height),
                    (Codec.floatRange(1, 64).fieldOf("cap_radius_division")).forGetter(config -> config.cap_radius_division),
                    (Codec.floatRange(0, 1).fieldOf("cap_droop_percentage")).forGetter(config -> config.cap_droop_percentage),
                    (Codec.floatRange(0, 1).fieldOf("excess_vine_discard_chance")).forGetter(config -> config.excess_vine_discard_chance),
                    (Codec.intRange(0, 256).fieldOf("vine_generation_tries")).forGetter(config -> config.vine_generation_tries),
                    (Codec.intRange(1, 64).fieldOf("tries")).forGetter(config -> config.tries))
            .apply(instance, LargeCelestialChanterelleFeature::new));

    public static final LargeCelestialChanterelleFeature DEFAULT = new LargeCelestialChanterelleFeature(UniformInt.of(10, 35), 4, 0.75F, 1, 64, 16);


    private static final WeightedStateProvider PURUBERRIES = new WeightedStateProvider(WeightedList.<BlockState>builder()
            .add(EnderscapeBlocks.PURUBERRY_FLOWER.defaultBlockState(), 1)
            .add(EnderscapeBlocks.UNRIPE_PURUBERRY_BLOCK.defaultBlockState(), 1)
            .add(EnderscapeBlocks.RIPE_PURUBERRY_BLOCK.defaultBlockState(), 1)
    );


    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        Direction direction = Direction.UP;
        BlockPos attached = origin.relative(direction.getOpposite());

        if (level.getBlockState(attached).isFaceSturdy(level, attached, direction)) {
            for (int i = 0; i < this.tries(); i++) {
                int height = this.height().sample(random);
                int radius = (int) (height / this.cap_radius_division());

                if (isEnoughAir(level, origin, height, radius / 2)) {
                    return generate(level, origin, random,  height, radius);
                }
            }
        }

        return false;
    }

    private boolean generate(WorldGenLevel level, BlockPos pos, RandomSource random, int height, int radius) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);
        set(level, pos.below(), Blocks.END_STONE.defaultBlockState());
        generateCap(level, random, pos.above(height), radius);

        for (int i = 0; i < height; i++) {
            set(level, pos.above(i), EnderscapeBlocks.CELESTIAL_STEM.defaultBlockState());
        }

        return true;
    }

    private boolean isEnoughAir(WorldGenLevel level, BlockPos pos, int height, int radius) {
        for (int i = 1; i < height + 1; i++) {
            if (!level.isEmptyBlock(pos.above(i))) {
                return false;
            }
        }
        return true;
    }

    private void generateVines(WorldGenLevel level, RandomSource random, BlockPos origin, int radius) {
        int generatedVines = 0;
        int maxTries = this.vine_generation_tries();
        int maxRadius = (int) (radius * 0.8F);
        int minHeight = (int) (radius * 0.4F);

        for (int i = 0; i < maxTries; i++) {
            BlockPos pos = BlockUtil.random(origin, random, maxRadius, 0, maxRadius).below(Mth.nextInt(random, 0, minHeight));

            for (int offset = -4; offset < 4; offset++) {
                BlockPos testPos = pos.above(offset);
                if (level.getBlockState(testPos).isAir() && level.getBlockState(testPos.above()).is(EnderscapeBlockTags.SUPPORTS_PURUBERRY_VINE)) {
                    pos = testPos;
                    break;
                }
            }

            if (pos.closerThan(origin, (int) (radius * 0.6F)) || (generatedVines > radius / 4 && random.nextFloat() <= this.excess_vine_discard_chance())) {
                continue;
            }

            if (canVineGrow(level, pos)) {
                generateVine(level, random, pos, radius);
                generatedVines++;
            }
        }
    }

    private boolean canVineGrow(LevelAccessor level, BlockPos pos) {
        return level.isEmptyBlock(pos) && level.isEmptyBlock(pos.below()) && level.getBlockState(pos.above()).is(EnderscapeBlockTags.SUPPORTS_PURUBERRY_VINE);
    }

    private void generateVine(WorldGenLevel level, RandomSource random, BlockPos start, int radius) {
        int length = Mth.nextInt(random, (int) (radius * 0.8F), radius * 2);
        BlockPos.MutableBlockPos mutable = start.mutable();

        for (int i = 0; i <= length; i++) {
            if (level.isEmptyBlock(mutable)) {
                if (i == length || !level.isEmptyBlock(mutable.below())) {
                    replace(level, mutable, PURUBERRIES.getState(level, random, mutable));
                    break;
                } else {
                    replace(level, mutable, EnderscapeBlocks.PURUBERRY_VINE.defaultBlockState().setValue(StateProperties.ATTACHED, true).setValue(AbstractVineBlock.AGE, AbstractVineBlock.MAX_AGE));
                }

                mutable.move(Direction.DOWN);
            }
        }
    }

    private void generateCap(WorldGenLevel level, RandomSource random, BlockPos pos, int radius) {
        for (int x = -radius + 1; x < radius; x++) {
            for (int z = -radius + 1; z < radius; z++) {
                double distance = Math.sqrt(x * x + z * z);

                if (distance <= radius) {
                    for (int capOrder = 1; capOrder < radius; capOrder++) {
                        int droopAmount = distance >= radius * this.cap_droop_percentage() && capOrder == 1 && radius > 3 ? 1 : 0;
                        replace(level, pos.offset(x / capOrder, -(2 * capOrder + droopAmount) + 2, z / capOrder), EnderscapeBlocks.CELESTIAL_CAP.defaultBlockState());
                    }
                }
            }
        }

        generateVines(level, random, pos.below(), radius);
    }

    @Override
    public MapCodec<? extends Feature> codec() {
        return CODEC;
    }
}