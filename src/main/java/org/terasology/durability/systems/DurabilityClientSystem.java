// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.durability.systems;

import org.joml.Vector2i;
import org.terasology.assets.ResourceUrn;
import org.terasology.durability.components.DurabilityComponent;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.rendering.assets.texture.Texture;
import org.terasology.engine.rendering.assets.texture.TextureUtil;
import org.terasology.rendering.nui.layers.ingame.inventory.GetItemTooltip;
import org.terasology.rendering.nui.layers.ingame.inventory.InventoryCellRendered;
import org.terasology.engine.utilities.Assets;
import org.terasology.joml.geom.Rectanglei;
import org.terasology.nui.Canvas;
import org.terasology.nui.Color;
import org.terasology.nui.widgets.TooltipLine;

/**
 * System that handles the UI parts of displaying the durability for the client.
 */
@RegisterSystem(RegisterMode.CLIENT)
public class DurabilityClientSystem extends BaseComponentSystem {

    /**
     * An event sent after the inventory cell has been rendered.
     * Used to draw the durability bar over the item in the cell.
     *
     * @param event The event sent
     * @param entity The entity sending the event
     * @param durability
     */
    @ReceiveEvent
    public void drawDurabilityBar(InventoryCellRendered event, EntityRef entity, DurabilityComponent durability) {
        Canvas canvas = event.getCanvas();

        Vector2i size = canvas.size();

        int minX = (int) (size.x * 0.1f);
        int maxX = (int) (size.x * 0.9f);

        int minY = (int) (size.y * 0.8f);
        int maxY = (int) (size.y * 0.9f);

        float durabilityPercentage = 1f * durability.durability / durability.maxDurability;

        if (durabilityPercentage != 1f) {
            ResourceUrn backgroundTexture = TextureUtil.getTextureUriForColor(Color.WHITE);

            final Color terasologyColor = getTerasologyColorForDurability(durabilityPercentage);

            ResourceUrn barTexture = TextureUtil.getTextureUriForColor(terasologyColor);

            canvas.drawTexture(Assets.get(backgroundTexture, Texture.class).get(), new Rectanglei(minX, minY, maxX,
                    maxY));
            int durabilityBarLength = (int) (durabilityPercentage * (maxX - minX - 1));
            int durabilityBarHeight = maxY - minY - 1;
            canvas.drawTexture(Assets.get(barTexture, Texture.class).get(),
                    new Rectanglei(minX + 1, minY + 1).setSize(durabilityBarLength, durabilityBarHeight));
        }
    }

    private Color getTerasologyColorForDurability(float durabilityPercentage) {
        final java.awt.Color awtColor = java.awt.Color.getHSBColor(0.33f * durabilityPercentage, 1f, 0.8f);

        return new Color(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
    }

    /**
     * Event sent when the tooltip is requested.
     * Event is modified to include a line on durability.
     *
     * @param event The event sent
     * @param entity The entity sending the event
     * @param durability The durability component of the entity
     */
    @ReceiveEvent
    public void getDurabilityItemTooltip(GetItemTooltip event, EntityRef entity, DurabilityComponent durability) {
        float durabilityPercentage = 1f * durability.durability / durability.maxDurability;

        final Color color = getTerasologyColorForDurability(durabilityPercentage);
        event.getTooltipLines().add(new TooltipLine("Durability: " + durability.durability + "/" + durability.maxDurability, color));
    }
}
