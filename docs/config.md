# 🔧 Configuration Guide

Overeating Plus is highly configurable with 50+ options organized into categories.

---

## Config File Location

```
config/overatingplus-server.toml
```

The config is **server-side** and syncs to clients automatically.

---

## Categories

### 1. Oversaturation (`[oversaturation]`)

Core settings for the oversaturation system.

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `accumulationSource` | Enum | `NUTRITION` | What food value to use: `NUTRITION` or `SATURATION` |
| `drainSpeedMultiplier` | Double | `1.0` | Multiplier for oversaturation drain (1.0 = same as hunger) |
| `maxOversaturationStacks` | Int | `40` | Maximum oversaturation stacks (1-100) |
| `saturationModeThreshold` | Double | `5.0` | Minimum saturation to gain oversaturation (0.0-20.0) |
| `enableFoodVariety` | Boolean | `true` | Enable food variety system |
| `foodVarietyHistorySize` | Int | `18` | Number of recent foods to track (1-64) |
| `foodVarietyBlacklist` | List | `["minecraft:ominous_bottle"]` | Foods excluded from variety tracking |
| `foodVarietyWhitelist` | List | `[]` | Foods included in variety (empty = all) |

### 2. Combat (`[combat]`)

Combat bonuses from oversaturation stacks.

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `maxFatArmorHp` | Int | `40` | Maximum fat armor HP (1-100) |
| `fatArmorRegenInterval` | Int | `20` | Ticks between fat armor regen (1-200) |
| `slowdownStartLevel` | Int | `1` | Stack where slowdown begins |
| `slowdownBasePercent` | Double | `0.01` | Base slowdown at start stack |
| `slowdownPerLevelPercent` | Double | `0.01` | Additional slowdown per stack |
| `strengthStartLevel` | Int | `1` | Stack where attack bonus begins |
| `strengthBasePercent` | Double | `0.02` | Base attack bonus at start stack |
| `strengthPerLevelPercent` | Double | `0.02` | Additional attack bonus per stack |
| `knockbackResistanceStartLevel` | Int | `1` | Stack where KB resistance begins |
| `knockbackResistanceBase` | Double | `0.02` | Base KB resistance at start stack |
| `knockbackResistancePerLevel` | Double | `0.02` | Additional KB resistance per stack |
| `meleeRepulseStartLevel` | Int | `10` | Stack where melee repulse begins |
| `meleeRepulseBase` | Double | `0.5` | Base repulse strength |
| `meleeRepulsePerLevel` | Double | `0.03` | Additional repulse per stack |
| `attackDistanceStartLevel` | Int | `1` | Stack where attack distance begins |
| `attackDistanceMaxBonus` | Double | `1.0` | Max bonus attack distance (blocks) |
| `blockReachStartLevel` | Int | `1` | Stack where block reach begins |
| `blockReachMaxBonus` | Double | `1.0` | Max bonus block reach (blocks) |

### 3. Abilities (`[abilities]`)

Settings for Slime Bounce and Shockwave.

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `slimeBounceStartLevel` | Int | `10` | Stack to unlock Slime Bounce |
| `slimeBounceFactor` | Double | `0.6` | Base bounce strength |
| `slimeBounceChainDamping` | Double | `0.5` | Bounce weakening per chain |
| `slimeBounceMinFallSpeed` | Double | `0.8` | Minimum fall speed to bounce |
| `slimeBounceStopSpeed` | Double | `0.1` | Stop bouncing below this speed |
| `shockwaveStartLevel` | Int | `25` | Stack to unlock Shockwave |
| `shockwaveMinFallBlocks` | Double | `5.0` | Minimum fall height for shockwave |
| `shockwaveRadius` | Double | `3.5` | Shockwave radius (blocks) |
| `shockwaveKnockback` | Double | `1.2` | Shockwave knockback strength |

### 4. Ability Costs (`[ability_costs]`)

Oversaturation point costs for abilities.

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `slimeBounceCost` | Double | `5.0` | Points per Slime Bounce |
| `shockwaveCost` | Double | `5.0` | Points per Shockwave |

### 5. Character Size (`[character_size]`)

Player hitbox growth settings.

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `sizeStartLevel` | Int | `1` | Stack where growth begins |
| `sizeMaxLevel` | Int | `40` | Stack for maximum growth |
| `sizeTargetWidth` | Double | `1.2` | Max hitbox width (blocks) |
| `sizeTargetHeight` | Double | `1.8` | Max hitbox height (blocks) |
| `sizeTorsoMaxScale` | Double | `2.2` | Max visual torso scale |
| `sizeAxisX` | Double | `1.0` | Width growth multiplier |
| `sizeAxisY` | Double | `0.2` | Height growth multiplier |
| `sizeAxisZ` | Double | `1.0` | Depth growth multiplier |
| `sizeCameraPullback` | Double | `0.0` | Camera pullback at max size |

### 6. Scales (`[scales]`)

Scales block redstone output.

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `scalesRedstoneMinStack` | Int | `0` | Minimum stack for redstone output |
| `scalesRedstoneMaxPower` | Int | `15` | Maximum redstone power |

---

## Example Config

```toml
[oversaturation]
accumulationSource = "NUTRITION"
drainSpeedMultiplier = 1.0
maxOversaturationStacks = 40
saturationModeThreshold = 5.0
enableFoodVariety = true
foodVarietyHistorySize = 18

[combat]
maxFatArmorHp = 40
fatArmorRegenInterval = 20
slowdownStartLevel = 1
slowdownBasePercent = 0.01
strengthStartLevel = 1
strengthBasePercent = 0.02

[abilities]
slimeBounceStartLevel = 10
slimeBounceFactor = 0.6
shockwaveStartLevel = 25
shockwaveRadius = 3.5

[ability_costs]
slimeBounceCost = 5.0
shockwaveCost = 5.0

[character_size]
sizeStartLevel = 1
sizeMaxLevel = 40
sizeTargetWidth = 1.2
sizeTorsoMaxScale = 2.2

[scales]
scalesRedstoneMinStack = 0
scalesRedstoneMaxPower = 15
```

---

## Tips

- **Balancing:** If the mod feels too easy, increase `drainSpeedMultiplier` or decrease `maxOversaturationStacks`
- **Hardcore:** Disable `enableFoodVariety` for a more relaxed experience
- **Abilities:** Adjust `slimeBounceCost` and `shockwaveCost` to control ability spam
