# Korenet Wiki

Welcome to the official Korenet wiki. This mod provides real-time network feedback (ping + jitter) and a hit desync HUD, with on-screen editors for both.

> [!TIP]
> New here? Start with the **Quick Start** below and then open the HUD editor in-game to place the widgets where you want.

## Quick Start
1. Install Fabric Loader and Fabric API.
2. Drop the Korenet JAR into your `mods` folder.
3. Launch Minecraft and join a world/server.
4. Press the keybinds to position/configure the HUDs.

## Features
- **Ping + Jitter HUD** with adjustable position, scale, opacity, and background.
- **Hit Desync HUD** showing synced/minor/major desync states.
- In‑game HUD editors for both overlays.

> [!TIP]
> If your HUD doesn’t show, verify that the HUD is enabled and that F1 (hide GUI) isn’t active.

## Keybinds
Default keybinds (configurable in Controls):
- **Open Korenet HUD Editor**: `H`
- **Open Desync HUD Editor**: `J`

> [!NOTE]
> If you already use `H` or `J` for other mods, rebind the keys in Minecraft’s Controls menu.

## HUD Editors
Both HUDs are moved via drag‑and‑drop and support snapping to screen corners.
- **Korenet HUD Editor** adjusts ping/jitter HUD position, scale, opacity, background, and update interval.
- **Desync HUD Editor** adjusts desync HUD position, scale, opacity, auto‑hide, and background.

## Configuration Files
Korenet stores config files in your `config` folder:
- `korenet.json` — Korenet core + ping/jitter HUD settings
- `korenet_desync_hud.json` — Desync HUD settings

> [!TIP]
> You can delete the config files to reset all settings to defaults.

## Ping Color Rules
The ping text color adapts to latency:
- **Green**: below 75 ms
- **Yellow**: 75–160 ms
- **Red**: above 160 ms

## Compatibility
- **Minecraft**: 1.21.x (as declared in `fabric.mod.json`)
- **Java**: 21+
- **Fabric API**: required

> [!NOTE]
> This mod is client‑side only and does not need to be installed on servers.

## Troubleshooting
- **HUD not visible**: Ensure the HUD is enabled and GUI is not hidden (F1).
- **No ping data**: Ping/jitter may show `N/A` in singleplayer or when disconnected.
- **Weird positions after resize**: Re-open the HUD editor to snap or reposition.

> [!TIP]
> If issues persist, share your `logs/latest.log` and config files when reporting bugs.

## Credits
Created by **FabledRuns**.

## License
MIT — see LICENSE for details.
