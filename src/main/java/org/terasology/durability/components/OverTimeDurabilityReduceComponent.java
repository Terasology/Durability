// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.durability.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.world.block.ForceBlockActive;
import org.terasology.engine.world.block.items.AddToBlockBasedItem;

@AddToBlockBasedItem
@ForceBlockActive
/**
 * Reduces 1 durability every 5 seconds
 */
public class OverTimeDurabilityReduceComponent implements Component {
}
