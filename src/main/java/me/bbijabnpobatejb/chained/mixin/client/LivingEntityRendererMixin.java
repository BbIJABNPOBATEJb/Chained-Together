package me.bbijabnpobatejb.chained.mixin.client;

import com.ibm.icu.impl.Pair;
import lombok.val;
import me.bbijabnpobatejb.chained.client.render.ChainRender;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> {

    @Inject(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "HEAD")
    )
    private void chained$onRender(T target, float yaw, float tickDelta, MatrixStack matrices,
                                  VertexConsumerProvider vertexConsumers, int light,
                                  CallbackInfo ci) {

        for (Pair<Integer, Integer> pair : ChainRender.getClientRenderChainedEntities()) {
            val first = pair.first;
            val second = pair.second;

            val entityId = target.getEntityId();
            if (entityId != second) continue;

            val owner = target.world.getEntityById(first);
            if (owner == null) continue;
            ChainRender.renderLeash(target, tickDelta, matrices, vertexConsumers, owner);
            break;
        }
    }
}