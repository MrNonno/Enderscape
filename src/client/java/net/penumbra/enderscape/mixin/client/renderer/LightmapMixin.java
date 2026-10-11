package net.penumbra.enderscape.mixin.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.world.level.Level;
import net.penumbra.enderscape.config.EnderscapeConfig;
import net.penumbra.enderscape.registry.renderer.EnderscapeRenderPipelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Lightmap.class)
public class LightmapMixin {

    @Unique
    private static final EnderscapeConfig CONFIG = EnderscapeConfig.getInstance();

    @Unique
    private final Minecraft minecraft = Minecraft.getInstance();

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/renderpearl/api/commands/RenderPass;setPipeline(Lcom/mojang/renderpearl/api/pipeline/CompiledRenderPipeline;)V"))
    private CompiledRenderPipeline Enderscape$changeEndLightmap(CompiledRenderPipeline pipeline) {
        ClientLevel level = minecraft.level;

        if (level != null && level.dimension().equals(Level.END)) {
            return switch (CONFIG.lightingStyle) {
                case IMPROVED -> RenderSystem.getCompiledPipeline(EnderscapeRenderPipelines.IMPROVED_LIGHTMAP);
                case MIDNIGHT -> RenderSystem.getCompiledPipeline(EnderscapeRenderPipelines.MIDNIGHT_LIGHTMAP);
                default -> pipeline;
            };
        }

        return pipeline;
    }
}