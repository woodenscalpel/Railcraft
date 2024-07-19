/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info This code is the property of CovertJaguar and may only be used
 * with explicit written permission unless otherwise specified on the license page at
 * http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import mods.railcraft.api.carts.IItemCart;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.plugins.thaumcraft.*;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.Optional;
import mods.railcraft.api.carts.locomotive.LocomotiveRenderType;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.items.ItemTicket;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.IInvSlot;
import mods.railcraft.common.util.inventory.wrappers.InventoryIterator;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.steam.EssentiaFuelProvider;
import scala.Int;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@Optional.InterfaceList(
        value = { @Optional.Interface(iface = "thaumcraft.api.aspects.IAspectContainer", modid = "Thaumcraft"), })
public class EntityLocomotiveSteamMagic extends EntityLocomotiveSteam implements ISidedInventory, IAspectContainer, IEssentiaCart, IEssentiaHandler {

    private static final int SLOT_TICKET = 6;
    private static final int SLOT_DESTINATION = 7;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 7);
    private static final byte FIRE_ASPECT_DATA_ID = 31;
    //private static final byte WATER_ASPECT_DATA_ID = 31;
    private final IInventory invTicket = new InventoryMapper(this, SLOT_TICKET, 2, false);
    private EssentiaTankManager essTankManager;
    private EssentiaTank fireAspect;
    private EssentiaTank waterAspect;

    public EntityLocomotiveSteamMagic(World world) {
        super(world);
    }

    public EntityLocomotiveSteamMagic(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public ICartType getCartType() {
        return EnumCart.LOCO_STEAM_MAGIC;
    }

    @Override
    public LocomotiveRenderType getRenderType() {return LocomotiveRenderType.STEAM_MAGIC;}


    @Override
    @Optional.Method(modid = "Thaumcraft")
    public boolean doesContainerAccept(Aspect tag) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void entityInit() {
        super.entityInit();

       // essTankManager = new EssentiaTankManager();

        fireAspect = new EssentiaTank(Aspect.FIRE, 256, dataWatcher, FIRE_ASPECT_DATA_ID);

       // waterAspect = new EssentiaTank(Aspect.WATER, 256, dataWatcher, WATER_ASPECT_DATA_ID);

        boiler.setFuelProvider(new EssentiaFuelProvider(fireAspect) {

            @Override
            public double getMoreFuel() {
                if (isShutdown()) return 0;
                return super.getMoreFuel();
            }
        });
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound data) {
        super.writeEntityToNBT(data);
        data.setInteger("EssentiaAMT",fireAspect.getAmount());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);
        fireAspect.setAmount(data.getInteger("EssentiaAMT"));
    }

    @Override
    protected void openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.LOCO_MAGIC, player, worldObj, this);
    }

    @Override
    public boolean needsRefuel() {
        FluidStack water = tankWater.getFluid();
        if (water == null || water.amount < tankWater.getCapacity() / 2) return true;
        if (fireAspect.getAmount() < 16) return true;
        return false;
    }

    public EssentiaTank getFireAspect() {
        return fireAspect;
    }

    public EssentiaTank getWaterAspect() {
        return waterAspect;
    }

    @Override
    protected IInventory getTicketInventory() {
        return invTicket;
    }

    @Override
    public int getSizeInventory() {
        return 8;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return slot < SLOT_TICKET;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        switch (slot) {
            case SLOT_LIQUID_INPUT:
                return FluidItemHelper.containsFluid(stack, Fluids.WATER.get(1));
            case SLOT_TICKET:
                return ItemTicket.FILTER.matches(stack);
            default:
                return false;
        }
    }

    @Override
    @Optional.Method(modid = "Thaumcraft")
    public AspectList getAspects() {
        return new AspectList().add(Aspect.FIRE, fireAspect.getAmount()).add(Aspect.WATER, waterAspect.getAmount());
    }

    @Override
    @Optional.Method(modid = "Thaumcraft")
    public void setAspects(AspectList aspects) {}

    @Override
    @Optional.Method(modid = "Thaumcraft")
    public int addToContainer(Aspect tag, int amount) {
        if (tag == Aspect.FIRE) return fireAspect.fill(amount, true);
        if (tag == Aspect.WATER) return waterAspect.fill(amount, true);
        return amount;
    }

    @Override
    @Optional.Method(modid = "Thaumcraft")
    public boolean takeFromContainer(Aspect tag, int amount) {
        if (tag == Aspect.FIRE) return fireAspect.remove(amount, true);
        if (tag == Aspect.WATER) return waterAspect.remove(amount, true);
        return false;
    }

    @Override
    @Optional.Method(modid = "Thaumcraft")
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }

    @Override
    @Optional.Method(modid = "Thaumcraft")
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        if (tag == Aspect.FIRE) return fireAspect.contains(amount);
        if (tag == Aspect.WATER) return waterAspect.contains(amount);
        return false;
    }

    @Override
    @Optional.Method(modid = "Thaumcraft")
    public boolean doesContainerContain(AspectList ot) {
        for (Entry<Aspect, Integer> entry : ot.aspects.entrySet()) {
            if (!doesContainerContainAmount(entry.getKey(), entry.getValue())) return false;
        }
        return true;
    }

    @Override
    @Optional.Method(modid = "Thaumcraft")
    public int containerContains(Aspect tag) {
        if (tag == Aspect.FIRE) return fireAspect.getAmount();
        if (tag == Aspect.WATER) return waterAspect.getAmount();
        return 0;
    }

    public EssentiaTank getEssentiaFireTank() {
        return fireAspect;
    }

    @Override
    public boolean canAcceptPushedEssentia(EntityMinecart requester, Aspect aspect, int amt) {
        return aspect == Aspect.FIRE;
    }

    @Override
    public boolean canProvidePulledEssentia(EntityMinecart requester, Aspect aspect, int amt) {
        return false;
    }

    @Override
    public int fill(ForgeDirection from, Aspect a, int amt, boolean doFill) {
        if(a == fireAspect.getAspect()) {
            return fireAspect.fill(amt, doFill);
        }
        return 0;
    }

    @Override
    public Integer drainess(ForgeDirection from, Aspect a, int amt, boolean doDrain) {
        return null;
    }

    @Override
    public Integer drainess(ForgeDirection from, int maxDrain, boolean doDrain) {
        return 0;
    }


    @Override
    public boolean canFill(ForgeDirection from, Aspect a, int i) {
        if(a != Aspect.FIRE){return false;}
        else {
            return fireAspect.getAmount() < fireAspect.getCapacity();
        }
    }

    @Override
    public boolean canDrain(ForgeDirection from, Aspect a, int i) {
        return false;
    }

    @Override
    public EssentiaTankInfo[] getEssTankInfo(ForgeDirection side) {
        List<EssentiaTankInfo> info = new ArrayList<>();
        info.add(new EssentiaTankInfo(fireAspect));
        return info.toArray(new EssentiaTankInfo[info.size()]);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }
}
