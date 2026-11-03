package com.fuqing.symmetrycraft.registry;

import com.fuqing.symmetrycraft.SymmetryCraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, SymmetryCraft.MOD_ID);

    /** 对称水晶：镜像锻炉的催化核心。 */
    public static final RegistryObject<Item> SYMMETRY_CRYSTAL = ITEMS.register("symmetry_crystal",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SYMMETRY_CRYSTAL_BLOCK_ITEM = ITEMS.register("symmetry_crystal_block",
            () -> new BlockItem(ModBlocks.SYMMETRY_CRYSTAL_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> SYMMETRY_FORGE_ITEM = ITEMS.register("symmetry_forge",
            () -> new BlockItem(ModBlocks.SYMMETRY_FORGE.get(), new Item.Properties()));

    private ModItems() {
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
