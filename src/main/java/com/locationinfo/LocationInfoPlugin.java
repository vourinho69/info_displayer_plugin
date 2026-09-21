package com.locationinfo;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

/**
 * Registers the overlay and records the menu action used by the player's most
 * recent click. This class intentionally does not change any game state: it
 * only observes RuneLite events and supplies data to the overlay.
 */
@PluginDescriptor(
	name = "QOL and Camera Info"
)
public class LocationInfoPlugin extends Plugin
{
	// Previous versions stored the zoom toggle under this key. Keep it only to
	// migrate an existing user's saved preference to the correctly named key.
	private static final String LEGACY_CAMERA_Z_KEY = "cameraZ";

	@Inject
	private ConfigManager configManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private LocationInfoOverlay overlay;

	@Override
	protected void startUp()
	{
		// Perform configuration migration before the overlay first reads it.
		migrateCameraZoomConfig();
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		// The overlay manager owns rendering, so removing it stops all display work.
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		// MenuOptionClicked is emitted after RuneLite processes a user click. The
		// overlay retains this classification for the "last click" indicator.
		overlay.setLastClickWasRed(event.getMenuAction());
	}

	/**
	 * Moves the old zoom preference to its new key once. A separately saved new
	 * value takes precedence, so upgrading cannot overwrite a later choice.
	 */
	private void migrateCameraZoomConfig()
	{
		Boolean legacyValue = configManager.getConfiguration(LocationInfoConfig.GROUP, LEGACY_CAMERA_Z_KEY, Boolean.class);
		if (legacyValue == null)
		{
			return;
		}

		Boolean currentValue = configManager.getConfiguration(LocationInfoConfig.GROUP, LocationInfoConfig.CAMERA_ZOOM_KEY, Boolean.class);
		if (currentValue == null)
		{
			configManager.setConfiguration(LocationInfoConfig.GROUP, LocationInfoConfig.CAMERA_ZOOM_KEY, legacyValue);
		}
		configManager.unsetConfiguration(LocationInfoConfig.GROUP, LEGACY_CAMERA_Z_KEY);
	}

	@Provides
	LocationInfoConfig provideConfig(ConfigManager configManager)
	{
		// Guice uses this method to provide one typed view of this plugin's config.
		return configManager.getConfig(LocationInfoConfig.class);
	}
}
