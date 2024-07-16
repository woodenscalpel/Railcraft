package mods.railcraft.common.plugins.thaumcraft;

import net.minecraftforge.common.util.ForgeDirection;

import thaumcraft.api.aspects.Aspect;

public interface IEssentiaHandler{
    int fill(ForgeDirection from, Aspect a, int amt, boolean doFill);
    Integer drainess(ForgeDirection from, Aspect a, int amt, boolean doDrain);
    Integer drainess(ForgeDirection from, int maxDrain, boolean doDrain);
    boolean canFill(ForgeDirection from, Aspect a, int i);
    boolean canDrain(ForgeDirection from, Aspect a, int i);

    EssentiaTankInfo[] getEssTankInfo(ForgeDirection side);

    // FluidTankInfo[] getEssTankInfo(ForgeDirection from);
}
