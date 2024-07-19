/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info This code is the property of CovertJaguar and may only be used
 * with explicit written permission unless otherwise specified on the license page at
 * http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.widgets;

import mods.railcraft.client.gui.GuiContainerRailcraft;
import mods.railcraft.client.render.FluidRenderer;
import mods.railcraft.client.render.RenderTools;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.thaumcraft.StandardEssentiaTank;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EssentiaGaugeWidget3 extends Widget {

    public final StandardEssentiaTank tank;

    public EssentiaGaugeWidget3(StandardEssentiaTank tank, int x, int y, int u, int v, int w, int h) {
        super(x, y, u, v, w, h);
        this.tank = tank;
    }

    @Override
    public ToolTip getToolTip() {
        return tank.getToolTip();
    }

    @Override
    public void draw(GuiContainerRailcraft gui, int guiX, int guiY, int mouseX, int mouseY) {
        if (tank == null) return;
        if (tank.renderData.aspect == null || tank.renderData.amount <= 0) return;

        IIcon fluidIcon = FluidRenderer.getFluidTexture(Fluids.STEAM.get(), false);

        if (fluidIcon == null) return;

        float scale = Math.min(tank.renderData.amount, tank.getCapacity()) / (float) tank.getCapacity();



        gui.bindTexture(FluidRenderer.getFluidSheet(Fluids.STEAM.get()));

        RenderTools.setColor(tank.renderData.color);

        for (int col = 0; col < w / 16; col++) {
            for (int row = 0; row <= h / 16; row++) {
                gui.drawTexturedModelRectFromIcon(guiX + x + col * 16, guiY + y + row * 16 - 1, fluidIcon, 16, 16);
            }
        }

        GL11.glColor4f(1, 1, 1, 1);
        gui.bindTexture(gui.texture);

        gui.drawTexturedModalRect(guiX + x, guiY + y - 1, x, y - 1, w, h - (int) Math.floor(h * scale) + 1);
        gui.drawTexturedModalRect(guiX + x, guiY + y, u, v, w, h);

    }
}
