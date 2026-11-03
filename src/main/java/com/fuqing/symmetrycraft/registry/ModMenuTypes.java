package com.fuqing.symmetrycraft.registry;

import com.fuqing.symmetrycraft.SymmetryCraft;
import com.fuqing.symmetrycraft.inventory.SymmetryForgeMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, SymmetryCraft.MOD_ID);

    public static final RegistryObject<MenuType<SymmetryForgeMenu>> SYMMETRY_FORGE =
            MENU_TYPES.register("symmetry_forge", () -> IForgeMenuType.create(SymmetryForgeMenu::new));

    private ModMenuTypes() {
    }

    public static void register(IEventBus modBus) {
        MENU_TYPES.register(modBus);
    }
}
