/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info This code is the property of CovertJaguar and may only be used
 * with explicit written permission unless otherwise specified on the license page at
 * http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.gamma;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.inventory.AdjacentInventoryCache;
import mods.railcraft.common.util.inventory.InvFilteredHelper;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventorySorter;
import mods.railcraft.common.util.inventory.ItemStackMap;
import mods.railcraft.common.util.inventory.ItemStackSet;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.ITileFilter;

public class TileItemLoader extends TileLoaderItemBase {

    private final Map<ItemStack, Short> transferredItems = new ItemStackMap<Short>();
    private final Set<ItemStack> checkedItems = new ItemStackSet();
    private final AdjacentInventoryCache invCache = new AdjacentInventoryCache(this, tileCache, new ITileFilter() {

        @Override
        public boolean matches(TileEntity tile) {
            return !(tile instanceof TileItemLoader);
        }
    }, InventorySorter.SIZE_DECENDING);
    private final InventoryMapper invBuffer;
    private final LinkedList<IInventory> chests = new LinkedList<IInventory>();

    public TileItemLoader() {
        super();
        setInventorySize(9);
        invBuffer = new InventoryMapper(this, false);
    }

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineGamma.ITEM_LOADER;
    }

    @Override
    public IIcon getIcon(int side) {
        return getMachineType().getTexture(side);
    }

    @Override
    public Slot getBufferSlot(int id, int x, int y) {
        return new Slot(this, id, x, y);
    }

    public ForgeDirection getOrientation() {
        return ForgeDirection.DOWN;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (Game.isNotHost(getWorld())) {
            return;
        }

        movedItemCart = false;

        EntityMinecart cart = CartTools.getMinecartOnSide(worldObj, xCoord, yCoord, zCoord, 0.1f, getOrientation());

        if (cart != currentCart) {
            setPowered(false);
            currentCart = cart;
            transferredItems.clear();
            cartWasSent();
        }

        if (cart == null) {
            return;
        }

        if (!canHandleCart(cart)) {
            sendCart(cart);
            return;
        }

        if (isPaused()) return;

        chests.clear();
        chests.addAll(invCache.getAdjacentInventories());
        chests.addFirst(invBuffer);

        checkedItems.clear();

        IInventory cartInv = (IInventory) cart;

        switch (getMode()) {
            case TRANSFER: {
                boolean hasFilter = false;
                for (ItemStack filter : getItemFilters().getContents()) {
                    if (filter == null) {
                        continue;
                    }
                    if (!checkedItems.add(filter)) {
                        continue;
                    }
                    hasFilter = true;
                    Short numMoved = transferredItems.get(filter);
                    if (numMoved == null) {
                        numMoved = 0;
                    }

                    InvFilteredHelper helper = InvFilteredHelper
                            .filteredByStacks(isMatchByNBT(), isMatchByMetadata(), filter);

                    if (numMoved < helper.countItems(getItemFilters())) {
                        ItemStack moved = helper.moveOneItem(chests, cartInv);
                        if (moved != null) {
                            movedItemCart = true;
                            numMoved++;
                            transferredItems.put(moved, numMoved);
                            break;
                        }
                    }
                }
                if (!hasFilter) {
                    ItemStack moved = InvFilteredHelper.acceptAll().moveOneItem(chests, cartInv);
                    if (moved != null) {
                        movedItemCart = true;
                        break;
                    }
                }
                break;
            }
            case STOCK: {
                for (ItemStack filter : getItemFilters().getContents()) {
                    if (filter == null) {
                        continue;
                    }
                    if (!checkedItems.add(filter)) {
                        continue;
                    }

                    InvFilteredHelper helper = InvFilteredHelper
                            .filteredByStacks(isMatchByNBT(), isMatchByMetadata(), filter);

                    int stocked = helper.countItems(cartInv);
                    if (stocked < helper.countItems(getItemFilters())) {
                        ItemStack moved = helper.moveOneItem(chests, cartInv);
                        if (moved != null) {
                            movedItemCart = true;
                            break;
                        }
                    }
                }
                break;
            }
            case EXCESS: {
                for (ItemStack filter : getItemFilters().getContents()) {
                    if (filter == null) {
                        continue;
                    }
                    if (!checkedItems.add(filter)) {
                        continue;
                    }

                    InvFilteredHelper helper = InvFilteredHelper
                            .filteredByStacks(isMatchByNBT(), isMatchByMetadata(), filter);

                    int stocked = helper.countItems(chests);
                    if (stocked > helper.countItems(getItemFilters())) {
                        ItemStack moved = helper.moveOneItem(chests, cartInv);
                        if (moved != null) {
                            movedItemCart = true;
                            break;
                        }
                    }
                }
                if (!movedItemCart) {
                    movedItemCart = InvFilteredHelper
                            .filteredByStacks(isMatchByNBT(), isMatchByMetadata(), getItemFilters().getContents())
                            .invert().moveOneItem(chests, cartInv) != null;
                }
                break;
            }
            case ALL: {
                boolean hasFilter = false;
                for (ItemStack filter : getItemFilters().getContents()) {
                    if (filter == null) {
                        continue;
                    }
                    if (!checkedItems.add(filter)) {
                        continue;
                    }
                    hasFilter = true;

                    ItemStack moved = InvFilteredHelper.filteredByStacks(isMatchByNBT(), isMatchByMetadata(), filter)
                            .moveOneItem(chests, cartInv);
                    if (moved != null) {
                        movedItemCart = true;
                        break;
                    }
                }
                if (!hasFilter) {
                    ItemStack moved = InvFilteredHelper.acceptAll().moveOneItem(chests, cartInv);
                    if (moved != null) {
                        movedItemCart = true;
                        break;
                    }
                }
                break;
            }
        }

        if (movedItemCart) {
            setPowered(false);
        }

        EnumRedstoneMode state = getRedstoneModeController().getButtonState();
        if (state != EnumRedstoneMode.MANUAL && !isPowered() && shouldSendCart(cart)) {
            sendCart(cart);
        }
    }

    @Override
    protected boolean shouldSendCart(EntityMinecart cart) {
        if (!(cart instanceof IInventory)) return true;
        IInventory cartInv = (IInventory) cart;
        EnumRedstoneMode state = getRedstoneModeController().getButtonState();
        if (!movedItemCart && state != EnumRedstoneMode.COMPLETE) {
            if (state == EnumRedstoneMode.PARTIAL) {
                if (!InvTools.isInventoryEmpty(cartInv)) {
                    return true;
                }
            } else {
                return true;
            }
        } else if (getMode() == EnumTransferMode.TRANSFER && isTransferComplete(getItemFilters().getContents())) {
            return true;
        } else if (getMode() == EnumTransferMode.STOCK && isStockComplete(cartInv, getItemFilters().getContents())) {
            return true;
        } else if (getMode() == EnumTransferMode.EXCESS && isExcessComplete(chests, getItemFilters().getContents())) {
            return true;
        } else if (getMode() == EnumTransferMode.ALL && isAllComplete(cartInv, getItemFilters().getContents())) {
            return true;
        } else if (!movedItemCart && InvTools.isInventoryFull(cartInv, getOrientation().getOpposite())) {
            return true;
        }
        return false;
    }

    private boolean isTransferComplete(ItemStack[] filters) {
        checkedItems.clear();
        boolean hasFilter = false;
        for (ItemStack filter : filters) {
            if (filter == null) {
                continue;
            }
            if (!checkedItems.add(filter)) {
                continue;
            }
            hasFilter = true;
            Short numMoved = transferredItems.get(filter);

            if (numMoved == null || numMoved < InvFilteredHelper
                    .filteredByStacks(isMatchByNBT(), isMatchByMetadata(), filter).countItems(getItemFilters())) {
                return false;
            }
        }
        return hasFilter;
    }

    private boolean isStockComplete(IInventory cart, ItemStack[] filters) {
        checkedItems.clear();
        for (ItemStack filter : filters) {
            if (filter == null) {
                continue;
            }
            if (!checkedItems.add(filter)) {
                continue;
            }

            InvFilteredHelper helper = InvFilteredHelper.filteredByStacks(isMatchByNBT(), isMatchByMetadata(), filter);

            int stocked = helper.countItems(cart);
            if (stocked < helper.countItems(getItemFilters())) {
                return false;
            }
        }
        return true;
    }

    private boolean isExcessComplete(List<IInventory> chests, ItemStack[] filters) {
        checkedItems.clear();
        int max = 0;
        for (ItemStack filter : filters) {
            if (filter == null) {
                continue;
            }
            if (!checkedItems.add(filter)) {
                continue;
            }

            InvFilteredHelper helper = InvFilteredHelper.filteredByStacks(isMatchByNBT(), isMatchByMetadata(), filter);

            int stocked = helper.countItems(chests);
            max += filter.stackSize;
            if (stocked > helper.countItems(getItemFilters())) {
                return false;
            }
        }
        return InvFilteredHelper.acceptAll().countItems(chests) <= max;
    }

    private boolean isAllComplete(IInventory cart, ItemStack[] filters) {
        checkedItems.clear();
        boolean hasFilter = false;
        for (ItemStack filter : filters) {
            if (filter == null) {
                continue;
            }
            if (!checkedItems.add(filter)) {
                continue;
            }
            hasFilter = true;

            if (InvFilteredHelper.filteredByStacks(isMatchByNBT(), isMatchByMetadata(), filter).countItems(cart) > 0) {
                return false;
            }
        }
        return hasFilter;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.LOADER_ITEM, player, worldObj, xCoord, yCoord, zCoord);
        return true;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }
}
