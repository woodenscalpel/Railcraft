/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info This code is the property of CovertJaguar and may only be used
 * with explicit written permission unless otherwise specified on the license page at
 * http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import mods.railcraft.client.render.RenderFakeBlock.RenderInfo;
import mods.railcraft.common.blocks.machine.gamma.*;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.plugins.thaumcraft.StandardEssentiaTank;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderEssentiaLoader extends TileEntitySpecialRenderer {

    private static final float PIPE_OFFSET = 5 * RenderTools.PIXEL;
    private static final RenderInfo backDrop = new RenderInfo();
    private static final RenderInfo pipe = new RenderInfo();

    public RenderEssentiaLoader() {
        backDrop.minX = 0.011f;
        backDrop.minY = 0.01f;
        backDrop.minZ = 0.011f;

        backDrop.maxX = 0.989f;
        backDrop.maxY = 0.99f;
        backDrop.maxZ = 0.989f;

        pipe.texture = EnumMachineGamma.pipeTexture;

        pipe.minX = PIPE_OFFSET;
        pipe.minZ = PIPE_OFFSET;

        pipe.maxX = 1 - PIPE_OFFSET;
        pipe.maxY = RenderTools.PIXEL;
        pipe.maxZ = 1 - PIPE_OFFSET;

        backDrop.texture = new IIcon[1];
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
        TileLoaderEssentiaBase base = (TileLoaderEssentiaBase) tile;
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        // GL11.glEnable(GL11.GL_CULL_FACE);

        backDrop.texture[0] = base.getMachineType().getTexture(7);
        bindTexture(TextureMap.locationBlocksTexture);
        RenderFakeBlock.renderBlock(backDrop, base.getWorld(), x, y, z, false, true);

        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        GL11.glScalef(1f, 0.6f, 1f);

        StandardEssentiaTank tank = base.getTankManager().get(0);

        if (tank.renderData.aspect != null && tank.renderData.amount > 0) {
            int[] displayLists = FluidRenderer.getLiquidDisplayLists(Fluids.LAVA.get());
            if (displayLists != null) {
                GL11.glPushMatrix();

                //  if (FluidRenderer.getFluidTexture(tank.renderData.fluid, false) != null) {

                float cap = tank.getCapacity();
                float level = (float) Math.min(tank.renderData.amount, cap) / cap;

                bindTexture(FluidRenderer.getFluidSheet(Fluids.LAVA.get()));

                RenderTools.setColor(tank.renderData.color);
                GL11.glCallList(displayLists[(int) (level * (float) (FluidRenderer.DISPLAY_STAGES - 1))]);
                //}

                GL11.glPopMatrix();
            }
        }

        // GL11.glScalef(0.994f, 1.05f, 0.994f);
        GL11.glPopAttrib();
        GL11.glPopMatrix();

        if (tile.getClass() == TileEssentiaLoader.class) {
            TileEssentiaLoader loader = (TileEssentiaLoader) tile;

            pipe.minY = RenderTools.PIXEL - loader.getPipeLenght();

            RenderFakeBlock.renderBlock(pipe, loader.getWorld(), x, y, z, false, true);
        }
    }
}
