package com.locationinfo;

import com.google.inject.Provides;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.WidgetNode;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.WidgetModalMode;
import net.runelite.client.callback.ClientThread;
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
	name = "QOL and Camera Info",
	description = "Game-state inspection aid for gameplay understanding and plugin debugging"
)
public class LocationInfoPlugin extends Plugin
{
	// Previous versions stored the zoom toggle under this key. Keep it only to
	// migrate an existing user's saved preference to the correctly named key.
	private static final String LEGACY_CAMERA_Z_KEY = "cameraZ";
	private final Set<Integer> openModalInterfaceIds = new LinkedHashSet<>();

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	private volatile boolean running;

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
		running = true;
		clientThread.invoke(() ->
		{
			if (running)
			{
				refreshOpenModalInterfaces();
			}
		});
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		// The overlay manager owns rendering, so removing it stops all display work.
		overlayManager.remove(overlay);
		running = false;
		clientThread.invoke(() ->
		{
			openModalInterfaceIds.clear();
			overlay.setCurrentModalInterfaceId(0);
		});
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		// MenuOptionClicked is emitted after RuneLite processes a user click. The
		// overlay retains this classification for the "last click" indicator.
		overlay.setLastClickWasRed(event.getMenuAction());
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		for (WidgetNode node : client.getComponentTable())
		{
			if (node.getId() == event.getGroupId() && isModal(node.getModalMode()))
			{
				openModalInterfaceIds.remove(event.getGroupId());
				openModalInterfaceIds.add(event.getGroupId());
				updateCurrentModalInterface();
				return;
			}
		}
	}

	@Subscribe
	public void onWidgetClosed(WidgetClosed event)
	{
		if (event.isUnload() && isModal(event.getModalMode()))
		{
			openModalInterfaceIds.remove(event.getGroupId());
			updateCurrentModalInterface();
		}
	}

	private void refreshOpenModalInterfaces()
	{
		openModalInterfaceIds.clear();
		for (WidgetNode node : client.getComponentTable())
		{
			if (isModal(node.getModalMode()))
			{
				openModalInterfaceIds.add(node.getId());
			}
		}
		updateCurrentModalInterface();
	}

	private void updateCurrentModalInterface()
	{
		int currentId = 0;
		for (int groupId : openModalInterfaceIds)
		{
			currentId = groupId;
		}
		overlay.setCurrentModalInterfaceId(currentId);
	}

	private static boolean isModal(int modalMode)
	{
		return modalMode != WidgetModalMode.NON_MODAL;
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
