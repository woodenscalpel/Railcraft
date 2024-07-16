package mods.railcraft.common.plugins.thaumcraft;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import thaumcraft.api.aspects.Aspect;

    public final class EssentiaTankInfo{
        public final Aspect aspect;
        public final int amount;
        public final int capacity;

        public EssentiaTankInfo(Aspect fluid,int amt, int capacity)
        {
            this.aspect = fluid;
            this.amount = amt;
            this.capacity = capacity;
        }

        public EssentiaTankInfo(EssentiaTank fireAspect) {
            this.aspect = fireAspect.getAspect();
            this.amount = fireAspect.getAmount();
            this.capacity = fireAspect.getCapacity();
        }
/*
        public EssentiaTankInfo(IEss tank)
        {
            this.fluid = tank.getFluid();
            this.capacity = tank.getCapacity();
        }

 */
    }
