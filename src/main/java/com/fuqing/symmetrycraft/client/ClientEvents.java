package com.fuqing.symmetrycraft.client;

import com.fuqing.symmetrycraft.SymmetryCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 客户端运行时事件：优化器节拍、渲染裁剪、雾效、HUD。
 */
@Mod.EventBusSubscriber(modid = SymmetryCraft.MOD_ID, value = Dist.CLIENT)
public final class ClientEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        while (KeyBindings.OPEN_OPTIMIZER.consumeClick()) {
            if (mc.screen == null && mc.player != null) {
                mc.setScreen(new OptimizerScreen());
            }
        }
        FpsOptimizer.tick(mc);
    }

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
        FpsOptimizer.cullEntityRender(event);
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        FpsOptimizer.adjustFog(event);
    }

    @SubscribeEvent
    public static void onHudOverlay(RenderGuiOverlayEvent.Post event) {
        SymmetryHud.render(event);
    }

    private ClientEvents() {
    }
}
