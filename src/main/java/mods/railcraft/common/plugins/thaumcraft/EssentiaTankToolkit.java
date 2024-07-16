/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info This code is the property of CovertJaguar and may only be used
 * with explicit written permission unless otherwise specified on the license page at
 * http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.thaumcraft;

import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;

/**
 * This class provides some convenience functions for ITankContainers
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EssentiaTankToolkit implements IEssentiaHandler {

    private final IEssentiaHandler tankContainer;

    public EssentiaTankToolkit(IEssentiaHandler c) {
        tankContainer = c;
    }

    public int getFluidQty(Aspect aspect) {
        if (aspect == null) return 0;
        int amount = 0;
        for (EssentiaTankInfo tank : getEssTankInfo(ForgeDirection.UNKNOWN)) {
            if (tank.aspect != null && aspect == tank.aspect) amount += tank.amount;
        }
        return amount;
    }

    public boolean isTankEmpty(Aspect aspect) {
        if (aspect == null) return areTanksEmpty();
        return getFluidQty(aspect) <= 0;
    }

    public boolean isTankFull(Aspect aspect) {
        if (aspect == null) return areTanksFull();
        int fill = fill(ForgeDirection.UNKNOWN, aspect, 1, false);
        return fill <= 0;
    }

    public boolean areTanksFull() {
        for (EssentiaTankInfo tank : getEssTankInfo(ForgeDirection.UNKNOWN)) {
            if (tank.aspect == null || tank.amount < tank.capacity) return false;
        }
        return true;
    }

    public boolean areTanksEmpty() {
        return !isFluidInTank();
    }

    public boolean isFluidInTank() {
        for (EssentiaTankInfo tank : getEssTankInfo(ForgeDirection.UNKNOWN)) {
            boolean empty = tank.aspect == null || tank.amount <= 0;
            if (!empty) return true;
        }
        return false;
    }

    public float getFluidLevel() {
        int amount = 0;
        int capacity = 0;
        for (EssentiaTankInfo tank : getEssTankInfo(ForgeDirection.UNKNOWN)) {
            Aspect liquid = tank.aspect;
            amount += liquid == null ? 0 : tank.amount;
            capacity += tank.capacity;
        }
        return capacity == 0 ? 0 : amount / capacity;
    }

    public float getFluidLevel(Aspect fluid) {
        int amount = 0;
        int capacity = 0;
        for (EssentiaTankInfo tank : getEssTankInfo(ForgeDirection.UNKNOWN)) {
            if (tank.aspect == null || tank.aspect != fluid) continue;
            amount += tank.amount;
            capacity += tank.capacity;
        }
        return capacity == 0 ? 0 : amount / (float) capacity;
    }

    public boolean canPutFluid(ForgeDirection from, Aspect aspect, int amt) {
        if (aspect == null) return false;
        return fill(from, aspect,amt, false) > 0;
    }

    @Override
    public int fill(ForgeDirection from, Aspect aspect, int  amt, boolean doFill) {
        return tankContainer.fill(from, aspect, amt, doFill);
    }

    @Override
    public Integer drainess(ForgeDirection from, int maxDrain, boolean doDrain) {
        return tankContainer.drainess(from, maxDrain, doDrain);
    }

    @Override
    public Integer drainess(ForgeDirection from, Aspect a, int i, boolean doDrain) {
        return tankContainer.drainess(from, a,i, doDrain);
    }

    @Override
    public EssentiaTankInfo[] getEssTankInfo(ForgeDirection side) {
        return tankContainer.getEssTankInfo(side);
    }

    @Override
    public boolean canFill(ForgeDirection from, Aspect a, int i) {
        return tankContainer.canFill(from, a,i);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Aspect fluid,int i) {
        return tankContainer.canDrain(from, fluid,i);
    }

    public boolean canFill2(ForgeDirection forgeDirection, Aspect aspect, int i) {

        EssentiaTankInfo e =  tankContainer.getEssTankInfo(ForgeDirection.UP)[0];
        if(e.aspect == aspect || e.aspect == null) {
            return aspect != null && i > 0 && e.amount < e.capacity;
        }
        return false;
    }
}
