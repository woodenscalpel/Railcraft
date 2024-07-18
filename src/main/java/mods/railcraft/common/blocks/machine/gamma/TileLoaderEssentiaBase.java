/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info This code is the property of CovertJaguar and may only be used
 * with explicit written permission unless otherwise specified on the license page at
 * http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.gamma;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.common.carts.CartUtils;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.plugins.thaumcraft.EssentiaTankManager;
import mods.railcraft.common.plugins.thaumcraft.IEssentiaHandler;
import mods.railcraft.common.plugins.thaumcraft.StandardEssentiaTank;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.PhantomInventory;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import thaumcraft.api.aspects.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class TileLoaderEssentiaBase extends TileLoaderBase implements IInventory, ISidedInventory, IEssentiaTransport, IAspectContainer {

    protected static final int SLOT_INPUT = 0;
    protected static final int SLOT_OUTPUT = 1;
    protected static final int[] SLOTS = InvTools.buildSlotArray(0, 2);
    protected static final int CAPACITY = 256;
    protected final PhantomInventory invFilter = new PhantomInventory(1);
    protected final IInventory invInput = new InventoryMapper(this, SLOT_INPUT, 1);
    protected final EssentiaTankManager tankManager = new EssentiaTankManager();
    protected final StandardEssentiaTank loaderTank = new StandardEssentiaTank(CAPACITY,this);
    //protected final EssentiaTank2 loaderTank = new EssentiaTank2(CAPACITY, this);
    //protected final EssentiaTank2 loaderTank = new EssentiaTank3(CAPACITY, this);
    protected int flow = 0;

    protected TileLoaderEssentiaBase() {
        super();
        setInventorySize(2);
        tankManager.add(loaderTank);
    }

    public EssentiaTankManager getTankManager() {
       return tankManager;
    }

    public PhantomInventory getFluidFilter() {
        return invFilter;
    }

    public Aspect getFilterAspect() {
        if (invFilter.getStackInSlot(0) != null) {
            Aspect apsect = ((IEssentiaContainerItem) invFilter.getStackInSlot(0).getItem()).getAspects(invFilter.getStackInSlot(0)).getAspects()[0];
            return apsect;
        }
        return null;
    }

    @Override
    public boolean isProcessing() {
        return flow > 0;
    }

    protected void sendCart(EntityMinecart cart) {
        if (cart == null) return;
        if (isManualMode()) return;
        if (CartTools.cartVelocityIsLessThan(cart, STOP_VELOCITY)) {
            flow = 0;
            setPowered(true);
        }
    }

    @Override
    protected void setPowered(boolean p) {
        if (isManualMode()) p = false;
        super.setPowered(p);
    }

    @Override
    public boolean canHandleCart(EntityMinecart cart) {
        if (isSendCartGateAction()) return false;
        if (!(cart instanceof IEssentiaHandler)) return false;
        ItemStack minecartSlot1 = getCartFilters().getStackInSlot(0);
        ItemStack minecartSlot2 = getCartFilters().getStackInSlot(1);
        if (minecartSlot1 != null || minecartSlot2 != null) if (!CartUtils.doesCartMatchFilter(minecartSlot1, cart)
                && !CartUtils.doesCartMatchFilter(minecartSlot2, cart))
            return false;
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (Game.isHost(getWorld()) && clock % FluidHelper.NETWORK_UPDATE_INTERVAL == 0) sendUpdateToClient();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot != SLOT_OUTPUT;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return slot == SLOT_OUTPUT;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.getTag("tanks") instanceof NBTTagCompound) data.setTag("tanks", new NBTTagList());
        tankManager.readTanksFromNBT(data);


        if (data.hasKey("filter")) {
            NBTTagCompound filter = data.getCompoundTag("filter");
            getFluidFilter().readFromNBT("Items", filter);
        } else getFluidFilter().readFromNBT("invFilter", data);

    }


    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);


        tankManager.writeTanksToNBT(data);
        getFluidFilter().writeToNBT("invFilter", data);
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);



        tankManager.writePacketData(data);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);


        tankManager.readPacketData(data);
    }

}
