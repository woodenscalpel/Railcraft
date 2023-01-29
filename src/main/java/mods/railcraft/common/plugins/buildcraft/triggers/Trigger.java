package mods.railcraft.common.plugins.buildcraft.triggers;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import buildcraft.api.statements.IStatementParameter;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class Trigger {

    public abstract boolean isTriggerActive(ForgeDirection side, TileEntity tile, IStatementParameter[] parameter);
}
