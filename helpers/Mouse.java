package net.runelite.client.plugins.humanboy.helpers;

import com.google.common.base.Stopwatch;
import lombok.SneakyThrows;
import net.runelite.api.Player;
import net.runelite.client.plugins.humanboy.HumanBoy;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.TimeUnit;

public class Mouse
{
	private final HumanBoy plugin;
	private final Robot robot;

	// Used to generate a random point on a player when
	// the initial mouse move starts
	// They don't get randomized once moving to
	// prevent the mouse from bugging
	private int randomizedPlayerWidth = -1;
	private int randomizedPlayerHeight = -1;

	@SneakyThrows
	public Mouse(HumanBoy plugin)
	{
		this.plugin = plugin;
		this.robot = new Robot();
	}

	/**
	 * Click at a point (should be randomized before)
	 * @param point
	 */
	public void clickAt(Point point)
	{
		humanizeClick(
						new Point(point.x + offsetX(), point.y + offsetY()),
						false
		);
	}

	/**
	 * Click at a point (should be randomized before), can be aborted
	 * @param point
	 * @param canAbort
	 */
	public void clickAt(Point point, boolean canAbort)
	{
		humanizeClick(
						new Point(point.x + offsetX(), point.y + offsetY()),
						canAbort
		);
	}

	/**
	 * Click a player
	 * @param player
	 */
	public void clickPlayer(Player player)
	{
		randomizedPlayerHeight = -1;
		randomizedPlayerWidth = -1;

		humanizeClick(player);
	}

