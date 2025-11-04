package com.direwolf20.justdirethings.common.capabilities;

import com.direwolf20.justdirethings.common.items.data.ItemDataHelper;
import com.direwolf20.justdirethings.common.items.data.ItemDataKeys;
import net.minecraft.world.item.ItemStack;

public class EnergyStorageItemStackNoReceive extends EnergyStorageItemstack {

    public EnergyStorageItemStackNoReceive(int capacity, ItemStack itemStack) {
        super(capacity, itemStack);
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    /**
     * Pocket Generators need to be able to power themselves, but not receive energy from anything else. This is used to do this.
     */
    public int forceReceiveEnergy(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate) {
            energy += energyReceived;
            ItemDataHelper.setInt(itemStack, ItemDataKeys.FORGE_ENERGY, energy);
        }
        return energyReceived;
    }
}
