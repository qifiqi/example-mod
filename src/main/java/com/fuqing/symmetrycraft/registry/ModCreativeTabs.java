package com.fuqing.symmetrycraft.registry;

import com.fuqing.symmetrycraft.SymmetryCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SymmetryCraft.MOD_ID);

    /** 独立创造模式标签页：本模组所有内容集中于此。 */
    public static final RegistryObject<CreativeModeTab> SYMMETRY_TAB = TABS.register("symmetrycraft",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.symmetrycraft"))
                    .icon(() -> new ItemStack(ModItems.SYMMETRY_CRYSTAL.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.SYMMETRY_CRYSTAL.get());
                        output.accept(ModItems.SYMMETRY_CRYSTAL_BLOCK_ITEM.get());
                        output.accept(ModItems.SYMMETRY_FORGE_ITEM.get());
                    })
                    .build());

    private ModCreativeTabs() {
    }

    public static void register(IEventBus modBus) {
        TABS.register(modBus);
    }
}
