package me.bbijabnpobatejb.chained.client.render;

import com.ibm.icu.impl.Pair;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class ChainRender {
    @Setter
    @Getter
    Set<Pair<Integer, Integer>> clientRenderChainedEntities = new HashSet<>();

    public void renderLeash(LivingEntity livingEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, Entity entity) {
        matrixStack.push();
        double d = MathHelper.lerp(f * 0.5f, entity.yaw, entity.prevYaw) * ((float) Math.PI / 180);
        double e = MathHelper.lerp(f * 0.5f, entity.pitch, entity.prevPitch) * ((float) Math.PI / 180);
        double g = Math.cos(d);
        double h = Math.sin(d);
        double i = Math.sin(e);
        if (entity instanceof AbstractDecorationEntity) {
            g = 0.0;
            h = 0.0;
            i = -1.0;
        }
        double j = Math.cos(e);
        double k = MathHelper.lerp((double) f, entity.prevX, entity.getX()) - g * 0.7 - h * 0.5 * j;
        double l = MathHelper.lerp((double) f, entity.prevY + (double) entity.getStandingEyeHeight() * 0.7, entity.getY() + (double) entity.getStandingEyeHeight() * 0.7) - i * 0.5 - 0.25;
        double m = MathHelper.lerp((double) f, entity.prevZ, entity.getZ()) - h * 0.7 + g * 0.5 * j;
        double n = (double) (MathHelper.lerp(f, ((LivingEntity) livingEntity).bodyYaw, (livingEntity).prevBodyYaw) * ((float) Math.PI / 180)) + 1.5707963267948966;
        Vec3d vec3d = ((Entity) livingEntity).method_29919();
        g = Math.cos(n) * vec3d.z + Math.sin(n) * vec3d.x;
        h = Math.sin(n) * vec3d.z - Math.cos(n) * vec3d.x;
        double o = MathHelper.lerp((double) f, livingEntity.prevX, ((Entity) livingEntity).getX()) + g;
        double p = MathHelper.lerp((double) f, livingEntity.prevY, ((Entity) livingEntity).getY()) + vec3d.y;
        double q = MathHelper.lerp((double) f, livingEntity.prevZ, ((Entity) livingEntity).getZ()) + h;
        matrixStack.translate(g, vec3d.y, h);
        float r = (float) (k - o);
        float s = (float) (l - p);
        float t = (float) (m - q);
        float u = 0.025f;
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getLeash());
        Matrix4f matrix4f = matrixStack.peek().getModel();
        float v = MathHelper.fastInverseSqrt(r * r + t * t) * 0.025f / 2.0f;
        float w = t * v;
        float x = r * v;
        BlockPos blockPos = new BlockPos(livingEntity.getCameraPosVec(f));
        BlockPos blockPos2 = new BlockPos(entity.getCameraPosVec(f));
        int y = getBlockLight(livingEntity, blockPos);
        int z = getBlockLight(entity, blockPos2);
        int aa = livingEntity.world.getLightLevel(LightType.SKY, blockPos);
        int ab = livingEntity.world.getLightLevel(LightType.SKY, blockPos2);
        MobEntityRenderer.method_23186(vertexConsumer, matrix4f, r, s, t, y, z, aa, ab, 0.025f, 0.025f, w, x);
        MobEntityRenderer.method_23186(vertexConsumer, matrix4f, r, s, t, y, z, aa, ab, 0.025f, 0.0f, w, x);
        matrixStack.pop();
    }

    int getBlockLight(Entity entity, BlockPos blockPos) {
        if (((Entity) entity).isOnFire()) {
            return 15;
        }
        return ((Entity) entity).world.getLightLevel(LightType.BLOCK, blockPos);
    }

}
