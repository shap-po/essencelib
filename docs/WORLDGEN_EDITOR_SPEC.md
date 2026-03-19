# Dungeon & Stone: Worldgen Editor Spec

This document defines the planned world generation editor for `EssenceLib` on `Minecraft 1.21.1 + Fabric`.
It aligns with the Shift loop in `MASTER_DESIGN.md` and keeps implementation additive and deterministic.

---

## 1) Purpose

Build an in-game and data-driven editor that lets server owners design "Shift" dungeon worlds without rewriting code.

The editor should configure:

- Player-scaled world size rules.
- Shift biome/theme selection and weighting.
- Structure pool selection (rooms, landmarks, boss arena, extraction zone).
- Difficulty profile for the current Shift.
- Spawn/distribution rules for resources and key objectives (including future mana stone veins).
- Validation and export/import of presets.

The editor is an authoring tool. Runtime generation stays server-authoritative.

---

## 2) Non-Negotiable Rules

1. Deterministic generation from a known seed and profile.
2. Server-only authority for worldgen and profile resolution.
3. Backward-safe schema evolution (older profiles still load).
4. No client-only classes in common generation code.
5. Safe registry access (`getOrEmpty` style lookups) for IDs from JSON.
6. Graceful fallback if a configured biome/feature/structure is missing.

---

## 3) Shift World Contract

This editor targets the Shift contract already defined:

- Runtime window: `50 minutes`.
- Deployment model: base overworld and dungeon runtime can run as separate worlds/servers.
- World footprint scales by active player count in the dungeon queue/party:
  - Base footprint starts at `1000 x 1000`.
  - Profile defines per-player expansion and hard min/max clamps.
  - Final size is computed server-side before generation starts.
- Contains:
  - Edge spawn regions for players (not center spawn).
  - Multiple traversal/combat zones moving inward/outward.
  - Exactly one Floor Guardian arena.
  - `3-4` portal exits (randomized locations).
  - Extraction portal beacon reveal at `10 minutes` remaining.
  - Collapse/disintegration starts at `10 minutes` remaining.
  - Players who fail to exit before collapse end are killed (not downed).

Entry flow:

- Players gather at a base-world structure within a configured radius.
- Eligible players are moved into the active dungeon instance when Shift begins.
- Spawn points are placed on edge bands (not center).
- While one dungeon is live, the system can warm up the next dungeon instance in parallel.

World style modes:

- Multi-biome dungeon profiles (desert + other biome mixes).
- Single-biome profiles for "overworld/dimension-based" runs where one biome has full spawn setup.

Generation should produce valid play space every time, even with sparse profile data.

---

## 4) Scope Boundaries

### In Scope (v1-v2)

- Profile JSON schema and loader.
- Profile validation + defaults.
- Server command API for profile management.
- Optional lightweight client editor UI for profile editing.
- Preset import/export.

### Out of Scope (for now)

- Full external visual node graph editor.
- Runtime hot-patching of active Shift chunks.
- Per-player personalized world layouts inside one Shift instance.

---

## 5) Data Model (Draft)

Store profile files under:

- `data/essencelib/worldgen_profiles/<profile_id>.json`

Suggested top-level structure:

