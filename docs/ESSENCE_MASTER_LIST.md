# Dungeon & Stone: Essence Master List

This document serves as the master record for all **implemented** Mob Essences. Use this as a reference when creating new essences to ensure consistency in power structure (Active, Passive, Lifestyle) and stat balancing.

## ⚖️ Global Mechanics & Survival

These rules apply to all players and should be considered when balancing essences.

*   **🚫 No Natural Regeneration**: Health does *not* regenerate naturally from saturation.
*   **🍖 Food & Exhaustion**: Food is critical. It is the primary way to manage exhaustion (stamina), but it does not directly heal HP.
*   **⛺ Resting**: The only way to regenerate Health is by **Camping/Resting** while well-fed.
*   **🧪 Potions**: Potions exist but are **rare** and have significant **drawbacks** (e.g., toxicity, stat penalties). They are emergency tools, not sustain.

---

## 📋 Essence Template (Copy for New Mobs)

### 👾 [Mob Name] ([Title])
*   **Class Affinity**: [Primary Class] / [Secondary Class]
*   **Stats (Base Attributes)**:
    *   🟢 **(+)** [Positive Stat]
    *   🔴 **(-)** [Negative Stat]
*   **Powers**:
    *   ⚔️ **Active: [Power Name]**
        *   *Description:* [Description]
        *   *Cost:* [Mana/Health Cost]
        *   *Cooldown:* [Time]
    *   🛡️ **Passive: [Power Name]**
        *   *Description:* [Description]
    *   🩸 **Lifestyle: [Power Name]**
        *   *Description:* [How this changes your playstyle]
        *   *Bonus:* [Reward for committing to this style]
        *   *Drawback / Cost (Optional):* [Penalty, upkeep, risk, or none if requirement is already hard]

### 🧭 Lifestyle Design Rule
*   Lifestyle powers do **not** need to copy Allay's charge-bar structure.
*   A Lifestyle's job is to push a distinct way to play.
*   Valid formats:
    *   **Bonus + Drawback** (clear upside and clear cost), or
    *   **Hard Requirement + Bonus** (difficult condition, no direct penalty needed).
*   Lifestyle effects can modify movement rules, food rules, combat cadence, positioning habits, or resource flow.

### 🧠 Mob Behavior Alignment Check (Required)
Every time you create a new mob power set, you must also check whether that mob's vanilla behavior should be updated to match the new identity.
*   Ask: "Do this mob's AI goals, targeting, movement habits, loot interaction, and temperament still fit the new powers?"
*   If the answer is no, add or update mob behavior logic (server-side AI/mixins/goals) so the mob fantasy matches the essence fantasy.
*   Keep changes additive and deterministic; avoid hidden one-off logic that duplicates existing systems.
*   Re-test key behavior paths after AI edits: spawn/despawn conditions, player proximity behavior, loot interaction, combat/non-combat stance, and persistence after relog/rejoin.

### 💡 Lifestyle Pattern Examples
*   **Scavenger Diet** (Bonus + Drawback):
    *   You can safely eat raw meat and rotten flesh.
    *   Cooked or "clean" foods restore less hunger/saturation than normal.
*   **Floor-Walker Amphibian** (Hard Requirement + Bonus):
    *   You can walk on the bottom underwater and breathe normally.
    *   You cannot swim upward quickly; to reposition, you must path using terrain.
*   **Night Stalker** (Bonus + Drawback):
    *   Gain speed and damage at night.
    *   Suffer reduced vision range or lower regen in daylight.
*   **Burdened Tank** (Hard Requirement + Bonus):
    *   Gain strong defense while wearing heavy armor only.
    *   If armor is removed, lifestyle bonus is disabled.

### 🌌 Game-Changing Essence Rule (Allowed by Design)
Some essences are allowed to be **game-changing**, not just small stat bumps. We can be highly creative as long as powers still respect core system identity and counterplay.

**Allowed "big impact" categories:**
*   **Insight / Analysis**:
    *   See rounded player stats.
    *   Detect mob hostility states and likely target ("hostile toward who").
    *   Reveal hidden trait hints on essences/items.
*   **Craft / Economy Amplification**:
    *   Better crafting outcomes (quality roll, bonus yield chance).
    *   Alchemy enhancement (stronger effect, longer duration, reduced penalty).
    *   Drawback mitigation (never full removal unless high cost/condition exists).
*   **Control / Utility Extremes**:
    *   Strong range/sense tools (loot sensing, threat sensing, aura range).
    *   Specialized duplication-like behavior only under strict rules (e.g., temporary spectral copies, cooldown-gated mirrors, non-tradable outputs).

