# 📘 Overeating Plus Wiki

Welcome to the official wiki for **Overeating Plus**!

---

## 📑 Table of Contents

1. [Getting Started](#getting-started)
2. [Oversaturation System](#oversaturation-system)
3. [Stacks & Bonuses](#stacks--bonuses)
4. [Abilities](#abilities)
5. [Fat Armor](#fat-armor)
6. [Character Growth](#character-growth)
7. [Scales Block](#scales-block)
8. [Food Variety System](#food-variety-system)
9. [HUD Elements](#hud-elements)
10. [Configuration](#configuration)
11. [Compatibility](#compatibility)

---

## Getting Started

### First Steps

1. Install the mod with NeoForge for Minecraft 1.21.1
2. Start playing and fill your hunger bar completely
3. Keep eating! When hunger is full, you'll start gaining oversaturation points
4. Watch your character grow and unlock new abilities

### Quick Tips

- 🍔 Eat a variety of foods to maximize gains
- ⚖️ Place Scales to measure your weight
- 🦘 Press `G` to toggle Slime Bounce
- 💥 Press `H` to toggle Shockwave

---

## Oversaturation System

### How It Works

Oversaturation is a **third food track** that activates when:
- Your hunger bar is full (20/20)
- Your saturation is above the threshold (default: 5.0)

When these conditions are met, eating food grants **oversaturation points** instead of being wasted.

### Points & Stacks

| Concept | Value |
|---------|-------|
| Points per stack | 20 |
| Maximum stacks | 40 (configurable) |
| Maximum points | 800 |

### Drain

Oversaturation drains over time based on your exhaustion level, similar to hunger. The drain rate is configurable (default: 1.0x hunger drain speed).

---

## Stacks & Bonuses

As you gain oversaturation stacks, you receive various bonuses and penalties:

### Combat Bonuses

| Bonus | Start Stack | Base Value | Per Stack |
|-------|-------------|------------|-----------|
| Attack Damage | 1 | +2% | +2% |
| Knockback Resistance | 1 | +2% | +2% |
| Melee Repulse | 10 | 0.5 strength | +0.03 |
| Attack Distance | 1 | +0 | Up to +1.0 blocks |
| Block Reach | 1 | +0 | Up to +1.0 blocks |

### Movement Penalties

| Penalty | Start Stack | Base Value | Per Stack |
|---------|-------------|------------|-----------|
| Slowdown | 1 | -1% | -1% |

### Fat Armor

| Stat | Value |
|------|-------|
| Max HP | Equal to stack level (capped at 40) |
| Regeneration | 1 HP per 20 ticks (configurable) |
| Cost | 1 oversaturation point per HP |

---

## Abilities

### 🦘 Slime Bounce

**Unlock:** 10+ oversaturation stacks  
**Toggle Key:** `G`  
**Cost:** 5 points per bounce

Bounce off blocks and walls like a slime! Each consecutive bounce is weaker than the last.

| Parameter | Default Value |
|-----------|---------------|
| Base bounce factor | 0.6 |
| Chain damping | 0.5 (50% weaker per bounce) |
| Minimum fall speed | 0.8 blocks/tick |
| Stop speed | 0.1 blocks/tick |

### 💥 Shockwave

**Unlock:** 25+ oversaturation stacks  
**Toggle Key:** `H`  
**Cost:** 5 points per trigger

Create a devastating shockwave when landing from heights!

| Parameter | Default Value |
|-----------|---------------|
| Minimum fall height | 5 blocks |
| Radius | 3.5 blocks |
| Knockback | 1.2 strength |

**Damage Calculation:**
- First 3 blocks: 4 damage per block
- Next 5 blocks: 2 damage per block
- Remaining: 1 damage per block

---

## Fat Armor

Fat Armor provides **additional hearts** that absorb damage before your regular health.

### Features

- 🛡️ Absorbs all types of damage
- 💚 Regenerates slowly over time
- 📊 Displayed as orange hearts above your health bar
- ⚖️ Maximum HP scales with oversaturation stacks

### Mechanics

- Fat Armor HP = current oversaturation stack (capped at 40)
- Regenerates 1 HP every 20 ticks (configurable)
- Costs 1 oversaturation point per HP regenerated
- Only regenerates at full health

---

## Character Growth

Your character **physically grows** as oversaturation increases!

### Dimensions

| Parameter | Vanilla | Max Growth |
|-----------|---------|------------|
| Width | 0.6 blocks | 1.2 blocks |
| Height | 1.8 blocks | 1.8 blocks |
| Torso Scale | 1.0x | 2.2x |

### Camera

Third-person camera pulls back as you grow (configurable up to 8 blocks).

---

## Scales Block

### Overview

The **Scales** block measures your oversaturation level and outputs a redstone signal.

### Features

- 📊 8 texture levels showing weight
- 🔊 Sound when stepping on
- ⚡ Redstone output (0-15)
- 🎯 Affected by player's oversaturation

### Redstone Signal

| Stack | Signal |
|-------|--------|
| 0 | 1 (minimum) |
| 1+ | Scales with oversaturation |

### Recipe

```
P P P
S S S
H H H

P = Planks
S = Stone Slab
H = Hopper
```

---

## Food Variety System

### Overview

To prevent spamming one food, the mod tracks your recent food history and blocks repeated foods.

### Settings

| Parameter | Default |
|-----------|---------|
| Enabled | Yes |
| History Size | 18 foods |
| Blacklist | `minecraft:ominous_bottle` |

### How It Works

1. When in oversaturation mode, the mod remembers the last 18 foods you ate
2. Eating the same food again will be blocked
3. A message appears: "You want something else!"
4. Different foods that belong to the same variety group count as the same food

### Variety Groups

Foods can be grouped using tags in `data/overatingplus/tags/item/food_variety_groups/`:

```json
{
  "replace": false,
  "values": [
    "farmersdelight:apple_pie_slice",
    "farmersdelight:apple_pie"
  ]
}
```

---

## HUD Elements

### Burger Counter

Replaces the vanilla hunger bar with burger icons showing oversaturation points.

- 🍔 Full burger = 2 points
- 🍔 Half burger = 1 point
- Background shows empty slots

### Stack Counter

Yellow number displayed to the right of the hunger bar showing current stack level.

### Fat Hearts

Orange hearts displayed above your health bar showing Fat Armor HP.

### Scales Panel

Side panel showing detailed stats:
- Weight (stack level)
- Fat Armor HP
- Character width
- Strength bonus
- Knockback resistance
- Slowdown penalty
- Ability toggles

---

## Configuration

The mod is highly configurable with 50+ options. See [Configuration Guide](config.md) for details.

### Config File Location

```
config/overatingplus-server.toml
```

### Categories

| Category | Description |
|----------|-------------|
| oversaturation | Core points, drain, and food variety |
| combat | Bonuses from stacks, fat armor |
| abilities | Slime bounce and shockwave |
| ability_costs | Point costs for abilities |
| character_size | Hitbox growth and visual scaling |
| scales | Redstone output mapping |

---

## Compatibility

### Supported Mods

| Mod | Compatibility | Notes |
|-----|---------------|-------|
| Farmer's Delight | ✅ Full | Pies and cakes work with oversaturation |
| Vanilla Delight | ✅ Full | Cakes and pies supported |
| Wizardry Delight | ✅ Full | Pies supported |

### Known Issues

- None currently reported

---

## Commands

| Command | Description |
|---------|-------------|
| `/extraweight <player> <amount>` | Add oversaturation points to a player |

---

*Last updated: 2024*
