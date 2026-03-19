# Dungeon & Stone: Master Design Document

**Motto:** Gear is eternal, but the body is fragile. Pay your tax or feed the crows.

## 🕒 I. The World Cycle: "The Shift"

The overworld is a revolving door of chaos known as "Dungeon Soup."

**The Gathering:** Every hour, the Kingdom's Gate opens. Players have 10 minutes to form parties.

**Unity Spawning:** If a Mage is in the party, all members spawn together. Without a Mage, players are scattered up to 500 blocks apart.

**The Shift (50 Minutes):** A 1000x1000 custom world generates (Flooded, Underground, Sky Islands, Lava Oceans, etc.).

**The Floor Guardian:** Every shift contains one Boss. Killing it yields Grand Essences (pure stats) and rare materials for Artificers.

**The Collapse (10 Minutes):** The world begins to de-materialize into the void.

**The Beacon:** A pillar of light marks the exit for all to see, usually creating a "King of the Hill" warzone.

**Scout Sense:** Scouts can detect the exit earlier and navigate there stealthily before the beacon activates.

## 🧬 II. The Essence & Ability System

Standard enchantments are replaced by monster souls (Essences).

### 1. Core Mechanics

**Attributes:** Every essence has 5 Positives and 2-3 Negatives (e.g., +Strength, +Health, -Mana, -Speed).

**Slotting:** Consumers absorb essences (1 Level = 1 Slot). Mages bind them to staves; Summoners turn them into crystals.

**Sensory Obfuscation:**
* Consumers see a vague, AI-generated "Whisper" describing how the essence feels (e.g., "The soul within is a violent vortex; it promises the strength of a storm but demands a focused mind.").
* Mages/Artificers see accurate numbers and label ability types (Active/Passive/Lifestyle).

### 2. Ability Types

**Active:** Mana-spending skills (dashes, strikes, shields).

**Passive:** Constant buffs (night vision, thorns, speed).

**Lifestyle:** Massive power with a behavioral cost (e.g., must eat raw meat to maintain strength).

**Mage Spells:** Mages unlock specific spells based on the essences bound to their staff.

### 3. State Management & Cleanup

**Power Removal:** When an essence is unequipped, all associated powers are automatically revoked by the Apoli/Shappoli system.

**Centralized Cleanup:** A single Java-based cleanup system (`EssenceCleanupHelper`) automatically handles cleanup for ALL essences when unequipped:
* Potion effects (slowness, weakness, invisibility, etc.) are cleared immediately
* Power resources are automatically cleaned up by Apoli when powers are revoked
* Visual effects (hologram models) are removed via power revocation
* No lingering state persists between equip/unequip cycles

**Implementation:** The cleanup system hooks into the `ShappoliTrinketItemPowersComponentMixin.essencelib$onUnequip` method, detecting when any `MobEssenceTrinketItem` is unequipped and clearing common effects. This approach scales automatically to all essences without requiring individual cleanup powers.

## 📈 III. Progression: Unique Kill System

Standard XP is disabled. You are rewarded for discovery, not grinding.

**The Bestiary:** Your level is determined by how many Unique Mobs you have killed for the first time.

**The Curve:**
* Level 1: 5 Unique Kills
* Level 2: 10 Unique Kills
* Level 3: 20 Unique Kills
* Level 4+: Percentage-based discovery requirements.

**Fresh Body:** Dying results in losing all gear and levels. You respawn at Level 1 but with a "Fresh Body" (100% HP).

## 🛡️ IV. The Class Ecosystem

Classes are divided by their relationship with Essences: Those who consume them for power, and those who enable the party through utility.

### The Consumers (Absorb Essences)

These classes face higher Monthly Taxes due to soul corruption, but gain immense raw power.

**Barbarian:** The Anchor. Absorbs physical and tank-heavy essences.
* **Role:** The "Mule." They can carry downed or dead allies out of a shift to save their loot.
* **Penalty:** "Barbarian’s Hunger." They have the fastest hunger drain and suffer the most when underfed.

