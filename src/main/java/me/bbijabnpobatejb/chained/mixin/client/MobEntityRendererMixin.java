package me.bbijabnpobatejb.chained.mixin.client;

import com.ibm.icu.impl.Pair;
import lombok.val;
import me.bbijabnpobatejb.chained.client.render.ChainRender;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntityRenderer.class)
@SuppressWarnings("unchecked")
public abstract class MobEntityRendererMixin {

    @Shadow
    protected abstract <T extends MobEntity, E extends Entity> void method_4073(T mobEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, E entity);

    @Inject(
            method = "render(Lnet/minecraft/entity/mob/MobEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/mob/MobEntity;getHoldingEntity()Lnet/minecraft/entity/Entity;"
            ),
            cancellable = true)
    private void chained$beforeHoldingEntity(MobEntity mob, float f, float tickDelta, MatrixStack matrices,
                                             VertexConsumerProvider vertexConsumers, int light,
                                             CallbackInfo ci) {

        for (Pair<Integer, Integer> pair : ChainRender.getClientRenderChainedEntities()) {
            val first = pair.first;
            val second = pair.second;

            if (mob.getEntityId() != second) continue;

            val entityById = mob.world.getEntityById(first);
            if (entityById == null) continue;
            this.method_4073(mob, tickDelta, matrices, vertexConsumers, entityById);
            ci.cancel();
            break;
        }
    }
}