```json
{
  "schema_version": 1,
  "id": "essencelib:default_shift",
  "display_name": "Default Shift",
  "dimension_mode": "pocket_dimension",
  "server_topology": {
    "separate_dungeon_server": true,
    "base_world_id": "essencelib:wasteland",
    "dungeon_world_id": "essencelib:active_shift"
  },
  "entry_gateway": {
    "structure_id": "essencelib:gate_structure_a",
    "join_radius": 24,
    "entry_lock_seconds_before_start": 20
  },
  "world_bounds": {
    "size_x": 1000,
    "size_z": 1000,
    "height_min": -64,
    "height_max": 320
  },
  "size_scaling": {
    "base_size_x": 1000,
    "base_size_z": 1000,
    "extra_blocks_per_player": 120,
    "min_size_x": 1000,
    "min_size_z": 1000,
    "max_size_x": 2400,
    "max_size_z": 2400
  },
  "biome_mode": "multi",
  "single_biome_id": null,
  "theme_pool": [
    { "id": "essencelib:flooded", "weight": 20 },
    { "id": "essencelib:desert_ruins", "weight": 20 },
    { "id": "essencelib:underground", "weight": 20 },
    { "id": "essencelib:sky_islands", "weight": 20 },
    { "id": "essencelib:lava_oceans", "weight": 10 }
  ],
  "structure_sets": {
    "starter": ["essencelib:spawn_hub_a"],
    "combat_rooms": ["essencelib:room_cavern_a", "essencelib:room_ruins_b"],
    "guardian_arenas": ["essencelib:arena_guardian_a"],
    "extraction": ["essencelib:extraction_site_a"]
  },
  "objectives": {
    "guardian_required": true,
    "extraction_required": true,
    "exit_portal_count_min": 3,
    "exit_portal_count_max": 4
  },
  "spawn_rules": {
    "spawn_at_edges": true,
    "edge_band_thickness": 80,
    "flow_preference": "inward_or_outward"
  },
  "boss_rules": {
    "height_anchor": "highest_or_lowest",
    "prefer_center_radius": 180
  },
  "portal_rules": {
    "type": "portal",
    "beam_reveal_time_remaining_minutes": 10,
    "beam_visible_global": true
  },
  "collapse_rules": {
    "start_time_remaining_minutes": 10,
    "failed_extract_outcome": "hard_death"
  },
  "resource_distribution": {
    "mana_stone_vein_density": 1.0,
    "mana_stone_density": 1.0,
    "seed_loot_bias": 1.0,
    "chest_density": 1.0,
    "allow_modded_mini_dungeons": true
  },
  "terrain_rules": {
    "underground_depth_scale": 0.65,
    "enable_caves": true,
    "enable_cave_biomes": true,
    "enable_dark_world_variants": true,
    "enable_normal_day_night_variants": true
  },
  "base_overworld_rules": {
    "is_wasteland": true,
    "allow_food_spawns": false,
    "allow_natural_mob_spawns": false,
    "crop_lifetime": {
      "enabled": true,
      "harvests_min": 1,
      "harvests_max": 4,
      "break_forever_after_lifetime": true
    }
  },
  "loot_recovery_rules": {
    "enable_church_repurchase": true,
    "church_location_id": "essencelib:church_main",
    "eligible_when": [
      "dungeon_hard_death",
      "dungeon_corpse_destroyed"
    ]
  },
  "difficulty": {
    "mob_level_min": 1,
    "mob_level_max": 4,
    "elite_chance": 0.08
  }
}
```

### Schema Guidance

- `schema_version` required for migration.
- `id` must be namespaced and stable.
- `dimension_mode`: `pocket_dimension` or `overworld`.
- `biome_mode`: `multi` or `single`; `single` requires `single_biome_id`.
- `server_topology.separate_dungeon_server` controls cross-world/server handoff mode.
- `entry_gateway.join_radius` defines who is queued from the base-world gathering structure.
- All weighted pools use integer weights (`>= 0`).
- Missing optional fields are defaulted during load.
- Unknown fields are ignored (forward compatibility).

---

## 6) Runtime Architecture

### Server Side (`src/main`)

- `WorldgenProfileRegistry`
  - Loads profile JSON from datapack/resource sources.
  - Validates fields and resolves defaults.
  - Exposes immutable profile snapshots by ID.

- `ShiftGenerationPlanner`
  - Converts profile + seed into a deterministic generation plan.
  - Computes player-scaled bounds before placement.
  - Chooses theme/biomes, structure sequence, objective placement, extraction portal positions.
  - Places boss arena near center while satisfying highest/lowest-height rule.

- `ShiftWorldBuilder`
  - Applies plan to world/chunk generation hooks.
  - Handles fallback placement if a chosen structure fails.

- `ShiftGenerationValidator`
  - Ensures contract validity (edge spawns present, guardian present, `3-4` exits reachable).
  - Ensures collapse timing and hard-death extraction rules are internally consistent.
  - Rejects invalid plans before world activation.

- `DungeonTransferCoordinator`
  - Handles movement of queued players from base-world structure radius into dungeon instance.
  - Supports same-server dimension transfer or separate-server handoff.

- `DungeonLossLedger`
  - Records loot state when dungeon hard death occurs or dungeon corpse is destroyed.
  - Publishes eligible records for church repurchase in base world.
  - Flushes ledger snapshots on interval and at server shutdown.

- `DungeonRotationOrchestrator`
  - Starts pre-generation for the next dungeon while the current dungeon is active.
  - Default cadence: at ~`30 minutes` into the active run, begin next-world generation/pregen so it is ready near closure.
  - Handles server/world handoff readiness signaling.

### Client Side (`src/client`)

