/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info This code is the property of CovertJaguar and may only be used
 * with explicit written permission unless otherwise specified on the license page at
 * http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.carts.EntityCartEssentiaTank;
import mods.railcraft.common.carts.EntityCartTank;
import mods.railcraft.common.gui.slots.SlotFluidFilter;
import mods.railcraft.common.gui.slots.SlotLiquidContainer;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.widgets.EssentiaGaugeWidget3;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerCartEssentiaTank extends RailcraftContainer {

    private EntityCartEssentiaTank tank;
    private final Slot input;

    public ContainerCartEssentiaTank(InventoryPlayer inventoryplayer, EntityCartEssentiaTank cart) {
        super(cart);
        this.tank = cart;

        //addWidget(new FluidGaugeWidget(cart.getTankManager().get(0), 35, 23, 176, 0, 16, 47));
        addWidget(new EssentiaGaugeWidget3(cart.getTankManager().get(0), 35, 23, 176, 0, 16, 47));

        addSlot(new SlotFluidFilter(cart.getFilterInv(), 0, 71, 39));
        addSlot(input = new SlotLiquidContainer(cart.getInvLiquids(), 0, 116, 21));
        addSlot(new SlotOutput(cart.getInvLiquids(), 1, 116, 56));

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }
        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(inventoryplayer, j, 8 + j * 18, 142));
        }
    }
}
