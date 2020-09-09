// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.durability.events;

import org.terasology.engine.entitySystem.event.Event;

/**
 * Event sent when the durability of an item is reduced.
 */
public class ReduceDurabilityEvent implements Event {
    private final int reduceBy;

    public ReduceDurabilityEvent(int reduceBy) {
        this.reduceBy = reduceBy;
    }

    /**
     * @return The amount to reduce by
     */
    public int getReduceBy() {
        return reduceBy;
    }
}
