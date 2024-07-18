/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info This code is the property of CovertJaguar and may only be used
 * with explicit written permission unless otherwise specified on the license page at
 * http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModelJar2 extends ModelBase {
    public ModelRenderer Core;
    public ModelRenderer Brine;
    public ModelRenderer Lid;

    public ModelJar2() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.Core = new ModelRenderer(this, 0, 0);
        this.Core.addBox(-5.0F, -12.0F, -5.0F, 10, 12, 10);
        this.Core.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Core.setTextureSize(64, 32);
        this.Core.mirror = true;
        this.setRotation(this.Core, 0.0F, 0.0F, 0.0F);
        this.Brine = new ModelRenderer(this, 0, 0);
        this.Brine.addBox(-4.0F, -11.0F, -4.0F, 8, 10, 8);
        this.Brine.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Brine.setTextureSize(64, 32);
        this.Brine.mirror = true;
        this.setRotation(this.Brine, 0.0F, 0.0F, 0.0F);
        this.Lid = new ModelRenderer(this, 0, 24);
        this.Lid.addBox(-3.0F, 0.0F, -3.0F, 6, 2, 6);
        this.Lid.setRotationPoint(0.0F, -14.0F, 0.0F);
        this.Lid.setTextureSize(64, 32);
        this.Lid.mirror = true;
    }

    public void renderBrine() {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        this.Brine.render(0.0625F);
        GL11.glDisable(3042);
    }

    public void renderAll() {
        this.Lid.render(0.0625F);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        this.Core.render(0.0625F);
        GL11.glDisable(3042);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
