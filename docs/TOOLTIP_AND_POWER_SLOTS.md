# Two-Window Tooltip & Power → Slot Mapping

## One snippet to share (two-window tooltip)

Copy this single block to show how the two-window tooltip is wired end-to-end:

```java
// ========== TWO-WINDOW TOOLTIP (left: powers, right: stats; Shift hides stats, shows descriptions) ==========
//
// 1) Data type – implement TooltipData so the game can pass it to your component
//    public record EssenceTooltipData(Text name, List<PowerEntry> powers, List<Text> stats) implements TooltipData {
//        public record PowerEntry(Text categoryLabel, Text powerName, String description) {}
//    }
//
// 2) When the item is hovered, Item.getTooltipData(stack) is called. Use a mixin to return your data for your item:
//    @Inject(method = "getTooltipData", at = @At("HEAD"), cancellable = true)
//    private void inject(ItemStack stack, CallbackInfoReturnable<Optional<TooltipData>> cir) {
//        if (stack.getItem() instanceof YourItem) {
//            cir.setReturnValue(YourHelper.buildTooltipData(stack).map(d -> (TooltipData) d));
//        }
//    }
//
// 3) Game then asks "who can render this TooltipData?" – register your component in client init:
//    TooltipComponentCallback.EVENT.register(data -> {
//        if (data instanceof EssenceTooltipData essenceData)
//            return new EssenceTooltipComponent(essenceData);
//        return null;
//    });
//
// 4) EssenceTooltipComponent implements TooltipComponent: getWidth/getHeight + drawText.
//    Left column = name + powers (categoryLabel: powerName; with Shift, add wrapped description).
//    Right column = stats; hide when Shift held. Use COLUMN_GAP between columns, align right column right.
//
// 5) To fill powers: get entries from the item's power component, filter by slot (active/passive/lifestyle
//    from a JSON map powerId -> "active"|"passive"|"lifestyle"), then add PowerEntry(categoryLabel, power.getName(), desc).
//    addPowerEntries(visible, "active", "✦ Active", RED, out);
//    addPowerEntries(visible, "passive", "✦ Passive", GREEN, out);
//    addPowerEntries(visible, "lifestyle", "✦ Lifestyle", YELLOW, out);
```

---

## Code snippets (copy reference)

### 1. Register custom tooltip component (client init)
```java
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import your.package.tooltip.EssenceTooltipComponent;
import your.package.tooltip.EssenceTooltipData;

// In onInitializeClient():
TooltipComponentCallback.EVENT.register(data -> {
    if (data instanceof EssenceTooltipData essenceData) {
        return new EssenceTooltipComponent(essenceData);
    }
    return null;
});
```

### 2. Provide custom TooltipData for your item (mixin)
```java
@Environment(EnvType.CLIENT)
@Mixin(Item.class)
public class MobEssenceTrinketItemTooltipMixin {

    @Inject(method = "getTooltipData", at = @At("HEAD"), cancellable = true)
    private void essencelib$injectEssenceTooltipData(ItemStack stack, CallbackInfoReturnable<Optional<TooltipData>> cir) {
        if (stack.getItem() instanceof MobEssenceTrinketItem) {
            Optional<TooltipData> data = EssenceTooltipHelper.buildTooltipData(stack).map(d -> (TooltipData) d);
            if (data.isPresent()) {
                cir.setReturnValue(data);
            }
        }
    }
}
```

### 3. Tooltip data record (implements TooltipData)
```java
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.Text;
import java.util.List;

public record EssenceTooltipData(
    Text name,
    List<PowerEntry> powers,
    List<Text> stats
) implements TooltipData {

    public record PowerEntry(
        Text categoryLabel,
        Text powerName,
        String description
    ) {}
}
```

