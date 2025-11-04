package com.direwolf20.justdirethings.common.items.interfaces;

import com.direwolf20.justdirethings.common.items.data.ItemDataHelper;
import com.direwolf20.justdirethings.common.items.data.ItemDataKeys;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ToggleableItem {
    default boolean getEnabled(ItemStack stack) {
        return ItemDataHelper.getBoolean(stack, ItemDataKeys.TOOL_ENABLED, true); //True by default
    }

    default void toggleEnabled(ItemStack stack, Player player) {
        boolean nowEnabled = ItemDataHelper.toggleBoolean(stack, ItemDataKeys.TOOL_ENABLED, true);
        player.displayClientMessage(Component.translatable("justdirethings.toolenabled", stack.getDisplayName(), nowEnabled ? Component.translatable("justdirethings.enabled") : Component.translatable("justdirethings.disabled")), true);
        if (nowEnabled)
            player.playNotifySound(SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
        else
            player.playNotifySound(SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS, 1.0F, 0.5F);
    }

    static ItemStack getToggleableItem(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof ToggleableItem)
            return mainHand;
        ItemStack offHand = player.getOffhandItem();
        if (offHand.getItem() instanceof ToggleableItem)
            return offHand;
        return ItemStack.EMPTY;
    }
}
