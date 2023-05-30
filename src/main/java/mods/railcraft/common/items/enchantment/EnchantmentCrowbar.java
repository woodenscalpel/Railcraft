package mods.railcraft.common.items.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;

import mods.railcraft.common.items.ItemCrowbar;

public class EnchantmentCrowbar extends Enchantment {

    public EnchantmentCrowbar(String tag, int id, int weight) {
        super(id, weight, EnumEnchantmentType.digger);
        setName("railcraft.crowbar." + tag);
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return stack.getItem() instanceof ItemCrowbar;
    }
}
