/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info This code is the property of CovertJaguar and may only be used
 * with explicit written permission unless otherwise specified on the license page at
 * http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.IFluidCart;
import mods.railcraft.api.carts.ILiquidTransfer;
import mods.railcraft.api.carts.IMinecart;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.thaumcraft.*;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import thaumcraft.api.aspects.Aspect;

public class EntityCartEssentiaTank extends EntityCartFiltered
        implements IEssentiaHandler, ISidedInventory, IMinecart, IEssentiaCart {

    private static final byte FLUID_ID_DATA_ID = 25;
    private static final byte FLUID_QTY_DATA_ID = 26;
    private static final byte FLUID_COLOR_DATA_ID = 27;
    private static final byte FILLING_DATA_ID = 28;
    private static final int SLOT_INPUT = 0;
    private static final int SLOT_OUTPUT = 1;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 2);
    private static final int ESSCAP = 256;
    private final EssentiaTankManager tankManager = new EssentiaTankManager();
    private final StandardEssentiaTank tank = new StandardEssentiaTank(ESSCAP);
    //Filters
    private final IInventory invLiquids = new InventoryMapper(this, false);
    private final IInventory invInput = new InventoryMapper(this, SLOT_INPUT, 1, false);
    private final IInventory invOutput = new InventoryMapper(this, SLOT_OUTPUT, 1, false);
    private int update = MiscTools.getRand().nextInt();

    public EntityCartEssentiaTank(World world) {
        super(world);
        tankManager.add(tank);
    }

    public EntityCartEssentiaTank(World world, double d, double d1, double d2) {
        this(world);
        setPosition(d, d1 + (double) yOffset, d2);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = d;
        prevPosY = d1;
        prevPosZ = d2;
    }

    @Override
    public ICartType getCartType() {
        return EnumCart.ESSENTIATANK;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(FLUID_ID_DATA_ID, new Integer(-1));
        dataWatcher.addObject(FLUID_QTY_DATA_ID, new Integer(0));
        dataWatcher.addObject(FLUID_COLOR_DATA_ID, new Integer(StandardEssentiaTank.DEFAULT_COLOR));
        dataWatcher.addObject(FILLING_DATA_ID, Byte.valueOf((byte) 0));
    }

    private int getFluidQty() {
        return dataWatcher.getWatchableObjectInt(FLUID_QTY_DATA_ID);
    }

    private void setFluidQty(int qty) {
        dataWatcher.updateObject(FLUID_QTY_DATA_ID, qty);
    }

    private int getFluidId() {
        return dataWatcher.getWatchableObjectInt(FLUID_ID_DATA_ID);
    }

    private void setFluidId(int fluidId) {
        dataWatcher.updateObject(FLUID_ID_DATA_ID, fluidId);
    }

    private int getFluidColor() {
        return dataWatcher.getWatchableObjectInt(FLUID_COLOR_DATA_ID);
    }

    private void setFluidColor(int color) {
        dataWatcher.updateObject(FLUID_COLOR_DATA_ID, color);
    }

    public EssentiaTankManager getTankManager() {
        return tankManager;
    }

    @Override
    public void setDead() {
        super.setDead();
        InvTools.dropInventory(invLiquids, worldObj, (int) posX, (int) posY, (int) posZ);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (Game.isNotHost(worldObj)) {
            if (getFluidId() != -1) {
                tank.renderData.aspect = Aspect.getAspect(EssentiaHelper.EssentiaEnum.values()[getFluidId()].getName());
                tank.renderData.amount = getFluidQty();
                tank.renderData.color = getFluidColor();
            } else {
                tank.renderData.aspect = null;
                tank.renderData.amount = 0;
                tank.renderData.color = StandardTank.DEFAULT_COLOR;
            }
            return;
        }

        Aspect aspect = tank.getAspect();
        if (aspect != null) {
            int fluidId = EssentiaHelper.EssentiaEnum.intfromname(aspect.getTag());
            if (fluidId != getFluidId()) setFluidId(fluidId);
            if (tank.amount != getFluidQty()) setFluidQty(tank.amount);
            if (tank.getColor() != getFluidColor()) setFluidColor(tank.getColor());
        } else {
            if (getFluidId() != -1) setFluidId(-1);
            if (getFluidQty() != 0) setFluidQty(0);
            if (getFluidColor() != StandardTank.DEFAULT_COLOR) setFluidColor(StandardTank.DEFAULT_COLOR);
        }

        update++;

        ItemStack topSlot = invLiquids.getStackInSlot(SLOT_INPUT);
        if (topSlot != null && !FluidItemHelper.isContainer(topSlot)) {
            invLiquids.setInventorySlotContents(SLOT_INPUT, null);
            entityDropItem(topSlot, 1);
        }

        ItemStack bottomSlot = invLiquids.getStackInSlot(SLOT_OUTPUT);
        if (bottomSlot != null && !FluidItemHelper.isContainer(bottomSlot)) {
            invLiquids.setInventorySlotContents(SLOT_OUTPUT, null);
            entityDropItem(bottomSlot, 1);
        }

        if (update % FluidHelper.BUCKET_FILL_TIME == 0) {
            //FluidHelper.processContainers(tank, invLiquids, SLOT_INPUT, SLOT_OUTPUT);
        }
    }

    @Override
    public boolean doInteract(EntityPlayer player) {
        if (Game.isHost(worldObj)) {
            //if (FluidHelper.handleRightClick(this, ForgeDirection.UNKNOWN, player, true, true)) return true;
            GuiHandler.openGui(EnumGui.CART_ESSENTIATANK, player, worldObj, this);
        }
        return true;
    }

    @Override
    public int getSizeInventory() {
        return 2;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);
        tankManager.readTanksFromNBT(data);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound data) {
        super.writeEntityToNBT(data);
        tankManager.writeTanksToNBT(data);
    }

    @Override
    public int fill(ForgeDirection from, Aspect a, int i, boolean doFill) {
        if (a == null) return 0;
        //Fluid filterFluid = getFilterFluid();
        //if (filterFluid == null || resource.getFluid() == filterFluid) return tank.fill(resource, doFill);
        return tank.fill(a,i,doFill);
        //return 0;
    }



    @Override
    public boolean canAcceptPushedEssentia(EntityMinecart requester, Aspect aspect, int amt) {
        return false;
    }

    @Override
    public boolean canProvidePulledEssentia(EntityMinecart requester, Aspect aspect, int amt) {
        return false;
    }

    @Override
    public void setFilling(boolean fill) {
        dataWatcher.updateObject(FILLING_DATA_ID, Byte.valueOf(fill ? 1 : (byte) 0));
    }

    public Fluid getFilterFluid() {
        ItemStack filter = getFilterItem();
        if (filter == null) return null;
        return FluidItemHelper.getFluidInContainer(filter);
    }

    public IInventory getInvLiquids() {
        return invLiquids;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot == SLOT_INPUT && FluidItemHelper.isContainer(stack);
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
    public Integer drainess(ForgeDirection from, Aspect a, int amt, boolean doDrain) {
        return 0;
    }

    @Override
    public Integer drainess(ForgeDirection from, int maxDrain, boolean doDrain) {
        return tankManager.get(0).drain(maxDrain,doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Aspect a, int i) {
        return tankManager.get(0).amount > tankManager.get(0).getCapacity();
    }

    @Override
    public boolean canDrain(ForgeDirection from, Aspect a, int i) {
        return true;
    }

    @Override
    public EssentiaTankInfo[] getEssTankInfo(ForgeDirection side) {
        EssentiaTankInfo e = new EssentiaTankInfo(tankManager.get(0).getAspect(),tankManager.get(0).amount,tankManager.get(0).getCapacity());
        return new EssentiaTankInfo[] {e};
    }
}
