/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info This code is the property of CovertJaguar and may only be used
 * with explicit written permission unless otherwise specified on the license page at
 * http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.thaumcraft;

import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.gui.tooltips.ToolTipLine;
import net.minecraft.entity.DataWatcher;

import net.minecraft.item.EnumRarity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import thaumcraft.api.aspects.Aspect;

import java.util.Locale;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EssentiaTank {

    private final Aspect aspect;
    private final DataWatcher dataWatcher;
    private final int capacity, dataId;
    protected final ToolTip toolTip = new ToolTip() {

        @Override
        public void refresh() {
            refreshTooltip();
        }
    };

    public static final int DEFAULT_COLOR = 0xFFFFFF;
    //public final EssentiaTank.TankRenderData renderData = new EssentiaTank.TankRenderData();

    public EssentiaTank(Aspect aspect, int capacity, DataWatcher dataWatcher, int dataId) {
        this.aspect = aspect;
        this.dataWatcher = dataWatcher;
        this.dataId = dataId;
        this.capacity = capacity;
        dataWatcher.addObject(dataId, (short) 0);
    }

    public Aspect getAspect() {
        return aspect;
    }

    public int getAmount() {
        return dataWatcher.getWatchableObjectShort(dataId);
    }

    public void setAmount(int amount) {
        dataWatcher.updateObject(dataId, (short) amount);
    }

    public int fill(int amount, boolean doAdd) {
        if (amount < 0) return 0;
        int remainder = 0;
        int contents = getAmount() + amount;
        int added = 0;
        if(contents < capacity){
           added = amount;
        }
        if (contents > capacity) {
            remainder = contents - capacity;
            contents = capacity;
            added = amount - remainder;
        }
        if (doAdd) setAmount(contents);
        return added;
    }

    public boolean contains(int amount) {
        return getAmount() >= amount;
    }

    public boolean remove(int amount, boolean doRemove) {
        if (amount < 0) return false;
        if (contains(amount)) {
            if (doRemove) {
                int contents = getAmount() - amount;
                setAmount(Math.max(contents, 0));
            }
            return true;
        }
        return false;
    }

    public ToolTip getToolTip() {
        return toolTip;
    }


    protected void refreshTooltip() {
        toolTip.clear();
        int amount = 0;
        if (aspect != null && getAmount() > 0) {
            EnumRarity rarity = EnumRarity.common;
            ToolTipLine fluidName = new ToolTipLine(
                aspect.getName(),
                rarity.rarityColor);
            fluidName.setSpacing(2);
            toolTip.add(fluidName);
            amount = getAmount();
        }
        //toolTip.add(new ToolTipLine(String.format(aspect.getName())));
        toolTip.add(new ToolTipLine(String.format(Locale.ENGLISH, "%,d / %,d", getAmount(), getCapacity())));
    }

    public int getCapacity() {
        return capacity;
    }

/*
    public static class TankRenderData {

        public Aspect aspect = null;
        //public int amount = getA;
        public int color = DEFAULT_COLOR;

        public void reset() {
            aspect = null;
         //   amount = 0;
            color = DEFAULT_COLOR;
        }
    }
 */
}
