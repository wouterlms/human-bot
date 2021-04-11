package net.runelite.client.plugins.humanboy.helpers;

import com.google.common.base.Stopwatch;
import net.runelite.client.plugins.humanboy.HumanBoy;
import net.runelite.client.plugins.pkplugin.PkPlugin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class Ping {

	private HumanBoy plugin;
	private String host;
	private int totalMs = 0;
	private int count = 0;
	private Stopwatch stopwatch;

	public Ping(HumanBoy plugin, String host) {
		this.plugin = plugin;
		this.host = host;

		System.out.println("Pinging " + host);
		sendPingRequest();
	}

	private void sendPingRequest() {
		new Thread(() -> {
			count += 1;
			stopwatch = Stopwatch.createStarted();

			try {
				try (Socket soc = new Socket()) {
					soc.connect(new InetSocketAddress(host, 43594), 2000);
				}
				stopwatch.stop();
				totalMs += stopwatch.elapsed(TimeUnit.MILLISECONDS);

				if (count < 3) {
					sendPingRequest();
				} else {
					System.out.println("Avg ping: " + totalMs / 3);
					plugin.setPing(totalMs / 3);
				}

			} catch (IOException ex) {}
		}).start();
	}

	private static void isReachable(String addr, int openPort, int timeOutMillis) {

	}
}
