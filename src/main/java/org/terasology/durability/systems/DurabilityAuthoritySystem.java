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
package org.terasology.durability.systems;

import org.joml.Vector3i;
import org.terasology.durability.components.DurabilityComponent;
import org.terasology.durability.components.OverTimeDurabilityReduceComponent;
import org.terasology.durability.components.RetainDurabilityComponent;
import org.terasology.durability.events.DurabilityExhaustedEvent;
import org.terasology.durability.events.DurabilityReducedEvent;
import org.terasology.durability.events.ReduceDurabilityEvent;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.health.DestroyEvent;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.registry.In;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.entity.damage.BlockDamageModifierComponent;
import org.terasology.world.block.items.OnBlockItemPlaced;
import org.terasology.world.block.items.OnBlockToItem;

/**
 * Authority system that handles reducing durability and destroying items
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class DurabilityAuthoritySystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    @In
    private Time time;
    @In
    private EntityManager entityManager;
    @In
    private WorldProvider worldProvider;
    @In
    private BlockManager blockManager;

    private long tickLength = 5000;
    private long lastModified;

    @Override
    public void update(float delta) {
        long gameTimeInMs = time.getGameTimeInMs();
        if (lastModified + tickLength < gameTimeInMs) {
            for (EntityRef entityRef : entityManager.getEntitiesWith(OverTimeDurabilityReduceComponent.class, DurabilityComponent.class)) {
                entityRef.send(new ReduceDurabilityEvent(1));
            }

            lastModified = gameTimeInMs;
        }
    }

    /**
     * Reduces the durability of a tool when it is used to destroy a block.
     * It does so by sending a new ReduceDurability event.
     *
     * @param event          The Destroy event
     * @param entity         The entity that instigated it
     * @param blockComponent The block component of the entity.
     */
    @ReceiveEvent(priority = EventPriority.PRIORITY_CRITICAL)
    public void reduceItemDurabilityOnBlockDestroyed(DestroyEvent event, EntityRef entity, BlockComponent blockComponent) {
        EntityRef tool = event.getDirectCause();
        DurabilityComponent durabilityComponent = tool.getComponent(DurabilityComponent.class);
        if (durabilityComponent != null) {
            Block block = blockComponent.getBlock();
            Iterable<String> categoriesIterator = block.getBlockFamily().getCategories();
            if (isTheRightTool(categoriesIterator, event.getDamageType())) {
                // It was the right tool for the job, so reduce the durability
                tool.send(new ReduceDurabilityEvent(1));
            }
        }
    }

    /**
     * Sent every time the durability on an entity should be reduced.
     * This event is sent before the durability is reduced
     * <p>
     * Reduces the durability by the specified amount.
     *
     * @param event               The event that was sent
     * @param entity              The entity sending the event
     * @param durabilityComponent The durability component of the entity
     */
    @ReceiveEvent
    public void reduceDurability(ReduceDurabilityEvent event, EntityRef entity, DurabilityComponent durabilityComponent) {
        durabilityComponent.durability -= event.getReduceBy();
        if (durabilityComponent.durability < 0) {
            durabilityComponent.durability = 0;
        }
        entity.saveComponent(durabilityComponent);

        entity.send(new DurabilityReducedEvent());
    }

    /**
     * Sent after the durability on an item is reduced.
     * <p>
     * Checks to see if the durability is zero
     *
     * @param event               The event
     * @param entity              The entity sending the event
     * @param durabilityComponent The durability component of the entity
     */
    @ReceiveEvent
    public void checkIfDurabilityExhausted(DurabilityReducedEvent event, EntityRef entity, DurabilityComponent durabilityComponent) {
        if (durabilityComponent.durability == 0) {
            entity.send(new DurabilityExhaustedEvent());
        }
    }

    /**
     * Event sent when the durability of an entity reaches zero.
     * <p>
     * This overload handles items.
     *
     * @param event               The event sent
     * @param entity              The entity sending the event
     * @param durabilityComponent The durability component of the entity
     * @param itemComponent       The item component of the entity
     */
    @ReceiveEvent(priority = EventPriority.PRIORITY_TRIVIAL)
    public void destroyItemOnZeroDurability(DurabilityExhaustedEvent event, EntityRef entity, DurabilityComponent durabilityComponent, ItemComponent itemComponent) {
        entity.destroy();
        event.consume();
    }

    /**
     * Event sent when the durability of an entity reaches zero
     * <p>
     * This overload handles blocks.
     *
     * @param event               The event sent
     * @param entity              The entity sending the event
     * @param durabilityComponent The durability component of the entity
     * @param blockComponent      The block component of the entity
     */
    @ReceiveEvent(priority = EventPriority.PRIORITY_TRIVIAL)
    public void destroyItemOnZeroDurability(DurabilityExhaustedEvent event, EntityRef entity, DurabilityComponent durabilityComponent, BlockComponent blockComponent) {
        worldProvider.setBlock(blockComponent.getPosition(new Vector3i()), blockManager.getBlock(BlockManager.AIR_ID));
        event.consume();
    }

    private boolean isTheRightTool(Iterable<String> categoriesIterator, Prefab damageType) {
        if (categoriesIterator.iterator().hasNext()) {
            BlockDamageModifierComponent blockDamage = damageType.getComponent(BlockDamageModifierComponent.class);
            if (blockDamage == null) {
                return false;
            }
            for (String category : categoriesIterator) {
                if (blockDamage.materialDamageMultiplier.containsKey(category)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }


    /**
     * Event sent when a block is destroyed and the entity is converted to an item.
     *
     * @param event                     The event sent
     * @param blockEntity               The entity sending the event
     * @param retainDurabilityComponent Marker indicating that durability should be conserved
     * @param durabilityComponent       The durability component
     */
    @ReceiveEvent
    public void dropBlockWithRetainDurability(OnBlockToItem event, EntityRef blockEntity,
                                              RetainDurabilityComponent retainDurabilityComponent,
                                              DurabilityComponent durabilityComponent) {
        saveRetainDurability(retainDurabilityComponent, durabilityComponent, event.getItem());
    }

    /**
     * Event sent when an item is placed and the entity converted to a block.
     *
     * @param event                     The event sent
     * @param itemEntity                The entity sending the event
     * @param retainDurabilityComponent Marker indicating that durability should be conserved
     * @param durabilityComponent       The durability component
     */
    @ReceiveEvent
    public void placeBlockWithRetainDurability(OnBlockItemPlaced event, EntityRef itemEntity,
                                               RetainDurabilityComponent retainDurabilityComponent,
                                               DurabilityComponent durabilityComponent) {
        saveRetainDurability(retainDurabilityComponent, durabilityComponent, event.getPlacedBlock());
    }

    /**
     * Saves the changes made to the durability component or adds a new one
     *
     * @param retainDurabilityComponent The marker component
     * @param durabilityComponent The durability component to save
     * @param entity The entity that is being changed
     */
    private void saveRetainDurability(RetainDurabilityComponent retainDurabilityComponent, DurabilityComponent durabilityComponent, EntityRef entity) {
        if (entity.hasComponent(RetainDurabilityComponent.class)) {
            entity.saveComponent(retainDurabilityComponent);
        } else {
            entity.addComponent(retainDurabilityComponent);
        }

        if (entity.hasComponent(DurabilityComponent.class)) {
            entity.saveComponent(durabilityComponent);
        } else {
            entity.addComponent(durabilityComponent);
        }
    }
}
