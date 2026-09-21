# QOL and Camera Info

QOL and Camera Info is a RuneLite external plugin that displays the local
player's world X, Y, and plane coordinates, current camera zoom, and camera
rotation in degrees.

Each value has its own setting. Disable **Names** to display values without
labels, or enable **Add all** to show every value without changing **Names**.

The plugin also has two optional status squares:

- **Click prediction**: yellow for a walk/cancel default action and red for a
  non-walk default action.
- **Last click status**: retains the same color classification for the most
  recent selected action.

The plugin only observes existing client state. It does not add, remove, or
change menu entries, and it does not inject input.

## Run locally

Run `gradlew.bat run` from the project directory, then enable **QOL and Camera Info**
in the RuneLite plugin list.
