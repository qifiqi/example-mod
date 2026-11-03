package com.fuqing.symmetrycraft.client;

import com.fuqing.symmetrycraft.registry.ModMenuTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.client.gui.screens.MenuScreens;
import com.fuqing.symmetrycraft.SymmetryCraft;

/**
 * 客户端初始化：注册锻炉界面与优化器按键。
 */
@Mod.EventBusSubscriber(modid = SymmetryCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(ModMenuTypes.SYMMETRY_FORGE.get(), SymmetryForgeScreen::new);
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.OPEN_OPTIMIZER);
    }

    private ClientSetup() {
    }
}