- `WorldgenEditorScreen` (optional in v2)
  - UI for editing profile fields and weights.
  - Saves drafts to JSON templates (client-side staging).
  - Submits to server commands for authoritative import/apply.

Client UI never directly mutates runtime generation state.

---

## 7) Command API (First Delivery)

Add server commands to unblock testing before GUI:

- `/ess worldgen profile list`
- `/ess worldgen profile show <id>`
- `/ess worldgen profile validate <id>`
- `/ess worldgen profile apply <id>`
- `/ess worldgen profile export <id>`
- `/ess worldgen profile import <id> <json>`
- `/ess worldgen profile clone <source_id> <new_id>`

Permissions:

- Admin only by default.
- Read-only commands can be allowed to moderators.

---

## 8) Validation Rules

At apply-time, reject profile if any of the following fail:

1. Missing or invalid `id`.
2. Empty or all-zero `theme_pool`.
3. Missing required structure sets for guardian/extraction.
4. Bounds outside permitted range for Shift contract.
5. Invalid portal count range (must be `3..4` inclusive for current design).
6. Boss placement rule cannot satisfy center + height constraints.
7. Missing/invalid entry gateway structure or join radius.
8. Collapse start timing invalid or not aligned with extraction reveal timing.
9. Difficulty ranges invalid (`min > max`, negative levels, invalid chance values).
10. Required registry references unresolved after safe lookup.

Warn but allow:

- Unknown optional keys.
- Missing optional themed assets (if fallback exists).

---

## 9) Determinism Contract

Given `(profile_id, shift_seed, schema_version)`, planner output must be stable.

Use deterministic random routing:

- Derive sub-seeds per generation stage (`theme`, `structures`, `objectives`, `loot_bias`).
- Avoid any non-seeded random sources in generation logic.
- Version-bump schema when deterministic behavior intentionally changes.

---

## 10) Integration With Dungeon Systems

The editor must cooperate with existing/future dungeon mechanics:

- Floor Guardian location output is published for encounter systems.
- Extraction portal outputs (`3-4`) are published for beacon/collapse logic.
- Collapse engine enforces hard death (no downed fallback) for non-extracted players.
- Difficulty profile can feed future mob level/tier assignment (planned in master design).
- Resource distribution should keep economy constraints in mind (no runaway farm loops).
- Dungeon resource logic should support future mana stone vein implementation.
- Modded mini-dungeon structures are allowed via curated structure pool IDs.
- Base overworld remains a low-resource wasteland (no food spawns, no natural hostile/passive spawn loops).
- Loot remaining on a player after dungeon hard death, or lost because dungeon corpse was destroyed, becomes repurchasable via church service in base overworld.
- Church inventory updates per player identity from the persisted death/corpse ledger.

This keeps worldgen and progression connected without hard-coding class/essence logic into generation itself.

---

## 11) Persistence & Reload Behavior

- Active Shift uses a frozen profile snapshot at start.
- Mid-Shift profile edits affect next Shift only.
- On server reload/restart, profiles reload from source data.
- If active profile is missing after reload, use configured fallback profile and log warning.
- Crop lifetime counters for base overworld persist across relog/restart.
- Church repurchase ledger persists across relog/restart and server restarts.
- On dungeon server shutdown, unresolved corpse/death loot records must be persisted before process exit.
- On base server startup/reload, church service rebuilds its offerings from persisted ledger state.

---

## 12) Failure Handling

If generation cannot satisfy contract:

1. Retry planner with deterministic fallback variant path.
2. If still invalid, switch to hardcoded emergency profile.
3. Emit detailed server log with profile ID, seed, and failed checks.

Never start a Shift with missing guardian or extraction.
Never start a Shift with fewer than `3` or more than `4` exit portals.
Never allow collapse-failed players to convert into downed state; they resolve as hard death.

---

## 13) Security & Permissions

- Only trusted operators can import/overwrite profiles.
- Validate JSON size and depth to prevent command abuse.
- Sanitize profile IDs and block path traversal-like input.
- Validate church transaction ownership rules for player-linked corpse loot.

---

## 14) Implementation Phases

### Phase 1 - Data + Commands

- Add schema model + registry + validator.
- Add command API.
- Add planner stub that creates deterministic plan metadata.
- Add tests for load/validate/default behavior.

### Phase 2 - Generation Binding

