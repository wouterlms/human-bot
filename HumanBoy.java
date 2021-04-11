package net.runelite.client.plugins.humanboy;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.humanboy.helpers.Inventory;
import net.runelite.client.plugins.humanboy.helpers.Mouse;
import net.runelite.client.plugins.humanboy.helpers.Ping;
import net.runelite.client.plugins.humanboy.helpers.Queue;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

@PluginDescriptor(
				name = "Human Boy",
				description = ""
)
public class HumanBoy extends Plugin implements KeyListener
{
	@Inject
	private HumanBoyConfig config;

	@Inject
	private Client client;

	@Inject
	private KeyManager keyManager;

	@Inject
	private ItemManager itemManager;

	@Provides
	HumanBoyConfig getConfig(ConfigManager configManager) {
		return configManager.getConfig(HumanBoyConfig.class);
	}

	private Mouse mouse;
	private Inventory inventory;
	private Queue queue;

	private boolean abortMouseMove = false;
	private boolean consumeKeyPress = false;
	private int ping = 0;

	private boolean runTest = false;

	public HumanBoy()
	{
		mouse = new Mouse(this);
		inventory = new Inventory(this);
		queue = new Queue(this);
	}


	@Override
	protected void startUp() throws Exception
	{
		keyManager.registerKeyListener(this);
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
    if (consumeKeyPress)
    {
      e.consume();
      consumeKeyPress = false;
    }
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == 45)
		{
			consumeKeyPress = true;
			abortMouseMove = true;
		}
		else if (e.getKeyCode() == 61)
		{
			consumeKeyPress = true;
			runTest = true;


		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{

	}

	@Subscribe
	private void onGameTick(GameTick event)
	{
		if (runTest)
		{
			runTest = false;
		}
	}

	@Subscribe
	private void onStatChanged(StatChanged event)
	{
		/*
		
		OUDE VERSIE
		
		// tick
        long difference = getDateDiff(lastTick, new Date(), TimeUnit.MILLISECONDS);
        int timeToNextTick = (600 - (int) difference);

        //ping
        WorldResult worldResult = worldService.getWorlds();
        int ping = Ping.ping(worldResult.findWorld(client.getWorld()));

        //log
        addChatMessage("Next tick in " + timeToNextTick + "ms");
        addChatMessage("Ping: " + ping);

        final double GMAUL_MULTIPLIER = 1.5;
        final double PLAYER_MULTIPLIER = 1.90;

        if (ping < 80) ping = 80;
		 */
		Player target = (Player) client.getLocalPlayer().getInteracting();

		if (target == null || event.getSkill() != Skill.RANGED)
			return;

		int distanceToGraniteMaul = inventory.getDistanceBetween(
						inventory.getItem("Granite maul"),
						MouseInfo.getPointerInfo().getLocation()
		);

		int s = config.test1() - ping;

		System.out.println("distance: " +  distanceToGraniteMaul);
		System.out.println("Sleeping: " + s);

		queue.sleep(s);

		queue.inventoryItem(inventory.getItem("Granite maul"));
		queue.sleep(10);
		queue.fKey(112);
		queue.sleep(10);
		queue.point(new Point(randomNumber(573, 590), randomNumber(415, 429)));
		queue.sleep(10);
		queue.click();
		queue.player(target);
		queue.fKey(27);

		//queue.sleep(150 - ping);
		//queue.inventoryItem(inventory.getItem("Tzhaar-ket-om"));
		//queue.player(getClient().getLocalPlayer());
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{

		if (event.getGameState() == GameState.LOGGED_IN) {

			String world = String.valueOf(client.getWorld()).substring(1);

			if (String.valueOf(world.charAt(0)).equals("0")) {
				world = world.substring(1);
			}

			new Ping(this, "oldschool" + world + ".runescape.com");
		}
	}

	public void sleep(final int ms)
	{
		final int random = randomNumber(ms - (ms / 40), ms + (ms / 40));

		try {
			Thread.sleep(random);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public int randomNumber(int min, int max)
	{
		return new Random().nextInt(max - min + 1) + min;
	}

	public double randomNumber(double min, double max)
	{
		return min + (max - min) *  new Random().nextDouble();
	}

	public Client getClient()
	{
		return client;
	}

	public Mouse getMouse()
	{
		return mouse;
	}

	public Queue getQueue()
	{
		return queue;
	}

	public boolean getAbortMouseMove()
	{
		return abortMouseMove;
	}

	public ItemManager getItemManager()
	{
		return itemManager;
	}

	public void setPing(int ping)
	{
		this.ping = ping;
	}
}