### 4. Build tooltip data: pull powers and assign slot (active/passive/lifestyle)
```java
public static Optional<EssenceTooltipData> buildTooltipData(ItemStack stack) {
    TrinketItemPowersComponent powers = stack.get(ShappoliTrinketsDataComponentTypes.TRINKET_POWERS);
    if (powers == null) return Optional.empty();

    Text name = stack.getName().copy().formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD);
    List<EssenceTooltipData.PowerEntry> powerEntries = new ArrayList<>();
    List<TrinketItemPowersComponent.Entry> entries = new ArrayList<>(
        ((TrinketItemPowersComponentAccessor) (Object) powers).getEntries());
    List<TrinketItemPowersComponent.Entry> visible = entries.stream()
        .filter(e -> !e.hidden() && PowerManager.getNullable(e.powerId()) != null
            && PowerTooltipSlotRegistry.hasSlot(e.powerId()))
        .toList();

    addPowerEntries(visible, PowerTooltipSlotRegistry.SLOT_ACTIVE, "✦ Active", Formatting.RED, powerEntries);
    addPowerEntries(visible, PowerTooltipSlotRegistry.SLOT_PASSIVE, "✦ Passive", Formatting.GREEN, powerEntries);
    addPowerEntries(visible, PowerTooltipSlotRegistry.SLOT_LIFESTYLE, "✦ Lifestyle", Formatting.YELLOW, powerEntries);

    List<Text> stats = new ArrayList<>();
    stats.add(Text.literal("Stats").formatted(Formatting.AQUA, Formatting.BOLD));
    stats.addAll(EssenceStatHelper.buildStatLinesGrouped(stack));

    return Optional.of(new EssenceTooltipData(name, powerEntries, stats));
}

private static void addPowerEntries(List<TrinketItemPowersComponent.Entry> entries, String slot,
                                    String categoryLabel, Formatting color,
                                    List<EssenceTooltipData.PowerEntry> out) {
    List<TrinketItemPowersComponent.Entry> inSlot = entries.stream()
        .filter(e -> slot.equals(PowerTooltipSlotRegistry.getSlot(e.powerId())))
        .toList();
    Text catText = Text.literal(categoryLabel).formatted(color);
    for (TrinketItemPowersComponent.Entry entry : inSlot) {
        Power power = PowerManager.getNullable(entry.powerId());
        if (power == null) continue;
        String desc = power.getDescription() != null ? power.getDescription().getString() : "";
        out.add(new EssenceTooltipData.PowerEntry(catText, power.getName(), desc));
    }
}
```

### 5. Power → slot registry (load from JSON)
```java
// Slots: "active" | "passive" | "lifestyle". Others = hidden.
public static final String SLOT_ACTIVE = "active";
public static final String SLOT_PASSIVE = "passive";
public static final String SLOT_LIFESTYLE = "lifestyle";

public static String getSlot(Identifier powerId) {
    if (SLOTS == null) load();
    return powerId != null ? SLOTS.getOrDefault(powerId.toString(), null) : null;
}

public static boolean hasSlot(Identifier powerId) {
    String slot = getSlot(powerId);
    return SLOT_ACTIVE.equals(slot) || SLOT_PASSIVE.equals(slot) || SLOT_LIFESTYLE.equals(slot);
}
// load() reads data/essencelib/essence_tooltip_slots.json: { "namespace:path/power_id": "active"|"passive"|"lifestyle" }
```

### 6. essence_tooltip_slots.json
```json
{
  "esspack:allay_essence/activepull": "active",
  "esspack:allay_essence/item_whisperer": "passive",
  "esspack:allay_essence/hoarder_lifestyle": "lifestyle"
}
```

---

## 1. Two-column tooltip (how it works)

### Flow
1. **Item.getTooltipData** – For essence items we return custom `TooltipData` via a mixin.
2. **MobEssenceTrinketItemTooltipMixin** – When `getTooltipData(ItemStack)` is called and the item is a `MobEssenceTrinketItem`, we call `EssenceTooltipHelper.buildTooltipData(stack)` and return it as `TooltipData`.
3. **TooltipComponentCallback** (EssenceLibClient) – When the game looks for a `TooltipComponent` for some `TooltipData`, we register a callback: if the data is `EssenceTooltipData`, we return `new EssenceTooltipComponent(essenceData)`.
4. **EssenceTooltipComponent** – Renders the tooltip with:
   - **Left column:** Item name (purple bold), then power lines grouped by slot (Active / Passive / Lifestyle). With **Shift**: same plus wrapped descriptions and the “Hold Shift to pickup” hint.
   - **Right column:** “Stats” header + stat lines. **Hidden when Shift is held.**

