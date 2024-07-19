
package mods.railcraft.common.plugins.thaumcraft;

import mods.railcraft.common.core.Railcraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.*;
import thaumcraft.api.aspects.Aspect;

import java.util.Objects;

/**
 * Reference implementation of {@link IFluidTank}. Use/extend this or implement your own.
 *
 * @author King Lemming, cpw (LiquidTank)
 *
 */
public class BaseEssentiaTank
{
    protected Aspect aspect;
    public int amount;
    protected int capacity;
    protected TileEntity tile;

    public BaseEssentiaTank(int capacity)
    {
        this(null, capacity);
    }

    public BaseEssentiaTank(Aspect aspect, int amount, int capacity)
    {
        this.aspect = aspect;
        this.amount = amount;
        this.capacity = capacity;
    }

    public BaseEssentiaTank(Aspect aspect, int capacity)
    {
        this.aspect = aspect;
        this.amount = 0;
        this.capacity = capacity;
    }


    public BaseEssentiaTank readFromNBT(NBTTagCompound nbt)
    {
        String aspectString = nbt.getString("AspectType");
        if (!Objects.equals(aspectString, "null"))
        {
            //FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
            aspect = Aspect.getAspect(aspectString);
            amount = nbt.getShort("AspectAmount");
        }
        else
        {
            aspect = null;
            amount = 0;
        }

        return this;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        if (!(aspect == null))
        {
            nbt.setString("AspectType",aspect.getTag());
            nbt.setShort("AspectAmount", (short) amount);
        }
        else
        {
            nbt.setString("AspectType","null");
            nbt.setShort("AspectAmount", (short) 0);
        }

        return nbt;
    }

    public void setAspect(Aspect aspect)
    {
        this.aspect = aspect;
    }

    public void setCapacity(int capacity)
    {
        this.capacity = capacity;
    }

    public Aspect getAspect()
    {
        return aspect;
    }

    public int getFluidAmount()
    {
        if (aspect == null)
        {
            return 0;
        }
        return amount;
    }

    public int getCapacity()
    {
        return capacity;
    }

    public int fill(Aspect raspect, int ramount, boolean doFill)
    {
        if (raspect == null)
        {
            return 0;
        }

        if (!doFill)
        {
            if (aspect == null)
            {
                return Math.min(capacity, ramount);
            }

            if (!(raspect == aspect))
            {
                return 0;
            }

            return Math.min(capacity - amount, ramount);
        }

        if (aspect == null)
        {
            aspect = raspect;
            amount = Math.min(capacity, ramount);

            if (tile != null)
            {
                //tile.getWorldObj().markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
                //FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(fluid, tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord, this, fluid.amount));
            }
            return amount;
        }

        if (!(aspect == raspect))
        {
            return 0;
        }
        int filled = capacity - amount;

        if (ramount < filled)
        {
            amount += ramount;
            filled = ramount;
        }
        else
        {
            amount = capacity;
        }

        if (tile != null)
        {
           // tile.getWorldObj().markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
            //FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(fluid, tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord, this, filled));
        }
        return filled;
    }

    public Integer drain(int maxDrain, boolean doDrain)
    {
        if (aspect == null)
        {
            return null;
        }

        int drained = maxDrain;
        if (amount < drained)
        {
            drained = amount;
        }

        //FluidStack stack = new FluidStack(fluid, drained);
        if (doDrain)
        {
            amount -= drained;
            if (amount <= 0)
            {
                aspect = null;
            }

            if (tile != null)
            {
            //    tile.getWorldObj().markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
                //FluidEvent.fireEvent(new FluidEvent.FluidDrainingEvent(fluid, tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord, this, drained));
            }
        }
        return drained;
        //return stack;
    }
}
