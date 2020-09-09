// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.durability.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.Replicate;
import org.terasology.inventory.logic.ItemDifferentiating;

/**
 * Durability component that enables the item/block to have durability
 */
public class DurabilityComponent implements Component, ItemDifferentiating {
    @Replicate
    public int durability;
    @Replicate
    public int maxDurability;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DurabilityComponent that = (DurabilityComponent) o;

        if (durability != that.durability) {
            return false;
        }
        return maxDurability == that.maxDurability;
    }

    @Override
    public int hashCode() {
        int result = durability;
        result = 31 * result + maxDurability;
        return result;
    }
}
