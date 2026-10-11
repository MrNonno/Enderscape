package net.penumbra.enderscape.registry.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Interval;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.densityfunction.*;
import net.penumbra.enderscape.Enderscape;
import org.jspecify.annotations.NonNull;

public class EnderscapeDensityFunctionTypes {

    static final Codec<Float> NOISE_VALUE_CODEC = Codec.floatRange(-1000000.0f, 1000000.0f);

    record DistanceFromCenterGradient(int minDistance, int maxDistance, Interval range) implements DensityFunction {
        public static final MapCodec<DistanceFromCenterGradient> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Codec.INT.fieldOf("min_distance").forGetter(DistanceFromCenterGradient::minDistance),
                                Codec.INT.fieldOf("max_distance").forGetter(DistanceFromCenterGradient::maxDistance),
                                RecordCodecBuilder.<Interval>mapCodec(instance1 -> instance1.group(
                                        NOISE_VALUE_CODEC.fieldOf("from_value").forGetter(Interval::min),
                                        NOISE_VALUE_CODEC.fieldOf("to_value").forGetter(Interval::max)
                                ).apply(instance1, Interval::of)).forGetter(DistanceFromCenterGradient::range)
                        )
                        .apply(instance, DistanceFromCenterGradient::new)
        );

        @Override
        public @NonNull DensitySampler compileSampler(CompileContext context) {
            return new DensitySampler() {
                @Override
                public void sampleVolume(SamplerContext context, DensityBuffer outputBuffer, DensityVolume volume) {
                    DensitySampler.sampleVolumeNaive(context, outputBuffer, volume, this);
                }

                @Override
                public float sampleValue(SamplerContext context, int blockX, int blockY, int blockZ) {
                    float distance = Mth.sqrt(blockX * blockX + blockZ * blockZ);

                    return Mth.clampedMap(distance, minDistance, maxDistance, range.min(), range.max());
                }
            };
        }

        @Override
        public DensityFunction rewriteChildren(DfRewriteRule rule) {
            return this;
        }

        @Override
        public @Axes int domainAxes() {
            return ALL_AXES;
        }

        @Override
        public MapCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    private static MapCodec<? extends DensityFunction> register(String string, MapCodec<? extends DensityFunction> dispatch) {
        return Registry.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE, Enderscape.id(string), dispatch);
    }

    static {
        register("distance_from_center_gradient", DistanceFromCenterGradient.CODEC);
    }
}