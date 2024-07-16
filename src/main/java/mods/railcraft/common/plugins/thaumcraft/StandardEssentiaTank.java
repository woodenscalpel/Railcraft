/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info This code is the property of CovertJaguar and may only be used
 * with explicit written permission unless otherwise specified on the license page at
 * http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.thaumcraft;

import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.gui.tooltips.ToolTipLine;
import net.minecraft.item.EnumRarity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import thaumcraft.api.aspects.Aspect;

import java.util.Locale;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class StandardEssentiaTank extends BaseEssentiaTank {

    public static final int DEFAULT_COLOR = 0xFFFFFF;
    public final TankRenderData renderData = new TankRenderData();
    protected final ToolTip toolTip = new ToolTip() {

        @Override
        public void refresh() {
            refreshTooltip();
        }
    };
    private int tankIndex;
    private boolean hidden;

    public StandardEssentiaTank(int capacity) {
        super(capacity);
    }

    public StandardEssentiaTank(int capacity, TileEntity tile) {
        this(capacity);
        this.tile = tile;
    }

    public int getTankIndex() {
        return tankIndex;
    }

    public void setTankIndex(int index) {
        this.tankIndex = index;
    }

    public int getColor() {
        Aspect a = getAspect();
        if (a == null) return DEFAULT_COLOR;
        return  a.getColor();
    }

    public boolean isEmpty() {
        return getAspect() == null || amount <= 0;
    }

    public boolean isFull() {
        return getAspect() != null && amount == getCapacity();
    }

    public int getRemainingSpace() {
        return capacity - getFluidAmount();
    }

    @Override
    public int fill(Aspect raspect,int ramt, final boolean doFill) {
        if (raspect == null) return 0;
        if (ramt <= 0) return 0;
        //tile.getWorldObj().markBlockForUpdate(tile.xCoord,tile.yCoord,tile.zCoord);
        return super.fill(raspect,ramt, doFill);
    }

    @Override
    public Integer drain(int maxDrain, boolean doDrain) {
        if (maxDrain <= 0) return null;
        //etile.getWorldObj().markBlockForUpdate(tile.xCoord,tile.yCoord,tile.zCoord);
        return super.drain(maxDrain, doDrain);
    }

    public ToolTip getToolTip() {
        return toolTip;
    }

    protected void refreshTooltip() {
        toolTip.clear();
        int amount = 0;
        if (renderData.aspect != null && renderData.amount > 0) {
            //EnumRarity rarity = renderData.aspect.getRarity();
            EnumRarity rarity= EnumRarity.common;
            ToolTipLine fluidName = new ToolTipLine(
                    renderData.aspect.getName(),
                    rarity.rarityColor);
            fluidName.setSpacing(2);
            toolTip.add(fluidName);
            amount = renderData.amount;
        }
        toolTip.add(new ToolTipLine(String.format(Locale.ENGLISH, "%,d / %,d", renderData.amount, getCapacity())));
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public static class TankRenderData {

        public Aspect aspect = null;
        public int amount = 0;
        public int color = DEFAULT_COLOR;

        public void reset() {
            aspect = null;
            amount = 0;
            color = DEFAULT_COLOR;
        }
    }
}