	/**
	 * Move from the current mouse position to a given point
	 * @param to
	 * @param canAbort
	 */
	private void humanizeClick(Point to, boolean canAbort)
	{
		final Point from = new Point(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
		final Integer distance = getDistanceBetweenPoints(from, to);

		final Point offsetPointA = getPointOnLine(from, to, plugin.randomNumber(0.15, 0.4));
		final Point offsetPointB = getPointOnLine(from, to, plugin.randomNumber(0.9, 1.05));

		// Misschien dit nog aanpassen
		offsetPointA.x += limitNumberToRange(plugin.randomNumber(-distance / 4, distance / 4), -100, 100);
		offsetPointB.y += limitNumberToRange(plugin.randomNumber(-distance / 4, distance / 4), -100, 100);

		// Handig voor debuggen


		new Thread(() -> {
			double estimatedTime = 0;
			double t = 0;

			while (t <= 1)
			{
				estimatedTime += 4.5;
				t+= getSpeed(distance, t);
			}

			System.out.println("I think it'll take " + estimatedTime + "ms");
		}).start();


		new Thread(() ->
		{
			Stopwatch stopwatch = Stopwatch.createStarted();

			double t = 0;

			//for (double t = 0; t <= 1; t += speed)
			while(t <= 1)
			{
				// temporary
				if (plugin.getAbortMouseMove())
				{
					return;
				}

				if (canAbort)
				{
					// TODO: do checks
				}

				Point point = new Point(
								cubicBezier(from.x, offsetPointA.x, offsetPointB.x, to.x, t),
								cubicBezier(from.y, offsetPointA.y, offsetPointB.y, to.y, t)
				);

				robot.mouseMove(point.x, point.y);

				t+= getSpeed(distance, t);

				try
				{
					Thread.sleep(1);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			stopwatch.stop();
			System.out.println("Moved mouse " + distance + "px, it took " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");

			plugin.sleep(5);

			click();



			plugin.getQueue().next();
		}).start();
	}

	/**
	 * Move from the current mouse position the a given player
	 * @param player
	 */
	private void humanizeClick(Player player)
	{
		final Point from = new Point(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
		final Integer distance = getDistanceBetweenPoints(
						from, getPlayerPoint(player)
		);

		new Thread(() -> {
			double t = 0;
			while (t <= 1) {
				// TODO: do checks to abort

				Point to = getPlayerPoint(player);

				Point point = new Point(
								cubicBezier(from.x, from.x, to.x, to.x, t),
								cubicBezier(from.y, from.y, to.y, to.y, t)
				);

				robot.mouseMove(point.x, point.y);

				t += getSpeed(distance, t);

				try
				{
					Thread.sleep(1);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}

			click();
			plugin.getQueue().next();
		}).start();
	}

	/**
	 * Send click event to canvas
	 */
	public void click()
	{
		final int x = plugin.getClient().getCanvas().getMousePosition().x;
		final int y = plugin.getClient().getCanvas().getMousePosition().y;

		MouseEvent mouseMoved = new MouseEvent(
						plugin.getClient().getCanvas(), MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), MouseEvent.NOBUTTON, x, y, 1, false
		);

		MouseEvent mousePressed = new MouseEvent(
						plugin.getClient().getCanvas(), MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), MouseEvent.NOBUTTON, x, y, 1, false
		);

		MouseEvent mouseReleased = new MouseEvent(
						plugin.getClient().getCanvas(), MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), MouseEvent.NOBUTTON, x, y, 1, false
		);

		MouseEvent mouseClicked = new MouseEvent(
						plugin.getClient().getCanvas(), MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), MouseEvent.NOBUTTON, x, y, 1, false
		);

		plugin.getClient().getCanvas().dispatchEvent(mousePressed);
		plugin.getClient().getCanvas().dispatchEvent(mouseReleased);
		plugin.getClient().getCanvas().dispatchEvent(mouseClicked);
	}

	/**
	 * Get a randomized point of a player their tile
	 * @param player
	 * @return
	 */
	private Point getPlayerPoint(Player player)
	{
		/*
		final Polygon tile = Perspective.getCanvasTilePoly(plugin.getClient(), player.getLocalLocation());

		int x = (int) tile.getBounds().getCenterX();
		int y = (int) tile.getBounds().getCenterY();

		final int height = (int) tile.getBounds().getHeight();
		final int width = (int) tile.getBounds().getWidth();

		if (randomizedPlayerWidth == -1 && randomizedPlayerHeight == -1)
		{
			randomizedPlayerWidth = plugin.randomNumber(-width / 4, width / 4);
			randomizedPlayerHeight = plugin.randomNumber(-height / 2, 0);
		}

		x += offsetX() + randomizedPlayerWidth;
		y += offsetY() + randomizedPlayerHeight;

		 */

		//int x = player.getLocalLocation().getX();
		//int y = player.getLocalLocation().getX();

		final Rectangle2D bounds = player.getCanvasTilePoly().getBounds2D();

		int x = (int) bounds.getX() + offsetX();
		int y = (int) bounds.getY() + offsetY();

		int h = (int) bounds.getHeight();
		int w = (int) bounds.getWidth();

		if (randomizedPlayerWidth == -1 && randomizedPlayerHeight == -1)
		{
			randomizedPlayerWidth = plugin.randomNumber(-w / 4, w / 4);
			randomizedPlayerHeight = plugin.randomNumber(-h / 2, h / 2);
		}

		return new Point(x + w / 2 + randomizedPlayerWidth, y - h / 2 + randomizedPlayerHeight);
	}

	/**
	 * Calculate mouse speed
	 * @param t
	 * @return
	 */
	private double getSpeed(int d, double t)
	{
		double speedBezier;

		if (d < 100) {
			speedBezier = cubicBezier(0.6, 0.98, 0.7, 0.5, t) / 15;
		} else {
			speedBezier = cubicBezier(0.34, 0.98, 0.7, 0.05, t) / 20;
		}

		return limitNumberToRange(speedBezier, 0.002, 1.0);
	}

	/**
	 * Limit number to min and max
	 * @param number
	 * @param min
	 * @param max
	 * @return
	 */
	private Integer limitNumberToRange(Integer number, Integer min, Integer max)
	{
		return Math.min(Math.max(number, min), max);
	}

	private Double limitNumberToRange(Double number, Double min, Double max)
	{
		return Math.min(Math.max(number, min), max);
	}

	/**
	 * Get distance between 2 points
	 * @param from
	 * @param to
	 * @return
	 */
	private Integer getDistanceBetweenPoints(Point from, Point to)
	{
		return (int) Math.sqrt(Math.pow(from.getX() - to.getX(), 2) + Math.pow(from.getY() - to.getY(), 2));
	}

	/**
	 * Get a point on a line by t (0...1)
	 * @param from
	 * @param to
	 * @param t
	 * @return
	 */
	private Point getPointOnLine(Point from, Point to, Double t)
	{
		return new Point((int) (((1 - t) * from.getX() + t * to.getX())), (int) (((1 - t) * from.getY() + t * to.getY())));
	}

	/**
	 * Create a cubic bezier by t (0...1)
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @param t
	 * @return
	 */
	private Integer cubicBezier(Integer a, Integer b, Integer c, Integer d, Double t)
	{
		final Double value = Math.pow((1 - t), 3) * a + 3 * t * Math.pow(1 - t, 2) * b + 3 * Math.pow(t, 2) * (1 - t) * c + Math.pow(t, 3) * d;
		return value.intValue();
	}

	private Double cubicBezier(Double a, Double b, Double c, Double d, Double t)
	{
		final Double value = Math.pow((1 - t), 3) * a + 3 * t * Math.pow(1 - t, 2) * b + 3 * Math.pow(t, 2) * (1 - t) * c + Math.pow(t, 3) * d;
		return value;
	}

	/**
	 * Get the client offset X
	 * @return
	 */
	private Integer offsetX()
	{
		return plugin.getClient().getCanvas().getLocationOnScreen().x;
	}

	/**
	 * Get the client offset Y
	 * @return
	 */
	private Integer offsetY()
	{
		return plugin.getClient().getCanvas().getLocationOnScreen().y;
	}
}
