package me.nentify.playershops.Utils;

import net.minecraft.inventory.IInventory;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

public class ChestUtils {

    public static Optional<Integer> getSlotWithSpaceForItem(IInventory inv, ItemStack itemStack, int stackSize) {
        if (inv.getInventoryStackLimit() <= stackSize) {
            return Optional.empty();
        }

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (inv.getStackInSlot(i) == null) {
                return Optional.of(i);
            }

            ItemStack chestStack = ItemUtils.convertToSponge(inv.getStackInSlot(i));

            if (ItemUtils.equal(itemStack, chestStack)) {
                if ((inv.getInventoryStackLimit() - chestStack.getQuantity()) >= stackSize) {
                    return Optional.of(i);
                }
            }
        }

        return Optional.empty();
    }

    public static void addItemsToSlot(IInventory inv, int slot, ItemStack itemStack) {
        net.minecraft.item.ItemStack stack = inv.getStackInSlot(slot);

        if (stack == null) {
            inv.setInventorySlotContents(slot, ItemUtils.convertFromSponge(itemStack));
        } else {
            net.minecraft.item.ItemStack stackInSlot = inv.getStackInSlot(slot).copy();
            stackInSlot.stackSize = stackInSlot.stackSize + itemStack.getQuantity();
            inv.setInventorySlotContents(slot, stackInSlot);

            System.out.println(stackInSlot.stackSize + " " + itemStack.getQuantity());
        }
    }

    public static boolean hasQuantityOfItems(IInventory inv, ItemStack itemStack, int stackSize) {
        int total = 0;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (inv.getStackInSlot(i) != null) {
                net.minecraft.item.ItemStack stackInSlot = inv.getStackInSlot(i);

                ItemStack chestStack = ItemUtils.convertToSponge(stackInSlot);

                if (ItemUtils.equal(itemStack, chestStack)) {
                    total += chestStack.getQuantity();

                    if (total >= stackSize)
                        return true;
                }
            }
        }

        return false;
    }

    public static void removeItems(IInventory inv, ItemStack itemStack) {
        int toRemove = itemStack.getQuantity();

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (inv.getStackInSlot(i) != null) {
                net.minecraft.item.ItemStack stackInSlot = inv.getStackInSlot(i);

                ItemStack chestStack = ItemUtils.convertToSponge(stackInSlot);

                if (ItemUtils.equal(itemStack, chestStack)) {
                    if (stackInSlot.stackSize >= toRemove) {
                        inv.decrStackSize(i, toRemove);
                        return;
                    } else {
                        toRemove = toRemove - stackInSlot.stackSize;
                        inv.setInventorySlotContents(i, null);
                    }
                }
            }
        }
    }
}
