// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.durability.components;

import org.terasology.gestalt.entitysystem.component.EmptyComponent;

/**
 * Adding this to an entity will copy both itself and DurabilityComponent from item to block and from block to item
 * The best example of this is is a torch that loses durability over time.  When you pick it up, it keeps the block's current durability.
 * And when you place it back down it keeps the item's current durability.
 */
public class RetainDurabilityComponent extends EmptyComponent<RetainDurabilityComponent> {
}
