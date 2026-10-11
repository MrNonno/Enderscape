package net.penumbra.enderscape.registry.level;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.penumbra.enderscape.Enderscape;

import java.util.ArrayList;
import java.util.List;

public class EnderscapeNoiseParameters {

    public static final List<ResourceKey<NormalNoise>> NOISE_PARAMETERS = new ArrayList<>();

    public static final ResourceKey<NormalNoise> CELESTIAL_SURFACE = createKey("celestial_surface");
    public static final ResourceKey<NormalNoise> CORRUPTION_CEILING = createKey("corruption_ceiling");
    public static final ResourceKey<NormalNoise> VEILED_SURFACE = createKey("veiled_surface");

    private static ResourceKey<NormalNoise> createKey(String string) {
        ResourceKey<NormalNoise> key = ResourceKey.create(Registries.NOISE, Enderscape.id(string));
        NOISE_PARAMETERS.add(key);
        return key;
    }

    public static void bootstrap(BootstrapContext<NormalNoise> context) {
        register(context, CELESTIAL_SURFACE, NormalNoise.createParity(-4, 1.0D, 0.5D, 1.0D));
        register(context, CORRUPTION_CEILING, NormalNoise.createParity(-4, 1.0D, 0.5D, 1.0D));
        register(context, VEILED_SURFACE, NormalNoise.createParity(-5, 1.0D, 0.5D, 1.0D));
    }

    private static void register(BootstrapContext<NormalNoise> context, ResourceKey<NormalNoise> key, NormalNoise object) {
        context.register(key, object);
    }
}