**Balance constraints (must keep):**
*   Every essence remains viable on its own (no mandatory combo lock).
*   Game-changing effects require meaningful tradeoffs:
    *   mana upkeep, cooldown windows, condition gates, or vulnerability phases.
*   Avoid permanent economy-breaking loops:
    *   no infinite cloning, no free top-tier potion chain, no zero-cost craft scaling.
*   Synergy is additive and expressive, not required to function.

**Range/Stat philosophy:**
*   Reuse shared stats where possible (example: reach/range/sense channels with multiple meanings).
*   A single visible stat may map to several hidden effects.
*   This supports future class fantasy (ex: Mage "Research" to reveal exact internal numbers).

**Power Scaling & Synergy Rule (Required for future design):**
*   Powers should support stat-based scaling where it improves build expression.
*   Use a clear pattern: `final_power_value = base_value + (scaling_stat * coefficient)`.
*   Examples:
    *   Item sense range scales from a range/sense stat.
    *   Active offense abilities scale from offense/ability power stats.
    *   Defensive actives or passives scale from armor/toughness/resistance channels.
*   Essences should create synergy between:
    *   base attributes,
    *   power scaling coefficients,
    *   and other equipped essences.
*   Keep scaling additive and bounded:
    *   no mandatory "perfect combo" requirement,
    *   no runaway exponential growth,
    *   each essence remains viable without synergy.
*   Tooltip/UX should stay obfuscated by default:
    *   show simplified stat impact to normal players,
    *   reserve exact scaling breakdown for advanced/research mechanics.

---

## 🧬 Implemented Essences

### 🧚 Allay (The Hoarder)
*   **Class Affinity**: Thief / Scout
*   **Stats (Base Attributes)**:
    *   🟢 **(+)** +15% Movement Speed
    *   🟢 **(+)** +2 Fortune
    *   🟢 **(+)** +1 Collection Range
    *   🟢 **(+)** +2 Block Interaction Range
    *   🟢 **(+)** +10% Stamina
    *   🔴 **(-)** -2 Armor
    *   🔴 **(-)** -10% Attack Damage
*   **Powers**:
    *   ⚔️ **Active: Magnetic Collection** (`activepull.json`)
        *   *Description:* Hold key to pull items within 28 blocks toward you. Active while held.
        *   *Cost:* 10 mana/sec (0.5 mana/tick).
    *   🛡️ **Passive: Collector's Intuition** (`item_whisperer.json`)
        *   *Description:* Item entities within 30 blocks glow with an outline visible through walls.
    *   🩸 **Lifestyle: Collector's Rush** (`hoarder_lifestyle.json`)
        *   *Mechanic:* Collection drive bar drains ~0.2%/sec. New item pickups charge the bar.
        *   **High Charge (≥75%)**:
            *   +20% Movement Speed
            *   +20% Mining Speed (Haste)
            *   +10 Mana Regen
        *   **Empty (0%)**:
            *   -2.5 Mana Regen
            *   -25% Stamina (Hunger drains faster)

### 🛡️ Armadillo (The Bulwark)
*   **Class Affinity**: Guardian / Scout
*   **Stats (Base Attributes)**:
    *   🟢 **(+)** +4 Armor
    *   🟢 **(+)** +0.20 Knockback Resistance
    *   🟢 **(+)** +25% Fall Reduction
    *   🟢 **(+)** +10% Stamina
    *   🟢 **(+)** +8% Melee Resistance
    *   🔴 **(-)** -10% Movement Speed
    *   🔴 **(-)** -12% Attack Speed
    *   🔴 **(-)** -10% Mining Speed
*   **Powers**:
    *   ⚔️ **Active: Ball Up** (`armadillo_ball_up.json`)
        *   *Description:* Curl into a ball for 5s. You cannot move, attack, mine, or use items while curled.
        *   *Effects:* +8 Armor, +0.5 Knockback Resistance, +20% Melee Resistance, Resistance I (20% DR), -65% Size.
        *   *Cost:* 10 mana
        *   *Cooldown:* 5s
    *   🛡️ **Passive: Scute Rebound** (`scute_rebound.json`)
        *   *Description:* Taking damage builds momentum. When momentum fills, a short "Rebound Window" triggers.
        *   *Effect:* +12% Movement Speed, +12% Melee Damage, +10% Attack Speed for 1.5s.
    *   🩸 **Lifestyle: Threat Rhythm** (`threat_rhythm_lifestyle.json`)
        *   *Mechanic:* Build "Resolve" while staying grounded near hostile mobs (facing them). Lose Resolve quickly when kiting away or losing aggro.
        *   **Steady (Resolve ≥ 75)**:
            *   +30% Armor (cumulative)
            *   +3 Flat Armor (cumulative)
            *   +0.08 Knockback Resistance (cumulative)
            *   +12% Melee Resistance (cumulative)
            *   +6 Mana Regen (cumulative)
        *   **Broken (Resolve ≤ 25 & Chased)**:
            *   -8% Stamina
            *   -0.05 Knockback Resistance
            *   -6% Melee Resistance
            *   -4% Movement Speed
            *   *(If Resolve ≤ 10)*: -12% Armor

