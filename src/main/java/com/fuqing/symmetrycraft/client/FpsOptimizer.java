package com.fuqing.symmetrycraft.client;

import com.fuqing.symmetrycraft.config.OptimizerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.ViewportEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

/**
 * 帧数优化器：三项针对本机的实时优化 + 统计。
 *
 * 1. 粒子剔除：每刻清除超出距离的粒子，并限制总量（最旧的先丢弃）。
 * 2. 生物渲染裁剪：超出配置距离的生物直接跳过渲染，不占 GPU。
 * 3. 雾效减弱：把雾面向远处推移（可选）。
 *
 * 粒子表通过 accesstransformer.cfg 公开，无 Mixin，稳定兼容。
 */
public final class FpsOptimizer {
    private static int liveParticles;
    private static long culledParticles;

    public static int getLiveParticles() {
        return liveParticles;
    }

    public static long getCulledParticles() {
        return culledParticles;
    }

    public static void tick(Minecraft mc) {
        if (mc.player == null || mc.level == null) {
            liveParticles = 0;
            return;
        }

        int cullDistance = OptimizerConfig.PARTICLE_CULL_DISTANCE.get();
        int maxParticles = OptimizerConfig.MAX_PARTICLES.get();
        double limitSquared = (double) cullDistance * cullDistance;
        Vec3 anchor = mc.player.position();

        Map<ParticleRenderType, Queue<Particle>> particles = mc.particleEngine.particles;
        int removed = 0;
        int live = 0;

        for (Queue<Particle> queue : particles.values()) {
            Iterator<Particle> iterator = queue.iterator();
            while (iterator.hasNext()) {
                Particle particle = iterator.next();
                if (particle.getPos().distanceToSqr(anchor) > limitSquared) {
                    iterator.remove();
                    removed++;
                } else {
                    live++;
                }
            }
        }

        int overflow = live - maxParticles;
        if (overflow > 0) {
            for (Queue<Particle> queue : particles.values()) {
                while (overflow > 0 && !queue.isEmpty()) {
                    queue.poll();
                    removed++;
                    live--;
                    overflow--;
                }
                if (overflow <= 0) {
                    break;
                }
            }
        }

        culledParticles += removed;
        liveParticles = live;
    }

    public static void cullEntityRender(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity == mc.player) {
            return;
        }
        int distance = OptimizerConfig.MOB_RENDER_DISTANCE.get();
        if (entity.distanceToSqr(mc.player) > (double) distance * distance) {
            event.setCanceled(true);
        }
    }

    public static void adjustFog(ViewportEvent.RenderFog event) {
        if (!OptimizerConfig.REDUCE_FOG.get()) {
            return;
        }
        event.setNearPlaneDistance(event.getNearPlaneDistance() * 1.5F);
        event.setFarPlaneDistance(event.getFarPlaneDistance() * 1.5F);
    }

    private FpsOptimizer() {
    }
}
