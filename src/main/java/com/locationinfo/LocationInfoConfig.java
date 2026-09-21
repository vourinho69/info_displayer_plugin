package com.locationinfo;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(LocationInfoConfig.GROUP)
public interface LocationInfoConfig extends Config
{
	// Keep the group stable: it is the namespace RuneLite uses for saved settings.
	String GROUP = "location-info";
	// The explicit key is also used by the one-time legacy preference migration.
	String CAMERA_ZOOM_KEY = "cameraZoom";

	// Each default method represents one setting in RuneLite's plugin panel.
	@ConfigItem(keyName = "names", name = "Names", description = "Show labels next to coordinate values")
	default boolean names()
	{
		return true;
	}

	@ConfigItem(keyName = "addAll", name = "Add all", description = "Show every value, regardless of its individual setting")
	default boolean addAll()
	{
		return false;
	}

	@ConfigItem(keyName = "playerX", name = "Player X", description = "Show the player's world X coordinate")
	default boolean playerX()
	{
		return true;
	}

	@ConfigItem(keyName = "playerY", name = "Player Y", description = "Show the player's world Y coordinate")
	default boolean playerY()
	{
		return true;
	}

	@ConfigItem(keyName = "playerZ", name = "Player Z", description = "Show the player's world plane (Z coordinate)")
	default boolean playerZ()
	{
		return true;
	}

	@ConfigItem(keyName = CAMERA_ZOOM_KEY, name = "Camera zoom", description = "Show the current camera zoom value")
	default boolean cameraZoom()
	{
		return true;
	}

	@ConfigItem(keyName = "cameraYawDegrees", name = "Lateral rotation", description = "Show the camera's yaw rotation in degrees")
	default boolean cameraYawDegrees()
	{
		return true;
	}

	@ConfigItem(keyName = "cameraPitchDegrees", name = "Horizontal rotation", description = "Show the camera's pitch rotation in degrees")
	default boolean cameraPitchDegrees()
	{
		return true;
	}

	@ConfigItem(keyName = "clickPrediction", name = "Click prediction", description = "Show whether the current default click is an action")
	default boolean clickPrediction()
	{
		return true;
	}

	@ConfigItem(keyName = "lastClickStatus", name = "Last click status", description = "Show whether the last click was an action")
	default boolean lastClickStatus()
	{
		return true;
	}
}
