package com.locationinfo;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(LocationInfoConfig.GROUP)
public interface LocationInfoConfig extends Config
{
	// Keep the group stable: it is the namespace RuneLite uses for saved settings.
	String GROUP = "location-info";
	// The explicit key is also used by the one-time legacy preference migration.
	String CAMERA_ZOOM_KEY = "cameraZoom";
	// This differs from the old cameraZ key, which previous versions used for
	// the zoom preference and which is migrated during plugin startup.
	String CAMERA_POSITION_Z_KEY = "cameraPositionZ";
	String PLAYER_COORDINATES_SECTION = "playerCoordinates";
	String CAMERA_INFO_SECTION = "cameraInfo";
	String DEBUGGING_SECTION = "debugging";

	@ConfigSection(
		name = "Player coordinates",
		description = "Player world-coordinate display settings",
		position = 2,
		closedByDefault = true
	)
	String playerCoordinatesSection = PLAYER_COORDINATES_SECTION;

	@ConfigSection(
		name = "Camera info",
		description = "Camera display settings",
		position = 6,
		closedByDefault = true
	)
	String cameraInfoSection = CAMERA_INFO_SECTION;

	@ConfigSection(
		name = "Debugging",
		description = "Game-state diagnostics for learning and plugin development",
		position = 10,
		closedByDefault = true
	)
	String debuggingSection = DEBUGGING_SECTION;

	// Each default method represents one setting in RuneLite's plugin panel.
	@ConfigItem(keyName = "addAll", name = "Add all", position = 0,
		description = "Show every value, regardless of its individual setting")
	default boolean addAll()
	{
		return false;
	}

	@ConfigItem(keyName = "names", name = "Names", position = 1,
		description = "Show labels next to coordinate values")
	default boolean names()
	{
		return true;
	}

	@ConfigItem(keyName = "playerX", name = "Player X", position = 3, section = PLAYER_COORDINATES_SECTION,
		description = "Show the player's world X coordinate")
	default boolean playerX()
	{
		return true;
	}

	@ConfigItem(keyName = "playerY", name = "Player Y", position = 4, section = PLAYER_COORDINATES_SECTION,
		description = "Show the player's world Y coordinate")
	default boolean playerY()
	{
		return true;
	}

	@ConfigItem(keyName = "playerZ", name = "Player Z", position = 5, section = PLAYER_COORDINATES_SECTION,
		description = "Show the player's world plane (Z coordinate)")
	default boolean playerZ()
	{
		return true;
	}

	@ConfigItem(keyName = "cameraX", name = "Camera X", position = 11, section = DEBUGGING_SECTION,
		description = "Show the camera X position")
	default boolean cameraX()
	{
		return true;
	}

	@ConfigItem(keyName = "cameraY", name = "Camera Y", position = 12, section = DEBUGGING_SECTION,
		description = "Show the camera Y position")
	default boolean cameraY()
	{
		return true;
	}

	@ConfigItem(keyName = CAMERA_POSITION_Z_KEY, name = "Camera Z", position = 13, section = DEBUGGING_SECTION,
		description = "Show the camera Z position")
	default boolean cameraZ()
	{
		return true;
	}

	@ConfigItem(keyName = CAMERA_ZOOM_KEY, name = "Camera zoom", position = 7, section = CAMERA_INFO_SECTION,
		description = "Show the current camera zoom value")
	default boolean cameraZoom()
	{
		return true;
	}

	@ConfigItem(keyName = "cameraYaw", name = "Camera yaw", position = 14, section = DEBUGGING_SECTION,
		description = "Show the raw camera yaw value")
	default boolean cameraYaw()
	{
		return true;
	}

	@ConfigItem(keyName = "cameraYawDegrees", name = "Horizontal rotation", position = 8, section = CAMERA_INFO_SECTION,
		description = "Show the camera's yaw rotation in degrees")
	default boolean cameraYawDegrees()
	{
		return true;
	}

	@ConfigItem(keyName = "cameraPitch", name = "Camera pitch", position = 15, section = DEBUGGING_SECTION,
		description = "Show the raw camera pitch value")
	default boolean cameraPitch()
	{
		return true;
	}

	@ConfigItem(keyName = "cameraPitchDegrees", name = "Vertical rotation", position = 9, section = CAMERA_INFO_SECTION,
		description = "Show the camera's pitch rotation in degrees")
	default boolean cameraPitchDegrees()
	{
		return true;
	}

	@ConfigItem(keyName = "sceneBaseX", name = "Scene base X", position = 16, section = DEBUGGING_SECTION,
		description = "Show the scene origin world X tile; -1 in unsupported scenes")
	default boolean sceneBaseX()
	{
		return true;
	}

	@ConfigItem(keyName = "sceneBaseY", name = "Scene base Y", position = 17, section = DEBUGGING_SECTION,
		description = "Show the scene origin world Y tile; -1 in unsupported scenes")
	default boolean sceneBaseY()
	{
		return true;
	}

	@ConfigItem(keyName = "clickPrediction", name = "Click prediction", position = 18, section = DEBUGGING_SECTION,
		description = "Show whether the current default click is an action")
	default boolean clickPrediction()
	{
		return true;
	}

	@ConfigItem(keyName = "hoveredEntityId", name = "Hovered object/NPC ID", position = 19, section = DEBUGGING_SECTION,
		description = "Show the ID of the object or NPC under the mouse")
	default boolean hoveredEntityId()
	{
		return true;
	}

	@ConfigItem(keyName = "lastClickStatus", name = "Last click status", position = 20, section = DEBUGGING_SECTION,
		description = "Show whether the last click was an action")
	default boolean lastClickStatus()
	{
		return true;
	}

	@ConfigItem(keyName = "currentModalInterface", name = "Modal UI", position = 21,
		section = DEBUGGING_SECTION,
		description = "Show the currently open modal-interface parent group ID; 0 means none")
	default boolean currentModalInterface()
	{
		return true;
	}
}
