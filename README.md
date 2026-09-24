# QOL and Camera Info

QOL and Camera Info is a prototype game-state inspection aid. Its main utility
is displaying the local player's world coordinates and camera position, zoom,
and rotation so players can better understand what is happening in their game
and make more informed gameplay decisions.

Its additional diagnostics provide visible reference data for learning the
RuneLite API and debugging the behavior of other plugins. It does not inspect or
modify other plugins; it exposes selected client state that developers can use
to compare against their own plugin behavior. The project is also part of the
author's effort to grow into writing complete, production-focused plugins.

**Scene base X/Y** use RuneLite's [WorldView origin](https://static.runelite.net/runelite-api/apidocs/net/runelite/api/WorldView.html).
They display `-1` in instances or unsupported sub-worldviews. The overlay is
hidden outside the logged-in state. The local position reader uses the origin
to keep newly recorded stationary targets anchored across scene changes.

Each value has its own setting. Disable **Names** to display values without
labels, or enable **Add all** to show every value without changing **Names**.
The settings are grouped into **Player coordinates**, **Camera info**, and a
final **Debugging** section for raw camera, scene-origin, and click diagnostics.
The value settings are listed in the same order as their on-screen display.

The plugin also has two optional status squares:

- **Click prediction**: yellow for a walk/cancel default action and red for a
  non-walk default action.
- **Last click status**: retains the same color classification for the most
  recent selected action.

**Hovered object/NPC ID** displays the ID of the world object or NPC under the
mouse directly below **Click prediction**. It displays `-1` when neither type is
hovered or while the right-click menu is open.

The plugin only observes existing client state. It does not add, remove, or
change menu entries, and it does not inject input.

The **Modal UI** debugging value shows the parent group ID of the modal interface
that is open right now, such as a bank or dialogue. It returns to `0` as soon as
no modal interface is open. It does not retain interface history or read chat
messages, senders, widget text, or interface contents.

## Run locally

Run `gradlew.bat run` from the project directory, then enable **QOL and Camera Info**
in the RuneLite plugin list.