### Layout constants (EssenceTooltipComponent)
- `LINE_HEIGHT = 10`, `PADDING = 4`, `COLUMN_GAP = 16`
- `DESC_WRAP_WIDTH = 26` (chars), `DESC_WRAP_WIDTH_SHIFT = 42` (chars when Shift)

### Data shape
- **EssenceTooltipData**: `(Text name, List<PowerEntry> powers, List<Text> stats)`
- **PowerEntry**: `(Text categoryLabel, Text powerName, String description)`  
  - `categoryLabel` is e.g. `"✦ Active"`, `"✦ Passive"`, `"✦ Lifestyle"`.

---

## 2. How we pull powers and which slot they use

### Where powers come from
- Stored on the **item stack** in the Shappoli component:  
  `stack.get(ShappoliTrinketsDataComponentTypes.TRINKET_POWERS)` → `TrinketItemPowersComponent`.
- We read the list of entries via **TrinketItemPowersComponentAccessor** (mixin):  
  `((TrinketItemPowersComponentAccessor)(Object) powers).getEntries()` → list of `Entry(powerId, hidden)`.

### Slot assignment (power ID → slot)
- **PowerTooltipSlotRegistry** decides which “slot” (category) a power belongs to.
- It loads from **`data/essencelib/essence_tooltip_slots.json`**:
  - Keys: power IDs (e.g. `"esspack:allay_essence/activepull"`).
  - Values: `"active"` | `"passive"` | `"lifestyle"`.
- Constants: `SLOT_ACTIVE`, `SLOT_PASSIVE`, `SLOT_LIFESTYLE`. Any other or missing → power is **hidden** from the tooltip.

### Building the tooltip (EssenceTooltipHelper.buildTooltipData)
1. Get `TrinketItemPowersComponent` from the stack.
2. Get all entries; keep only **visible** ones: `!e.hidden()` and `PowerManager.getNullable(e.powerId()) != null` and **PowerTooltipSlotRegistry.hasSlot(e.powerId())**.
3. For each slot in order (Active → Passive → Lifestyle), filter entries by `PowerTooltipSlotRegistry.getSlot(e.powerId()) == slot` and add them to the tooltip:
   - **Active**  → category label `"✦ Active"` (red)
   - **Passive** → `"✦ Passive"` (green)
   - **Lifestyle** → `"✦ Lifestyle"` (yellow)
4. For each entry we create **EssenceTooltipData.PowerEntry** with:
   - `categoryLabel` = that category text
   - `powerName` = `PowerManager.getNullable(entry.powerId()).getName()`
   - `description` = that power’s description string (for Shift-expanded view).
5. Stats column is built by **EssenceStatHelper.buildStatLinesGrouped(stack)** and passed as `stats` in `EssenceTooltipData`.

### Example: essence_tooltip_slots.json
```json
{
  "esspack:allay_essence/activepull": "active",
  "esspack:allay_essence/item_whisperer": "passive",
  "esspack:allay_essence/hoarder_lifestyle": "lifestyle"
}
```
- Only powers listed here with value `active` / `passive` / `lifestyle` are shown; all others are skipped.

---

## 3. File reference

| Purpose | File |
|--------|------|
| Two-column rendering | `EssenceTooltipComponent.java` |
| Tooltip data type | `EssenceTooltipData.java` |
| Provide tooltip data for essence items | `MobEssenceTrinketItemTooltipMixin.java` (inject `getTooltipData`) |
| Map TooltipData → EssenceTooltipComponent | `EssenceLibClient.java` (TooltipComponentCallback) |
| Build data: powers + stats, slot filtering | `EssenceTooltipHelper.java` |
| Power ID → slot (active/passive/lifestyle) | `PowerTooltipSlotRegistry.java` |
| Slot mapping data | `data/essencelib/essence_tooltip_slots.json` |

---

## 4. Adding a new power to the tooltip
1. Add a line in **`data/essencelib/essence_tooltip_slots.json`**:
   - `"<namespace>:<path/to/power>"`: `"active"` | `"passive"` | `"lifestyle"`.
2. No code change needed; the registry is loaded from that JSON and powers with a slot are shown in the two-column tooltip under the right category.
