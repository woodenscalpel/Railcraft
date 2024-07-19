/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info This code is the property of CovertJaguar and may only be used
 * with explicit written permission unless otherwise specified on the license page at
 * http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import cpw.mods.fml.common.registry.GameRegistry;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.carts.LocomotivePaintingRecipe;
import mods.railcraft.common.items.ItemIngot;
import mods.railcraft.common.items.RailcraftItem;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import org.apache.logging.log4j.Level;

import mods.railcraft.common.plugins.thaumcraft.ItemCrowbarMagic;
import mods.railcraft.common.plugins.thaumcraft.ItemCrowbarVoid;
import mods.railcraft.common.plugins.thaumcraft.ThaumcraftPlugin;
import mods.railcraft.common.util.misc.Game;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ModuleThaumcraft extends RailcraftModule {

    @Override
    public boolean canModuleLoad() {
        return ThaumcraftPlugin.isModInstalled();
    }

    @Override
    public void printLoadError() {
        Game.log(Level.INFO, "Module disabled: {0}, Thaumcraft not detected", this);
    }

    @Override
    public void initFirst() {
        ItemCrowbarMagic.registerItem();
        ItemCrowbarVoid.registerItem();
    }

    @Override
    public void initSecond() {}

    @Override
    public void postInit() {
        ThaumcraftPlugin.registerAspects();
        ThaumcraftPlugin.setupResearch();

        ItemCrowbarMagic.registerResearch();
        ItemCrowbarVoid.registerResearch();
    }
}
