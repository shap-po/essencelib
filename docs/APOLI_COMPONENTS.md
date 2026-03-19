# Apoli-Oriented Java Components

This project now exposes reusable Java-side components that powers can reuse by data config.

## 1) Entity Detection Component Profiles

File: `data/essencelib/detection_components.json`

Each profile defines:
- `required_power`: power ID player must have
- `target_type`: `item` | `mob` | `player` | `block` | `any`
- `range_attribute`: optional attribute ID for scaling
- `base_range`, `range_per_attribute`, `min_range`, `max_range`

Current default profiles:
- `essencelib:collector_intuition_item_outline`
- `essencelib:aggro_mob_detector`
- `essencelib:researcher_player_detector`
- `essencelib:block_detector`

`ability_hooks.json` chooses which profile current outline logic uses:
- `collector_intuition_detection_profile`

Runtime helpers:
- `EntityDetectionComponentRegistry.canDetect(player, entity, profileId)`
- `EntityDetectionComponentRegistry.canDetectBlock(player, blockPos, profileId)`

## 2) Aggro Analysis Component

Class: `AggroAnalysisHelper`

Reusable methods:
- `isMobAggroingPlayer(mob, player)`
- `analyze(player, mob, moveX, moveZ, minMoveSq, facingTargetDotMin)`
- `hasRetreatIntent(movingAway, sprinting, facingTarget)`

Used by `PlayerEntityMixin` threat-rhythm logic, but can be reused by future powers.

## 3) Power Gate Component

Class: `EquippedEssencePowerHelper`

Reusable method:
- `hasVisibleEquippedPower(player, powerId)`

Use this for any future Java helper that should be gated by an Apoli power on equipped essence items.

