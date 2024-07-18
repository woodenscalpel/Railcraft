/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info This code is the property of CovertJaguar and may only be used
 * with explicit written permission unless otherwise specified on the license page at
 * http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.carts;

import mods.railcraft.client.render.FluidRenderer;
import mods.railcraft.client.render.RenderFakeBlock;
import mods.railcraft.client.render.RenderFakeBlock.RenderInfo;
import mods.railcraft.client.render.RenderTools;
import mods.railcraft.client.render.models.ModelJar2;
import mods.railcraft.common.carts.EntityCartEssentiaTank;
import mods.railcraft.common.carts.EntityCartTank;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.plugins.thaumcraft.StandardEssentiaTank;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartContentRendererEssentia extends CartContentRenderer {

    private final RenderInfo fillBlock = new RenderInfo(0.4f, 0.0f, 0.4f, 0.6f, 0.999f, 0.6f);

    public CartContentRendererEssentia() {
        fillBlock.texture = new IIcon[6];
    }

    private void renderTank(RenderCart renderer, EntityMinecart cart, float light, float time, int x, int y, int z) {
        EntityCartEssentiaTank cartTank = (EntityCartEssentiaTank) cart;
        StandardEssentiaTank tank = cartTank.getTankManager().get(0);
        if (tank.renderData.aspect != null && tank.renderData.amount > 0) {
            int[] displayLists = FluidRenderer.getLiquidDisplayLists(Fluids.STEAM.get());
            if (displayLists != null) {
                GL11.glPushMatrix();

                GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

                GL11.glTranslatef(0, 0.0625f, 0);

                float cap = tank.getCapacity();
                float level = Math.min(tank.renderData.amount, cap) / cap;

                renderer.bindTex(FluidRenderer.getFluidSheet(Fluids.STEAM.get()));
                RenderTools.setColor(tank.renderData.color);
                GL11.glCallList(displayLists[(int) (level * (float) (FluidRenderer.DISPLAY_STAGES - 1))]);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);


                /*
                if (cartTank.isFilling()) {
                    ResourceLocation texSheet = FluidRenderer
                            .setupFlowingLiquidTexture(tank.renderData.fluid, fillBlock.texture);
                    if (texSheet != null) {
                        renderer.bindTex(texSheet);
                        RenderFakeBlock.renderBlockForEntity(fillBlock, cart.worldObj, x, y, z, false, true);
                    }
                }

                 */

                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }
        }
    }

    private void renderFilterItem(RenderCart renderer, EntityCartEssentiaTank cart, float light, float time, int x, int y,
            int z) {
        if (!cart.hasFilter()) return;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        // GL11.glEnable(GL11.GL_CULL_FACE);

        EntityItem item = new EntityItem(null, 0.0D, 0.0D, 0.0D, cart.getFilterItem().copy());
        item.getEntityItem().stackSize = 1;
        item.hoverStart = 0.0F;

        float scale = 1.2F;

        GL11.glPushMatrix();
        GL11.glRotatef(90.F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, -0.6F, 0.68F);
        GL11.glScalef(scale, scale, scale);
        renderItem(item);
        GL11.glPopMatrix();

        GL11.glRotatef(-90.F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, -0.6F, 0.68F);
        GL11.glScalef(scale, scale, scale);
        renderItem(item);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void renderItem(EntityItem item) {
        RenderItem.renderInFrame = true;
        RenderManager.instance.renderEntityWithPosYaw(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        if (!RenderManager.instance.options.fancyGraphics) {
            GL11.glRotatef(180, 0, 1, 0);
            RenderManager.instance.renderEntityWithPosYaw(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
            GL11.glRotatef(-180, 0, 1, 0);
        }
        RenderItem.renderInFrame = false;
    }

    @Override
    public void render(RenderCart renderer, EntityMinecart cart, float light, float time) {
        super.render(renderer, cart, light, time);
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glTranslatef(0.0F, 0.3125F, 0.0F);
        GL11.glRotatef(90F, 0.0F, 1.0F, 0.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int x = (int) (Math.floor(cart.posX));
        int y = (int) (Math.floor(cart.posY));
        int z = (int) (Math.floor(cart.posZ));

        ModelJar2 mj = new ModelJar2();

        GL11.glPushMatrix();
        GL11.glTranslatef(0,-0.2F,0);
        GL11.glScalef(0.4F,0.4F,0.4F);
        renderTank(renderer, cart, light, time, x, y, z);
        GL11.glPopMatrix();

        EntityCartEssentiaTank cartTank = (EntityCartEssentiaTank) cart;
        renderFilterItem(renderer, cartTank, light, time, x, y, z);

        renderer.bindTex(new ResourceLocation(RailcraftConstants.CART_TEXTURE_FOLDER + "cart_jar.png"));
        GL11.glRotatef(180F,1,0,0);
        GL11.glTranslatef(0,0.5F,0);

        //mj.renderBrine();
        mj.renderAll();


        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
