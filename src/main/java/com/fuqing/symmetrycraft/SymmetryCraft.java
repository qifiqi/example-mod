package com.fuqing.symmetrycraft;

import com.fuqing.symmetrycraft.config.OptimizerConfig;
import com.fuqing.symmetrycraft.registry.ModBlockEntities;
import com.fuqing.symmetrycraft.registry.ModBlocks;
import com.fuqing.symmetrycraft.registry.ModCreativeTabs;
import com.fuqing.symmetrycraft.registry.ModItems;
import com.fuqing.symmetrycraft.registry.ModMenuTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 对称工艺 SymmetryCraft
 *
 * 独立材质 · 独立UI · 针对本机帧数优化
 * 一切内容围绕中轴对称：对称水晶、对称锻炉、对称界面、对称HUD。
 */
@Mod(SymmetryCraft.MOD_ID)
public class SymmetryCraft {
    public static final String MOD_ID = "symmetrycraft";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public SymmetryCraft() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(modBus);
        ModItems.register(modBus);
        ModBlockEntities.register(modBus);
        ModMenuTypes.register(modBus);
        ModCreativeTabs.register(modBus);

        // 客户端帧数优化配置（独立配置文件，改起来不碰其他模组）
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, OptimizerConfig.SPEC, "symmetrycraft-optimizer.toml");

        LOGGER.info("[对称工艺] 已装载 —— 左右对称，帧数安定。");
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
