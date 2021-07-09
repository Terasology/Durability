// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.durability.components;

import org.terasology.engine.world.block.ForceBlockActive;
import org.terasology.engine.world.block.items.AddToBlockBasedItem;
import org.terasology.gestalt.entitysystem.component.EmptyComponent;


/**
 * Reduces 1 durability every 5 seconds
 */
@AddToBlockBasedItem
@ForceBlockActive
public class OverTimeDurabilityReduceComponent extends EmptyComponent<OverTimeDurabilityReduceComponent> {
}
