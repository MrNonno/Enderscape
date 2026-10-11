package net.penumbra.enderscape.mixin.level;

import net.minecraft.world.level.pathfinder.PathType;
import net.penumbra.enderscape.entity.ai.EnderscapePathTypes;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(PathType.class)
public enum PathTypeMixin {
    ENDERSCAPE_VOID_FIRE(MixinIntrinsics.currentEnumOrdinal(), -1.0F),
    ENDERSCAPE_VOID_LACHRYMA(MixinIntrinsics.currentEnumOrdinal(), -1.0F),
    ENDERSCAPE_VOID_SHALE(MixinIntrinsics.currentEnumOrdinal(), 8.0F);
    PathTypeMixin(int id, final float defaultCost) {}
}