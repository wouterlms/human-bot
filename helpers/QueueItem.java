package net.runelite.client.plugins.humanboy.helpers;

import net.runelite.api.Player;
import net.runelite.client.plugins.humanboy.enums.QueueItemType;

import java.awt.*;

public class QueueItem
{
	private QueueItemType type;
	private Point point;
	private Player player;
	private int sleep;
	private int fkey;
	private boolean canAbort = false;

	public QueueItem(QueueItemType type, Point point, boolean canAbort)
	{
		this.type = type;
		this.point = point;
		this.canAbort = canAbort;
	}

	public QueueItem(QueueItemType type, Point point)
	{
		this.type = type;
		this.point = point;
	}

	public QueueItem(QueueItemType type, Player player)
	{
		this.type = type;
		this.player = player;
	}

	public QueueItem(QueueItemType type, int sleepOrFkey)
	{
		this.type = type;

		if (type == QueueItemType.FKEY)
		{
			this.fkey = sleepOrFkey;
		}
		else
		{
			this.sleep = sleepOrFkey;
		}
	}

	public QueueItem(QueueItemType type)
	{
		this.type = type;
	}

	public QueueItemType getType()
	{
		return type;
	}

	public Point getPoint()
	{
		return point;
	}

	public Player getPlayer()
	{
		return player;
	}

	public int getSleep()
	{
		return sleep;
	}

	public int getFkey()
	{
		return fkey;
	}

	public boolean isCanAbort()
	{
		return canAbort;
	}
}
