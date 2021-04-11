package net.runelite.client.plugins.humanboy.helpers;

import lombok.SneakyThrows;
import net.runelite.api.Player;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.humanboy.HumanBoy;
import net.runelite.client.plugins.humanboy.enums.QueueItemType;

import java.awt.*;
import java.util.LinkedList;

public class Queue
{
	final HumanBoy plugin;
	final Robot robot;
	java.util.Queue<QueueItem> queue = new LinkedList<>();

	@SneakyThrows
	public Queue(HumanBoy plugin)
	{
		this.plugin = plugin;
		this.robot = new Robot();
	}

	private void add(QueueItem queueItem)
	{
		queue.add(queueItem);

		if (queue.size() == 1)
		{
			next(false);
		}
	}

	public void player(Player player)
	{
		add(new QueueItem(QueueItemType.PLAYER, player));
	}

	public void point(Point point)
	{
		add(new QueueItem(QueueItemType.POINT, point));
	}

	public void click()
	{
		add(new QueueItem(QueueItemType.CLICK));
	}

	public void inventoryItem(WidgetItem item)
	{
		inventoryItem(item, false);
	}

	public void inventoryItem(WidgetItem item, boolean canAbort)
	{
		final int x = item.getCanvasLocation().getX();
		final int y = item.getCanvasLocation().getY();
		final int w = item.getCanvasBounds().width;
		final int h = item.getCanvasBounds().height;

		final Point point = new Point(
						plugin.randomNumber(x + 10, x + w - 10),
						plugin.randomNumber(y + 10, y + h - 10)
		);

		add(new QueueItem(QueueItemType.POINT, point, canAbort));
	}

	public void fKey(int fKey)
	{
		add(new QueueItem(QueueItemType.FKEY, fKey));
	}

	public void sleep(int sleep)
	{
		add(new QueueItem(QueueItemType.SLEEP, sleep));
	}

	public void next()
	{
		next(true);
	}

	public void next(boolean poll)
	{
		if (poll)
		{
			queue.poll();
		}

		if (queue.size() > 0)
		{
			QueueItem item = queue.peek();
			QueueItemType type = item.getType();

			if (type == QueueItemType.POINT)
			{
				plugin.getMouse().clickAt(item.getPoint());
			}
			else if (type == QueueItemType.PLAYER)
			{
				plugin.getMouse().clickPlayer(item.getPlayer());
			}
			else if (type == QueueItemType.CLICK)
			{
				plugin.getMouse().click();
				next();
			}
			else if (type == QueueItemType.FKEY)
			{
				robot.keyPress(item.getFkey());
				robot.keyRelease(item.getFkey());
				next();
			}
			else if (type == QueueItemType.SLEEP)
			{
				new Thread(() -> {
					plugin.sleep(item.getSleep());
					next();
				}).start();
			}
		}
	}
}
