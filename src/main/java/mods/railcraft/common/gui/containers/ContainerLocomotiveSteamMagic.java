/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info This code is the property of CovertJaguar and may only be used
 * with explicit written permission unless otherwise specified on the license page at
 * http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.common.carts.EntityLocomotiveSteamMagic;
import mods.railcraft.common.carts.EntityLocomotiveSteamSolid;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.gui.slots.SlotFuel;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotWaterLimited;
import mods.railcraft.common.gui.widgets.EssentiaGaugeWidget;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

public class ContainerLocomotiveSteamMagic extends ContainerLocomotive {

    private final EntityLocomotiveSteamMagic loco;
    private double lastBurnTime;
    private double lastItemBurnTime;
    private double lastHeat;

    private ContainerLocomotiveSteamMagic(InventoryPlayer playerInv, EntityLocomotiveSteamMagic loco) {
        super(playerInv, loco, 205);
        this.loco = loco;
    }

    public static ContainerLocomotiveSteamMagic make(InventoryPlayer playerInv, EntityLocomotiveSteamMagic loco) {
        ContainerLocomotiveSteamMagic con = new ContainerLocomotiveSteamMagic(playerInv, loco);
        con.init();
        return con;
    }

    @Override
    public void defineSlotsAndWidgets() {
        addWidget(new FluidGaugeWidget(loco.getTankManager().get(0), 116, 23, 176, 0, 16, 47));
        addWidget(new FluidGaugeWidget(loco.getTankManager().get(1), 17, 23, 176, 0, 16, 47));
        addWidget(new EssentiaGaugeWidget(loco.getEssentiaFireTank(), 72, 23, 176, 0, 16, 47));

        addWidget(new IndicatorWidget(loco.boiler.heatIndicator, 40, 25, 176, 61, 6, 43));

        addSlot(new SlotWaterLimited(loco, 0, 143, 21));
        addSlot(new SlotOutput(loco, 1, 143, 56));

    }

    @Override
    public void addCraftingToCrafters(ICrafting icrafting) {
        super.addCraftingToCrafters(icrafting);
        TankManager tMan = loco.getTankManager();
        if (tMan != null) {
            tMan.initGuiData(this, icrafting, 0);
            tMan.initGuiData(this, icrafting, 1);
        }

        icrafting.sendProgressBarUpdate(this, 20, (int) Math.round(loco.boiler.burnTime));
        icrafting.sendProgressBarUpdate(this, 21, (int) Math.round(loco.boiler.currentItemBurnTime));
        icrafting.sendProgressBarUpdate(this, 22, (int) Math.round(loco.boiler.getHeat()));

    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();
        TankManager tMan = loco.getTankManager();
        if (tMan != null) {
            tMan.updateGuiData(this, crafters, 0);
            tMan.updateGuiData(this, crafters, 1);
        }

        for (int var1 = 0; var1 < this.crafters.size(); ++var1) {
            ICrafting var2 = (ICrafting) this.crafters.get(var1);

            if (this.lastBurnTime != loco.boiler.burnTime)
                var2.sendProgressBarUpdate(this, 20, (int) Math.round(loco.boiler.burnTime));

            if (this.lastItemBurnTime != loco.boiler.currentItemBurnTime)
                var2.sendProgressBarUpdate(this, 21, (int) Math.round(loco.boiler.currentItemBurnTime));

            if (this.lastHeat != loco.boiler.getHeat())
                var2.sendProgressBarUpdate(this, 22, (int) Math.round(loco.boiler.getHeat()));
        }

        this.lastBurnTime = loco.boiler.burnTime;
        this.lastItemBurnTime = loco.boiler.currentItemBurnTime;
        this.lastHeat = loco.boiler.getHeat();

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        super.updateProgressBar(id, value);
        TankManager tMan = loco.getTankManager();
        if (tMan != null) tMan.processGuiUpdate(id, value);

        switch (id) {
            case 20:
                loco.boiler.burnTime = value;
                break;
            case 21:
                loco.boiler.currentItemBurnTime = value;
                break;
            case 22:
                loco.boiler.setHeat(value);
                break;
        }

    }


}
