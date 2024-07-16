/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info This code is the property of CovertJaguar and may only be used
 * with explicit written permission unless otherwise specified on the license page at
 * http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.thaumcraft;

import com.google.common.collect.ForwardingList;
import cpw.mods.fml.common.network.ByteBufUtils;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.plugins.forge.NBTPlugin.NBTList;
import mods.railcraft.common.util.misc.AdjacentTileCache;
import mods.railcraft.common.util.misc.ITileFilter;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import thaumcraft.api.aspects.Aspect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EssentiaTankManager extends ForwardingList<StandardEssentiaTank> implements List<StandardEssentiaTank> {

    private static final byte NETWORK_DATA = 3;
    private final List<StandardEssentiaTank> tanks = new ArrayList<StandardEssentiaTank>();

    public EssentiaTankManager() {}

    public EssentiaTankManager(StandardEssentiaTank... tanks) {
        addAll(Arrays.asList(tanks));
    }


    @Override
    protected List<StandardEssentiaTank> delegate() {
        return tanks;
    }

    @Override
    public boolean add(StandardEssentiaTank tank) {
        boolean added = tanks.add(tank);
        int index = tanks.indexOf(tank);
        tank.setTankIndex(index);
        return added;
    }

    public void writeTanksToNBT(NBTTagCompound data) {
        NBTTagList tagList = new NBTTagList();
        for (byte slot = 0; slot < tanks.size(); slot++) {
            StandardEssentiaTank tank = tanks.get(slot);
            if (tank.getAspect() != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("tank", slot);
                tank.writeToNBT(tag);
                tagList.appendTag(tag);
            }
        }
        data.setTag("tanks", tagList);
    }

    public void readTanksFromNBT(NBTTagCompound data) {
        NBTList<NBTTagCompound> tagList = NBTPlugin.getNBTList(data, "tanks", NBTPlugin.EnumNBTType.COMPOUND);
        for (NBTTagCompound tag : tagList) {
            int slot = tag.getByte("tank");
            if (slot >= 0 && slot < tanks.size()) tanks.get(slot).readFromNBT(tag);
        }
    }

    public void writePacketData(DataOutputStream data) throws IOException {
        for (int i = 0; i < tanks.size(); i++) {
            writePacketData(data, i);
        }
    }

    public void writePacketData(DataOutputStream data, int tankIndex) throws IOException {
        if (tankIndex >= tanks.size()) return;
        StandardEssentiaTank tank = tanks.get(tankIndex);
        Aspect aspect = tank.getAspect();
        if (aspect != null) {
            //Railcraft.logger.info(String.valueOf(EssentiaHelper.EssentiaEnum.intfromname(aspect.getName())));
            data.writeShort((short) EssentiaHelper.EssentiaEnum.intfromname(aspect.getTag()));
            data.writeInt(tank.amount);
            data.writeInt(aspect.getColor());
        } else data.writeShort(-1);
    }

    public void readPacketData(DataInputStream data) throws IOException {
        for (int i = 0; i < tanks.size(); i++) {
            readPacketData(data, i);
        }
    }

    public void readPacketData(DataInputStream data, int tankIndex) throws IOException {
        if (tankIndex >= tanks.size()) return;
        StandardEssentiaTank tank = tanks.get(tankIndex);
        int fluidId = data.readShort();
        if (fluidId != -1) {
            tank.renderData.aspect = Aspect.getAspect(EssentiaHelper.EssentiaEnum.values()[fluidId].getName());
            tank.renderData.amount = data.readInt();
            tank.renderData.color = data.readInt();
        } else tank.renderData.reset();
    }

    public void initGuiData(Container container, ICrafting player, int tankIndex) {
        Railcraft.logger.info("INIT GUI");
        if (tankIndex >= tanks.size()) return;
        StandardEssentiaTank tank = tanks.get(tankIndex);
        //FluidStack fluidStack = tanks.get(tankIndex).getFluid();
        int color = tank.getColor();
        int fluidId = -1;
        int fluidAmount = 0;
        Railcraft.logger.info("ASECT");
        Railcraft.logger.info(tank.getAspect());
        if (tank.aspect != null && tank.amount > 0) {
            fluidId = EssentiaHelper.EssentiaEnum.intfromname(tank.aspect.getTag());
            fluidAmount = tank.amount;
        }

        player.sendProgressBarUpdate(container, tankIndex * NETWORK_DATA + 0, fluidId);
        PacketBuilder.instance().sendGuiIntegerPacket(
                (EntityPlayerMP) player,
                container.windowId,
                tankIndex * NETWORK_DATA + 1,
                fluidAmount);
        PacketBuilder.instance()
                .sendGuiIntegerPacket((EntityPlayerMP) player, container.windowId, tankIndex * NETWORK_DATA + 2, color);

        tank.renderData.aspect = tank.getAspect();
        tank.renderData.amount = fluidAmount;
        tank.renderData.color = color;
    }

    public void updateGuiData(Container container, List crafters, int tankIndex) {
        StandardEssentiaTank tank = tanks.get(tankIndex);
        int color = tank.getColor();
        int pColor = tank.renderData.color;

        for (Object crafter1 : crafters) {
            ICrafting crafter = (ICrafting) crafter1;
            EntityPlayerMP player = (EntityPlayerMP) crafter1;
            if (tank.aspect == null ^ tank.renderData.aspect== null) {
                int fluidId = -1;
                int fluidAmount = 0;
                if (tank.aspect != null) {
                    fluidId = EssentiaHelper.EssentiaEnum.intfromname(tank.aspect.getTag());
                    fluidAmount = tank.amount;
                }
                crafter.sendProgressBarUpdate(container, tankIndex * NETWORK_DATA + 0, fluidId);
                PacketBuilder.instance()
                        .sendGuiIntegerPacket(player, container.windowId, tankIndex * NETWORK_DATA + 1, fluidAmount);
            } else if (tank.aspect != null && tank.renderData.aspect != null) {
                if (tank.getAspect() != tank.renderData.aspect) crafter.sendProgressBarUpdate(
                        container,
                        tankIndex * NETWORK_DATA + 0,
                    EssentiaHelper.EssentiaEnum.intfromname(tank.aspect.getTag()));
                if (tank.amount != tank.renderData.amount) PacketBuilder.instance().sendGuiIntegerPacket(
                        player,
                        container.windowId,
                        tankIndex * NETWORK_DATA + 1,
                        tank.amount);
                if (color != pColor) PacketBuilder.instance()
                        .sendGuiIntegerPacket(player, container.windowId, tankIndex * NETWORK_DATA + 2, color);
            }
        }

        tank.renderData.aspect = tank.getAspect();
        tank.renderData.amount = tank.getFluidAmount();
        tank.renderData.color = color;
    }

    public void processGuiUpdate(int messageId, int data) {
        int tankIndex = messageId / NETWORK_DATA;

        if (tankIndex >= tanks.size()) return;
        StandardEssentiaTank tank = tanks.get(tankIndex);
        switch (messageId % NETWORK_DATA) {
            case 0:
                if(data== -1){

                }
                else {
                    tank.renderData.aspect = Aspect.getAspect(EssentiaHelper.EssentiaEnum.values()[data].getName());
                }
                break;
            case 1:
                tank.renderData.amount = data;
                break;
            case 2:
                tank.renderData.color = data;
                break;
        }
    }

    public int fill(ForgeDirection from, Aspect aspect, Integer amt, boolean doFill) {
        return fill(0, aspect,amt, doFill);
    }

    public int fill(int tankIndex, Aspect aspect,int amt, boolean doFill) {
        if (tankIndex < 0 || tankIndex >= tanks.size() || aspect== null) return 0;

        return tanks.get(tankIndex).fill(aspect,amt, doFill);
    }

    public Integer drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return drain(0, maxDrain, doDrain);
    }

    public Integer drain(int tankIndex, int maxDrain, boolean doDrain) {
        if (tankIndex < 0 || tankIndex >= tanks.size()) return null;

        return tanks.get(tankIndex).drain(maxDrain, doDrain);
    }

    public Integer drain(ForgeDirection from, Aspect raspect,int ramt, boolean doDrain) {
        for (StandardEssentiaTank tank : tanks) {
            if (tankCanDrainFluid(tank, raspect)) return tank.drain(ramt, doDrain);
        }
        return null;
    }

    public boolean canFill(ForgeDirection from, Aspect aspect) {
        return true;
    }

    public boolean canDrain(ForgeDirection from, Aspect aspect) {
        return true;
    }

    public StandardEssentiaTank get(int tankIndex) {
        if (tankIndex < 0 || tankIndex >= tanks.size()) return null;
        return tanks.get(tankIndex);
    }

    public void setCapacity(int tankIndex, int capacity) {
        StandardEssentiaTank tank = get(tankIndex);
        tank.setCapacity(capacity);
        Aspect aspect= tank.aspect;
        if (aspect != null && tank.amount > capacity) tank.amount = capacity;
    }


    private boolean tankCanDrain(StandardEssentiaTank tank) {
        Integer drained = tank.drain(1, false);
        return drained > 0;
    }

    private boolean tankCanDrainFluid(StandardEssentiaTank tank, Aspect aspect) {
        if (aspect == null) return false;
        if (!(aspect == tank.getAspect())) return false;
        return tankCanDrain(tank);
    }
}
