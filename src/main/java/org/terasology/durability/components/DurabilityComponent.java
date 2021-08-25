// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.durability.components;

import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.module.inventory.components.ItemDifferentiating;

/**
 * Durability component that enables the item/block to have durability
 */
public class DurabilityComponent implements Component<DurabilityComponent>, ItemDifferentiating {
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
        if (maxDurability != that.maxDurability) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = durability;
        result = 31 * result + maxDurability;
        return result;
    }

    @Override
    public void copyFrom(DurabilityComponent other) {
        this.durability = other.durability;
        this.maxDurability = other.maxDurability;
    }
}
