package com.locationinfo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;

/**
 * Renders the selected player and camera information in the top-left corner.
 * The overlay is redrawn by RuneLite, so it reads the current client state at
 * render time instead of maintaining its own copy of location or camera data.
 */
public class LocationInfoOverlay extends OverlayPanel
{
	// RuneLite represents a full yaw rotation using 16,384 angular units.
	private static final int FULL_TURN_UNITS = 16_384;
	// These client pitch limits map the permitted vertical camera range to 0-90 degrees.
	private static final int MIN_CAMERA_PITCH = 164;
	private static final int MAX_CAMERA_PITCH = 4_160;
	// A monospaced, high-contrast font keeps the displayed values easy to read.
	private static final Font DISPLAY_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 24);
	// Padding and spacing define the tightly sized, individual text backgrounds.
	private static final int LINE_PADDING_X = 3;
	private static final int LINE_PADDING_Y = 2;
	private static final int WORD_GAP = 2;
	private static final int CLICK_STATUS_SIZE = 20;

	private final Client client;
	private final LocationInfoConfig config;
	// Updated by the plugin event handler and retained until the next click occurs.
	private boolean lastClickWasRed;

	@Inject
	private LocationInfoOverlay(Client client, LocationInfoConfig config)
	{
		this.client = client;
		this.config = config;
		setPosition(OverlayPosition.TOP_LEFT);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		// OverlayPanel keeps child components between frames, so rebuild the list
		// from the current settings and client data each time it is rendered.
		panelComponent.getChildren().clear();
		// Individual line components paint their own small white backgrounds.
		panelComponent.setBackgroundColor(null);
		panelComponent.setBorder(new Rectangle());
		panelComponent.setGap(new Point(0, 2));
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			// There is no world location to show before a local player exists.
			return null;
		}

		WorldPoint location = player.getWorldLocation();
		if (config.addAll() || config.playerX())
		{
			addLine("Player X", location.getX());
		}
		if (config.addAll() || config.playerY())
		{
			addLine("Player Y", location.getY());
		}
		if (config.addAll() || config.playerZ())
		{
			addLine("Player Z", location.getPlane());
		}
		if (config.addAll() || config.cameraZoom())
		{
			addLine("Camera zoom", client.getScale());
		}
		if (config.addAll() || config.cameraYawDegrees())
		{
			addLine("Lateral rotation", toYawDegrees(client.getCameraYaw()), " deg");
		}
		if (config.addAll() || config.cameraPitchDegrees())
		{
			addLine("Horizontal rotation", toPitchDegrees(client.getCameraPitch()), " deg");
		}
		if (config.clickPrediction())
		{
			// Red means the current default click is an interaction; yellow means it
			// is a walk, cancel, unknown action, or an open context menu.
			panelComponent.getChildren().add(new ClickStatusComponent(isActionHover()));
		}
		if (config.lastClickStatus())
		{
			panelComponent.getChildren().add(new ClickStatusComponent(lastClickWasRed));
		}

		return super.render(graphics);
	}

	private boolean isActionHover()
	{
		// A context menu has no single default action under the cursor.
		if (client.isMenuOpen())
		{
			return false;
		}

		MenuEntry[] entries = client.getMenu().getMenuEntries();
		if (entries.length == 0)
		{
			return false;
		}

		// RuneLite stores the default left-click entry at the end of this array.
		MenuEntry defaultEntry = entries[entries.length - 1];
		return isRedClick(defaultEntry.getType());
	}

	void setLastClickWasRed(MenuAction action)
	{
		// Save the result separately from hover state, which can change each frame.
		lastClickWasRed = isRedClick(action);
	}

	private static boolean isRedClick(MenuAction action)
	{
		// Walking and cancelling are non-interaction outcomes. Every other menu
		// action is displayed as an interaction using the red status color.
		return action != MenuAction.WALK && action != MenuAction.CANCEL && action != MenuAction.UNKNOWN;
	}

	private void addLine(String label, int value)
	{
		addLine(label, value, "");
	}

	private void addLine(String label, int value, String suffix)
	{
		// Degree units are only appended with labels; number-only mode contains
		// exactly the numeric value for a cleaner compact display.
		String valueText = value + (config.names() ? suffix : "");
		if (config.names())
		{
			panelComponent.getChildren().add(new DisplayLineComponent(label + ": " + valueText));
			return;
		}

		panelComponent.getChildren().add(new DisplayLineComponent(valueText));
	}

	private int toYawDegrees(int cameraYaw)
	{
		// Convert RuneLite's circular angular units into conventional 0-359 degrees.
		return (int) Math.round(cameraYaw * 360.0 / FULL_TURN_UNITS) % 360;
	}

	private int toPitchDegrees(int cameraPitch)
	{
		// Convert the allowed camera pitch range into a clamped 0-90 degree value.
		int degrees = (int) Math.round((cameraPitch - MIN_CAMERA_PITCH) * 90.0
			/ (MAX_CAMERA_PITCH - MIN_CAMERA_PITCH));
		return Math.max(0, Math.min(90, degrees));
	}

	private static class DisplayLineComponent implements LayoutableRenderableEntity
	{
		// One component owns one rendered text line and its exact bounds.
		private final String text;
		private final Rectangle bounds = new Rectangle();
		private Point preferredLocation = new Point();

		private DisplayLineComponent(String text)
		{
			this.text = text;
		}

		@Override
		public Dimension render(Graphics2D graphics)
		{
			// Preserve the graphics state because RuneLite reuses this Graphics2D
			// object when it renders other overlays.
			Font previousFont = graphics.getFont();
			Color previousColor = graphics.getColor();
			graphics.setFont(DISPLAY_FONT);
			FontMetrics metrics = graphics.getFontMetrics();
			int height = metrics.getHeight() + LINE_PADDING_Y * 2;
			// Paint each word independently so its white background only covers the
			// pixels immediately around that word, not the whole panel width.
			String[] words = text.split(" ");
			int width = 0;
			for (String word : words)
			{
				width += metrics.stringWidth(word) + LINE_PADDING_X * 2;
			}
			width += WORD_GAP * (words.length - 1);

			int x = preferredLocation.x;
			for (String word : words)
			{
				int wordWidth = metrics.stringWidth(word) + LINE_PADDING_X * 2;
				graphics.setColor(Color.WHITE);
				graphics.fillRect(x, preferredLocation.y, wordWidth, height);
				graphics.setColor(Color.BLACK);
				graphics.drawString(word, x + LINE_PADDING_X,
					preferredLocation.y + LINE_PADDING_Y + metrics.getAscent());
				x += wordWidth + WORD_GAP;
			}

			graphics.setFont(previousFont);
			graphics.setColor(previousColor);
			bounds.setBounds(preferredLocation.x, preferredLocation.y, width, height);
			return new Dimension(width, height);
		}

		@Override
		public Rectangle getBounds()
		{
			return bounds;
		}

		@Override
		public void setPreferredLocation(Point location)
		{
			preferredLocation = location;
		}

		@Override
		public void setPreferredSize(Dimension size)
		{
			// The background is sized to this line's text, not the panel width.
		}
	}

	private static class ClickStatusComponent implements LayoutableRenderableEntity
	{
		// This component is a fixed-size status square placed after the text lines.
		private final boolean actionHover;
		private final Rectangle bounds = new Rectangle();
		private Point preferredLocation = new Point();

		private ClickStatusComponent(boolean actionHover)
		{
			this.actionHover = actionHover;
		}

		@Override
		public Dimension render(Graphics2D graphics)
		{
			// Red denotes an interaction action; yellow denotes a non-action click.
			graphics.setColor(actionHover ? Color.RED : Color.YELLOW);
			graphics.fillRect(preferredLocation.x, preferredLocation.y, CLICK_STATUS_SIZE, CLICK_STATUS_SIZE);
			bounds.setBounds(preferredLocation.x, preferredLocation.y, CLICK_STATUS_SIZE, CLICK_STATUS_SIZE);
			return new Dimension(CLICK_STATUS_SIZE, CLICK_STATUS_SIZE);
		}

		@Override
		public Rectangle getBounds()
		{
			return bounds;
		}

		@Override
		public void setPreferredLocation(Point location)
		{
			preferredLocation = location;
		}

		@Override
		public void setPreferredSize(Dimension size)
		{
			// The indicator has a fixed square size.
		}
	}
}
