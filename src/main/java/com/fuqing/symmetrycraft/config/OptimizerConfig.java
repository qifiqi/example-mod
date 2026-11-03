package com.fuqing.symmetrycraft.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * 针对本机的帧数优化配置。
 * 全部参数可通过游戏内「对称优化设置」界面（默认按 O）实时调整并保存。
 */
public final class OptimizerConfig {
    public static final ForgeConfigSpec SPEC;

    /** 粒子剔除距离（格）：超过该距离的粒子立即清除。 */
    public static final ForgeConfigSpec.IntValue PARTICLE_CULL_DISTANCE;
    /** 粒子总量上限：超过后自动丢弃最旧的粒子。 */
    public static final ForgeConfigSpec.IntValue MAX_PARTICLES;
    /** 生物渲染距离上限（格）：超出后跳过渲染，不占用GPU。 */
    public static final ForgeConfigSpec.IntValue MOB_RENDER_DISTANCE;
    /** 减弱雾效，远景更通透。 */
    public static final ForgeConfigSpec.BooleanValue REDUCE_FOG;
    /** 顶部居中对称 HUD：FPS / 粒子数。 */
    public static final ForgeConfigSpec.BooleanValue SHOW_HUD;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("fpsOptimizer");

        PARTICLE_CULL_DISTANCE = builder
                .comment("Particle cull distance in blocks (8~128). Particles beyond this are removed instantly.")
                .defineInRange("particleCullDistance", 32, 8, 128);
        MAX_PARTICLES = builder
                .comment("Max particles kept alive (256~8192). Oldest particles are dropped beyond the cap.")
                .defineInRange("maxParticles", 4096, 256, 8192);
        MOB_RENDER_DISTANCE = builder
                .comment("Living entity render distance cap in blocks (16~128).")
                .defineInRange("mobRenderDistance", 64, 16, 128);
        REDUCE_FOG = builder
                .comment("Push fog away for a clearer view.")
                .define("reduceFog", false);
        SHOW_HUD = builder
                .comment("Show the symmetric FPS/particle HUD at top center.")
                .define("showHud", true);

        builder.pop();
        SPEC = builder.build();
    }

    private OptimizerConfig() {
    }
}
