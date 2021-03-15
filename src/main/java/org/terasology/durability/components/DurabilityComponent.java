/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.durability.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.logic.inventory.ItemDifferentiating;
import org.terasology.engine.network.Replicate;

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
}
