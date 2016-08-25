package me.nentify.playershops.Utils;

import org.spongepowered.api.item.inventory.ItemStack;

public class ItemUtils {

    public static ItemStack convertToSponge(net.minecraft.item.ItemStack itemStack) {
        return (ItemStack) (Object) itemStack;
    }

    public static net.minecraft.item.ItemStack convertFromSponge(ItemStack itemStack) {
        return (net.minecraft.item.ItemStack) (Object) itemStack;
    }

    public static boolean equal(ItemStack stack1, ItemStack stack2) {
        ItemStack first = stack1.copy();
        ItemStack second = stack2.copy();

        first.setQuantity(1);
        second.setQuantity(1);

        return first.equalTo(second);
    }
}
