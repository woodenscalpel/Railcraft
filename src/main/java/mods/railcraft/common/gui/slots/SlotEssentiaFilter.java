/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info This code is the property of CovertJaguar and may only be used
 * with explicit written permission unless otherwise specified on the license page at
 * http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.slots;

import mods.railcraft.common.fluids.FluidItemHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.IEssentiaContainerItem;

public class SlotEssentiaFilter extends SlotRailcraft {

    public SlotEssentiaFilter(IInventory iinventory, int slotIndex, int posX, int posY) {
        super(iinventory, slotIndex, posX, posY);
        setPhantom();
        setStackLimit(1);
    }

    public static boolean canPlaceItem(ItemStack itemstack) {
        return itemstack.getItem() instanceof IEssentiaContainerItem;
    }

    @Override
    public boolean isItemValid(ItemStack itemstack) {
        return canPlaceItem(itemstack);
    }
}