- Connect planner output to biome/theme/portal placement hooks.
- Implement fallback behavior.
- Validate Shift contract pre-start.
- Add boss placement resolver (highest/lowest near center preference).
- Add edge-spawn resolver and inward/outward traversal validation.
- Add transfer coordinator for base structure radius queue -> dungeon instance entry.
- Add collapse runtime and hard-death enforcement path.
- Add dungeon death/corpse-destroyed loot ledger publishing.
- Add rolling next-dungeon pregen trigger at active-run `~30 minute` mark.
- Add readiness checks to route next group only to fully warmed dungeon instance.

### Phase 3 - Editor UI

- Build optional client editor screen.
- Add profile diff preview + validation feedback.
- Add import/export UX.

---

## 15) Test Checklist

For each meaningful worldgen editor change:

- `compileJava`
- `compileClientJava`
- Validate profile load errors and warnings.
- Generate multiple Shifts with same seed/profile and verify identical plan output.
- Test missing registry IDs and confirm fallback behavior.
- Test relog/restart with active and fallback profiles.
- Verify guardian + extraction portals are always present and reachable.
- Verify portal beam reveal occurs at `10 minutes` remaining.
- Verify exit portal count is always `3` or `4`.
- Verify edge spawns are valid and do not place players in lethal blocks.
- Verify base-world radius gather and transfer into dungeon instance.
- Verify collapse phase starts at `10 minutes` remaining.
- Verify non-extracted players die (not downed) at collapse completion.
- Verify hard-death/corpse-destroyed loot appears in church repurchase inventory.
- Verify dungeon shutdown persists unresolved loot ledger without data loss.
- Verify church inventory is rebuilt correctly after restart from persisted records.
- Verify church purchase path returns eligible loot to owner identity.
- Verify church low-price sale path converts unclaimed corpse loot into church stock/economy sink.
- Verify base overworld no-food/no-mob-spawn rules.
- Verify overworld crop finite-lifetime behavior across relog/restart.

---

## 16) Open Decisions

1. Exact player-size scaling formula defaults (`extra_blocks_per_player`, clamps, party vs active entrants).
2. Beam visibility implementation method (custom renderer packet, forced beacon proxy, or sky marker entity).
3. Rules for selecting highest vs lowest boss anchor (theme-based, random weighted, or profile fixed).
4. Curated compatibility list for external mod mini-dungeon structure IDs.
5. Final behavior for player flow mode (`inward`, `outward`, or mixed) by profile.
6. Church repurchase pricing model (flat tax, % of item value, or tiered by rarity).
7. Cross-server state transport mechanism (plugin message, DB, or file-backed queue).
8. Church commerce defaults: owner buyback window, church low-price resale multiplier, and expiry/cleanup policy.
9. Exact overlap scheduler policy when current run duration changes (fixed time mark vs percentage).

---

## 17) Church Loot Service Lifecycle (Draft)

### Record Sources

- Player hard death during dungeon collapse flow.
- Player hard death from other dungeon fatal paths (if configured).
- Downed corpse destroyed in dungeon before successful recovery.

### Persistence Contract

- Every record stores: player UUID, shift ID, world/server ID, timestamp, serialized loot payload, status.
- Write-ahead style persistence is required for shutdown safety.
- On controlled shutdown, force final ledger flush before closing world/server.

### Church Inventory Update

- Church service loads persisted records on startup/reload.
- Offerings are grouped by owner (`player UUID`) and by status:
  - `buyback_available` (owner can repurchase),
  - `church_stock` (unclaimed or sold to church at low price).
- Owner purchase restores item payload and marks record consumed.

### Pricing (Current Direction)

- Owner buyback uses discounted recovery pricing (not free).
- Church stock resale is intentionally low-value to avoid economy abuse.
- Exact multipliers remain configurable and are tracked in Open Decisions.

---

## 18) Rolling Dungeon Server Lifecycle (Draft)

Goal: reduce dead time between dungeon runs by preparing the next run while one is still active.

1. `T+0`: Active dungeon starts.
2. `~T+30m`: Orchestrator launches next dungeon world/server generation and pregen.
3. `T+40m`: Portal beam reveal + collapse pressure starts for active run.
4. `T+50m`: Active run closes; survivors extract; failures resolve to hard death.
5. `T+50m+`: Next dungeon instance is already warm and accepts the next queued group.

Safety constraints:

- Never route players into next instance until readiness checks pass.
- Keep active and next instances isolated in state/seed/profile snapshot.
- If warmup fails, fall back to emergency generation path and delay entry with clear server messaging.

Document decisions here before Phase 2 implementation.

