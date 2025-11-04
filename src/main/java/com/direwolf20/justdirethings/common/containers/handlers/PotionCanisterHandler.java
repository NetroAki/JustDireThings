package com.direwolf20.justdirethings.common.containers.handlers;

import com.direwolf20.justdirethings.common.items.PotionCanister;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.neoforged.neoforge.items.ItemStackHandler;

public class PotionCanisterHandler extends ItemStackHandler {
    private final ItemStack potionStack;

    public PotionCanisterHandler(ItemStack parent, int size) {
        super(size);
        this.potionStack = parent;
    }

    @Override
    protected void onContentsChanged(int slot) {
        ItemStack stackInSlot = getStackInSlot(slot);
        if (!stackInSlot.isEmpty() && stackInSlot.getItem() instanceof PotionItem) {
            PotionCanister.attemptFill(potionStack);
        }
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return stack.isEmpty() || stack.getItem() instanceof PotionItem || stack.is(Items.GLASS_BOTTLE);
    }
}
