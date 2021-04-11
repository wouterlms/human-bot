package net.runelite.client.plugins.humanboy;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("HumanBoyConfig")
public interface HumanBoyConfig extends Config
{
	@ConfigItem(
					keyName = "test1",
					name = "Test 1",
					description = "",
					position = 1
	)
	default int test1()
	{
		return 450;
	}

	@ConfigItem(
					keyName = "test2",
					name = "Test 2",
					description = "",
					position = 1
	)
	default int test2()
	{
		return 200;
	}
}