### 🦎 Axolotl (The Amphibian)
*   **Class Affinity**: Scout / Healer
*   **Stats (Base Attributes)**:
    *   🟢 **(+)** +20% Water Speed
    *   🟢 **(+)** +50% Water Visibility
    *   🟢 **(+)** +4 Max Health (2 hearts)
    *   🟢 **(+)** +1 Attack Damage
    *   🔴 **(-)** -10% Movement Speed
    *   🔴 **(-)** -10% Stamina
    *   🔴 **(-)** -2 Armor
*   **Powers**:
    *   ⚔️ **Active: Play Dead** (`play_dead.json`)
        *   *Description:* Feign death for 5 seconds. Immobilized; gain Resistance II, Regeneration I, Invisibility.
        *   *Cost:* 25 mana to activate, 8 mana/sec while active.
        *   *Cooldown:* 5s (key cooldown)
    *   🛡️ **Passive: Amphibious** (`amphibious.json`)
        *   *Description:* Water Breathing and Night Vision. Water speed improved by essence attributes.
    *   🩸 **Lifestyle: Hydration** (`hydration_lifestyle.json`)
        *   *Mechanic:* Hydration fills in water or rain (+5/sec), drains on dry land (-1/sec).
        *   **High (≥75%)**:
            *   +3 Attack Damage (Strength I equivalent, while submerged)
            *   +20% Movement Speed (while submerged)
        *   **Low (<25%, >0)**:
            *   Weakness I, Slowness I
        *   **Empty (0%)**:
            *   Wither I (damage over time)

---

## 📚 Available Attributes Reference

Use these IDs when defining stats in JSON files.

### 🔷 Vanilla (Minecraft)
*   `minecraft:generic.max_health`
*   `minecraft:generic.movement_speed`
*   `minecraft:generic.attack_damage`
*   `minecraft:generic.attack_speed`
*   `minecraft:generic.armor`
*   `minecraft:generic.armor_toughness`
*   `minecraft:generic.knockback_resistance`
*   `minecraft:generic.luck`
*   `minecraft:generic.follow_range`
*   `minecraft:player.block_interaction_range`
*   `minecraft:player.entity_interaction_range`
*   `minecraft:player.block_break_speed`
*   `minecraft:player.sneaking_speed`
*   `minecraft:player.submerged_mining_speed`
*   `minecraft:player.sweeping_damage_ratio`

### 🐡 Pufferfish's Attributes (`puffish_attributes`)
*   `puffish_attributes:player.fortune`
*   `puffish_attributes:player.stamina`
*   `puffish_attributes:player.mining_speed`
*   `puffish_attributes:player.looting`
*   `puffish_attributes:player.crit_chance`
*   `puffish_attributes:player.life_steal`
*   `puffish_attributes:player.natural_regeneration`
*   `puffish_attributes:player.resistance`
*   `puffish_attributes:player.magic_resistance`
*   `puffish_attributes:player.melee_resistance`
*   `puffish_attributes:player.ranged_resistance`
*   `puffish_attributes:player.magic_damage`
*   `puffish_attributes:player.melee_damage`
*   `puffish_attributes:player.ranged_damage`
*   `puffish_attributes:player.pickaxe_speed`
*   `puffish_attributes:player.axe_speed`
*   `puffish_attributes:player.shovel_speed`
*   `puffish_attributes:player.fall_reduction`

### ➕ Additional Entity Attributes (`additionalentityattributes`)
*   `additionalentityattributes:player.collection_range`
*   `additionalentityattributes:player.critical_bonus_damage`
*   `additionalentityattributes:player.water_speed`
*   `additionalentityattributes:player.lava_speed`
*   `additionalentityattributes:player.dig_speed`
*   `additionalentityattributes:player.water_visibility`
*   `additionalentityattributes:player.lava_visibility`
*   `additionalentityattributes:player.bonus_loot_count_rolls`
*   `additionalentityattributes:player.bonus_rare_loot_rolls`
*   `additionalentityattributes:generic.magic_protection`

### 🔮 EssenceLib Custom
*   `essencelib:max_mana`
*   `essencelib:current_mana`
*   `essencelib:mana_regen`
