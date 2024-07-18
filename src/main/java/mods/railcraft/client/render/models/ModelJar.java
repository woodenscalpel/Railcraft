/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info This code is the property of CovertJaguar and may only be used
 * with explicit written permission unless otherwise specified on the license page at
 * http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.models;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModelJar extends ModelTextured {

    public ModelJar() {
        super("jar");
        renderer.setTextureSize(64, 32);
        /*
        renderer.addBox(-8F, -8F, -8F, 16, 16, 16);
        renderer.rotationPointX = 8F;
        renderer.rotationPointY = 8F;
        renderer.rotationPointZ = 8F;
         */

        setTextureOffset("jar.sides", 0, 22);
        setTextureOffset("jar.lid", 0, 0);

        renderer.addBox("sides", -5F, -8F, -5F, 10, 12, 10);
        renderer.addBox("lid", -3F, 6F, -3F, 6, 2, 6);
        //renderer.addBox(-5.0F, -8.0F, -5.0F, 10, 16, 10);
        //renderer.addBox(-4.0F, 6.0F, -4.0F, 8, 2, 8);
        renderer.setRotationPoint(8F, 8F, 8F);
    }
}
