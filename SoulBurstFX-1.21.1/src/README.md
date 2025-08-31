# SoulburstFX

**Autor:** wolfwriter \
**Version:** SoulburstFX-1.0-1.21.1 \
**License:** GNU General Public License version 3 (GPLv3)

## What is FootstepsFX?
SoulburstFX turns every kill into a visual spectacle:
When a player defeats another player or mob, stunning particle effects appear at the exact death location. These effects are fully customizable, unlockable, and stylishly animated — from flames and explosions to flowers and spirals.

## Features
- Toggle kill effects on/off per player
- Wide selection of particle effects (flame, explosion, heart, angry villager, …)
- Style effects like `circle`, `spiral`, `flower`, `helix` and more
- Unlock & lock system for effects and styles
- Effects appear precisely at the enemy’s death point
- JSON-based storage: settings persist across server restarts
- Simple commands with tab-completion and permission control

## Commands
| Commands                                                       | Description                        |
|----------------------------------------------------------------|------------------------------------|
| `/killeffect on`                                               | Enables kill effects               |
| `/killeffect off`                                              | Disables kill effects              |
| `/killeffect effect <name>`                                    | Sets the particle effect           |
| `/killeeffect list [page]`                                     | Lists all available effects        |
| `/killeffect style <name/on/off>`                              | Configures style effects           |
| `/killeffect styles`                                           | Lists all available styles         |
| `/killeffect unlock <effect/syle> <name/all>`                  | Unlocks effects or styles          |
| `/killeffect unlock player <player> <effect/style> <name/all>` | Unlocks for another player         |
| `/killeffect lock <effect/style> <name/all>`                   | Locks effects or styles            |
| `/killeffect lock player <player> <effect/style> <name/all>`   | Locks for another player           |
| `/killeffect reload`                                           | Reloads kill effect data from JSON |
| `/killeffect help`                                             | Shows help menu                    |

All commands require the `killeffect.use` permission, unless they involve unlocking, locking, or reloading — which require `killeffect.unlock`, `killeffect.lock`, or `killeffect.reload` respectively.

## Storage
Player settings are stored in `plugins/SoulburstFX/soulburstfx_playerdate.json`:

`````json
{
    “uuid-of-the-player”: {
       "enabled": true/false,
        "effectType": "effectname",
        "style": "stylename",
        "styleEnabled": true/false,
        "unlockedEffects": [
        "unlocked effectname"
        ],
        "unlockedStyles": [
        "unlocked stylename"
        ]
    },
    [...]
}