/*
 * ****************************************************************************** Copyright 2011-2015 CovertJaguar This
 * work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ******************************************************************************
 */
package mods.railcraft.common.plugins.thaumcraft;

import mods.railcraft.api.carts.ITrainTransferHelper;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraftforge.fluids.Fluid;
import thaumcraft.api.aspects.Aspect;

/**
 * Replaces ILiquidTransfer with a simpler interface for moving Fluids between Minecarts.
 * <p/>
 * Created by CovertJaguar on 5/9/2015.
 *
 * @see ITrainTransferHelper
 */
public interface IEssentiaCart {

    /**
     * This function controls whether a cart can pass push or pull requests. This function is only called if the cart
     * cannot fulfill the request itself.
     * <p/>
     * If this interface is not implemented, a default value will be inferred based on the size of the tanks of the
     * Minecart. Anything with eight or more buckets will be assumed to allow passage, but only if the contained fluid
     * matches the request.
     *
     * @return true if can pass push and pull requests
    boolean canPassFluidRequests(Fluid fluid);
     */

    boolean canAcceptPushedEssentia(EntityMinecart requester, Aspect aspect,int amt);

    /**
     * This function controls whether a cart will fulfill a pull request for a specific Fluid. Even if this function
     * returns true, there still must be a tank that can extract the Fluid in question before it can be removed from the
     * cart.
     * <p/>
     * If this interface is not implemented, it is assumed to be true.
     *
     * @param requester the EntityMinecart that initiated the action
     * @return
     */
    boolean canProvidePulledEssentia(EntityMinecart requester,Aspect aspect,int amt);

    /**
     * Set by the Liquid Loader while filling, primarily used for rendering a visible change while being filled.
     *
     * @param filling
     */
    void setFilling(boolean filling);
}
