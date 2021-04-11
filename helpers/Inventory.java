package net.runelite.client.plugins.humanboy.helpers;

import net.runelite.api.ItemComposition;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.humanboy.HumanBoy;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Inventory
{
	private static final int INVENTORY_SIZE = 28;
	private final HumanBoy plugin;

	public Inventory(HumanBoy plugin)
	{
		this.plugin = plugin;
	}

	private WidgetItem getItemInSlot(int index)
	{
		Widget inventoryWidget = plugin.getClient().getWidget(WidgetInfo.INVENTORY);
		return inventoryWidget.getWidgetItem(index);
	}

	public WidgetItem getItem(String item)
	{
		return getFirstMatch(new ArrayList<>(Arrays.asList(item)));
	}

	public WidgetItem getFirstMatch(ArrayList<String> items)
	{
		for (int i = 0; i < INVENTORY_SIZE; i++)
		{
			final WidgetItem item = getItemInSlot(i);
			final ItemComposition itemComposition = plugin.getItemManager().getItemComposition(item.getId());

			if (items.contains(itemComposition.getName()))
			{
				return item;
			}
		}

		return null;
	}

	public WidgetItem getClosest(String item, Point point)
	{
		return getClosest(new ArrayList<>(Arrays.asList(item)), point);
	}

	public WidgetItem getClosest(ArrayList<String> items, Point point)
	{
		WidgetItem closestItem = null;

		// Loop trough inventory slots
		for (int i = 0; i < INVENTORY_SIZE; ++i)
		{
			final WidgetItem currentItem = getItemInSlot(i);
			final ItemComposition itemComposition = plugin.getItemManager().getItemComposition(currentItem.getId());

			// if the item in the current slot is the item we need
			if (items.contains(itemComposition.getName()))
			{
				// if there's no closest item yet, set it to this item
				if (closestItem == null)
				{
					closestItem = currentItem;
				}

				// If there is a closest item
				else
				{
					// calc distance of current item
					int currentItemDistance = getDistanceBetween(currentItem, point);

					// calc distance of closest item
					int closestItemDistance = getDistanceBetween(closestItem, point);

					// If the current item is closer than the "closestItem", set it as the closestItem
					if (currentItemDistance < closestItemDistance)
					{
						closestItem = currentItem;
					}
				}
			}
		}
		return closestItem;
	}

	public int getDistanceBetween(WidgetItem widgetItem, Point point) {
		final net.runelite.api.Point currentItemCanvasLocation = widgetItem.getCanvasLocation();
		final int width = widgetItem.getCanvasBounds().width;
		final int height = widgetItem.getCanvasBounds().height;

		return (int) Math.sqrt(
						Math.pow(point.x - (currentItemCanvasLocation.getX() + (width / 2) + plugin.getClient().getCanvas().getLocationOnScreen().getX()), 2) +
										Math.pow(point.y - (currentItemCanvasLocation.getY() + (height / 2) + plugin.getClient().getCanvas().getLocationOnScreen().getY()), 2));
	}
}
