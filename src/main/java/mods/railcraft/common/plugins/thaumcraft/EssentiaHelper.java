/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info This code is the property of CovertJaguar and may only be used
 * with explicit written permission unless otherwise specified on the license page at
 * http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.thaumcraft;

import mods.railcraft.common.core.Railcraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;

import java.util.Objects;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EssentiaHelper {

    public static boolean getEssentia(IInventory inv, int slot, Aspect aspect) {
        ItemStack stack = inv.getStackInSlot(slot);

        if (stack != null && stack.getItem() instanceof IEssentiaContainerItem
                && "item.BlockJarFilledItem".equals(stack.getUnlocalizedName())) {
            IEssentiaContainerItem jar = (IEssentiaContainerItem) stack.getItem();
            AspectList aspects = jar.getAspects(stack);
            if (aspects.getAmount(aspect) > 0) {
                aspects.remove(aspect, 1);
                if (aspects.size() == 0) {
                    ItemStack emptyJar = GameRegistry.findItemStack("Thaumcraft", "blockJar", 1);
                    inv.setInventorySlotContents(slot, emptyJar);
                } else jar.setAspects(stack, aspects);
                return true;
            }
        }
        return false;
    }

    public enum EssentiaEnum{
        AIR("aer"),
        EARTH("terra"),
        FIRE("ignis"),
        WATER("aqua"),
        ORDER("ordo"),
        ENTROPY("perditio"),
        VOID("vacuos"),
        LIGHT("lux"),
        WEATHER("tempestas"),
        MOTION("motus"),
        COLD("gelum"),
        CRYSTAL("vitreus"),
        LIFE("victus"),
        POISON("venenum"),
        ENERGY("potentia"),
        EXCHANGE("permutatio"),
        METAL("metallum"),
        DEATH("mortuus"),
        FLIGHT("volatus"),
        DARKNESS("tenebrae"),
        SOUL("spiritus"),
        HEAL("sano"),
        TRAVEL("iter"),
        ELDRITCH("alienis"),
        MAGIC("praecantatio"),
        AURA("auram"),
        TAINT("vitium"),
        SLIME("limus"),
        PLANT("herba"),
        TREE("arbor"),
        BEAST("bestia"),
        FLESH("corpus"),
        UNDEAD("exanimis"),
        MIND("cognitio"),
        SENSES("sensus"),
        MAN("humanus"),
        CROP("messis"),
        MINE("perfodio"),
        TOOL("instrumentum"),
        HARVEST("meto"),
        WEAPON("telum"),
        ARMOR("tutamen"),
        HUNGER("fames"),
        GREED("lucrum"),
        CRAFT("fabrico"),
        CLOTH("pannus"),
        MECHANISM("machina"),
        TRAP("vinculum");


        public String name;
        EssentiaEnum(String name){
            this.name = name;
        }
        public int getInt(){
            return this.ordinal();
        }
        public String getName(){
            return this.name;
        }

        public static int intfromname(String givenname){
            for(EssentiaEnum a : EssentiaEnum.values()){
                if (Objects.equals(a.name, givenname.toLowerCase())){
                    return a.getInt();
                }
            }
            return -1;
        }

    }
}