**Archer:** The Sniper. Absorbs precision and speed essences.
* **Role:** High-range DPS. They can engage Bosses from safety but are extremely fragile; one slip-up usually results in a "Fresh Body" respawn.

**Thief:** The Looter. Absorbs stealth and critical-hit essences.
* **Role:** Economic engine. They can sense high-value Mana Stone drops through walls and open locked dungeon chests without keys.

**Scout:** The Navigator. Absorbs utility and speed essences.
* **Role:** Survival expert. They sense the extraction rifts before the beacon activates and can identify rare dungeon flora/seeds for the Alchemist.

**Healer:** The Battery. Absorbs "Lifestyle" essences focused on mana/regen.
* **Role:** Party sustain. Uses mana-based auras to provide the only reliable healing mid-dungeon.

**Alchemist:** The Chef. Absorbs essences to master the "taste" of the Labyrinth.
* **Role:** Utility provider. They craft potions and high-tier meals that are the only way to mitigate the negative traits of essences.

### The Enablers (Utilize Essences)

**Mage:** The Scholar.
* **Role:** Tactical Anchor. Provides the Unity Bond at spawn. They are the only class capable of Uncovering the hidden stats and powers of unidentified essences and gear.

**Artificer:** The Smith.
* **Role:** Industry leader. They socket essences into gear and repair high-tier items. They earn wealth by charging Consumers for gear maintenance.

**Summoner:** The Commander.
* **Role:** Crowd control. Converts essences into Summon Crystals. These mobs fight for the party and can be recalled if they survive the shift, but the essence is permanently bound to the crystal.

## ❤️ V. Survival & Recovery

Survival is a slow-burn battle against exhaustion.

**No Natural Regen:** Health only restores through Healer spells, Alchemist potions, or Sleep.

**Food & Rarity:** Seeds are rare dungeon loot. Farming in the Kingdom is a high-profit trade. Hunger amplifies Essence Negatives.

**Exhaustion:** Exiting a dungeon at low HP leaves you with "Lingering Wounds" (debuffs).

**Recovery:** Players must rest in safe-zone Inns. Quality of sleep determines Mana recovery and wound healing speed.

## ⚖️ VI. Economy & The Crown

The Kingdom provides safety, but it isn't free.

**Mana Stones:** Physical drops from all mobs. These are the primary currency traded at the Merchant Guild.

**The Monthly Execution:** Every 1st of the month, a tax is due based on Level and Essence count.

**Enabler Advantage:** Mages and Artificers pay lower taxes as they do not "corrupt" their souls with essences.

**Failure:** Unpaid players are caged in the town square and executed, resulting in a full account wipe.

## 🧪 VII. Future Mob Progression Roadmap (Planned)

This section is planned for later implementation and should guide future dungeon-focused updates.

### 1) Hidden Mob Stat Layer
- Mobs use hidden channels in addition to visible combat stats:
  - `max_mana`
  - `current_mana`
  - `mana_regen`
- Hidden channels are initialized from:
  1. vanilla/default mob attributes (base profile), then
  2. essence-derived bonuses for that mob type (identity carry-over).
- These channels are server-authoritative and primarily invisible to normal players.

### 2) Elite / Leveled Mobs (Later)
- Add optional mob level/tier system after dungeon generation is finalized.
- Candidate model:
  - levels `1..10` (10 = rare high-threat "mega" encounters),
  - elite/tier flags layered on top of level.
- Level/tier modifies:
  - hidden channels (mana capacity/regen),
  - combat profile,
  - behavior cadence and ability windows.

### 3) Essence Drop Scaling by Level (Later)
- Higher level/tier mobs can increase essence drop odds.
- Keep scaling bounded and server-configurable to avoid economy breakage.
- Level 10/high-tier encounters should remain rare, dangerous, and meaningful rather than common farm targets.
