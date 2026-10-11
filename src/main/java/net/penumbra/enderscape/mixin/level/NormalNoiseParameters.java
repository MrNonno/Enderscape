package net.penumbra.enderscape.mixin.level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "net/minecraft/world/level/levelgen/synth/NormalNoise$Parameters")
public class NormalNoiseParameters {
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;doubleRange(DD)Lcom/mojang/serialization/Codec;"), index = 0)
    private static double Enderscape$allowNegativeAmplitudes(double original) {
        return Math.min(original, -32);
    }
}
