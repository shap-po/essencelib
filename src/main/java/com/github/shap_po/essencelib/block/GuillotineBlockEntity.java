package com.github.shap_po.essencelib.block;

import com.github.shap_po.essencelib.component.CollectorRushComponent;
import com.github.shap_po.essencelib.component.DownedComponent;
import com.github.shap_po.essencelib.component.LevelComponent;
import com.github.shap_po.essencelib.component.RestrainedComponent;
import com.github.shap_po.essencelib.guillotine.RestrainedHelper;
import com.github.shap_po.essencelib.registry.ModBlockEntities;
import com.github.shap_po.essencelib.registry.ManaAttributeRegistry;
import com.github.shap_po.essencelib.util.StructureRollbackTracker;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Block entity for the guillotine. Right-click triggers the blade drop animation.
 */
public class GuillotineBlockEntity extends BlockEntity implements GeoBlockEntity {

    private static final RawAnimation ACTIVATE_ANIM = RawAnimation.begin().thenPlay("activate");
    public static final String CONTROLLER_NAME = "guillotine_controller";
    // Fast drop cadence: strike almost immediately, then finish execution shortly after.
    private static final int SEQUENCE_TOTAL_TICKS = 24; // 1.2s
    private static final int STRIKE_TICK = 3; // 0.15s after activation
    private static final int FAST_BLOOD_PHASE_TICKS = 10; // 0.5s
    private static final int SLOW_BLOOD_PHASE_TICKS = 20; // 1.0s

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int sequenceTick = -1;

    public GuillotineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GUILLOTINE, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, CONTROLLER_NAME, 0, s -> PlayState.STOP)
            .triggerableAnim("activate", ACTIVATE_ANIM));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public void activate() {
        if (!(world instanceof ServerWorld)) return;
        if (sequenceTick >= 0) return; // Prevent retrigger spam while active.
        sequenceTick = 0;
        triggerAnim(CONTROLLER_NAME, "activate");
    }

    public static void tick(World world, BlockPos pos, BlockState state, GuillotineBlockEntity be) {
        if (!(world instanceof ServerWorld serverWorld)) return;
        if (be.sequenceTick < 0) return;
        be.sequenceTick++;
        if (be.sequenceTick == STRIKE_TICK) {
            be.executeStrikeVisuals(serverWorld, pos);
        }
        if (be.sequenceTick >= STRIKE_TICK && be.sequenceTick < SEQUENCE_TOTAL_TICKS) {
            be.spawnExecutionParticles(serverWorld, pos, be.sequenceTick);
        }
        if (be.sequenceTick >= SEQUENCE_TOTAL_TICKS) {
            be.executeFinalKill(serverWorld, pos);
            be.sequenceTick = -1;
        }
    }

    private void executeStrikeVisuals(ServerWorld world, BlockPos pos) {
        for (PlayerEntity player : world.getPlayers()) {
            if (!isRestrainedAtThisGuillotine(world, pos, player)) continue;
            spawnHeadDrop(world, pos, player);
        }
    }

    private void spawnHeadDrop(ServerWorld world, BlockPos pos, PlayerEntity player) {
        ItemStack head = new ItemStack(Items.PLAYER_HEAD);
        Vec3d start = new Vec3d(pos.getX() + 0.5, pos.getY() + 1.86, pos.getZ() + 0.12);
        Vec3d basket = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.78, pos.getZ() - 0.30);
        Vec3d driftToBasket = basket.subtract(start);
        ItemEntity droppedHead = new ItemEntity(world, start.x, start.y, start.z, head);
        droppedHead.setVelocity(
            driftToBasket.x * 0.09,
            -0.33 + world.random.nextDouble() * 0.04,
            driftToBasket.z * 0.11
        );
        droppedHead.setNoGravity(false);
        world.spawnEntity(droppedHead);
    }

    private void spawnExecutionParticles(ServerWorld world, BlockPos pos, int tick) {
        int sinceStrike = tick - STRIKE_TICK;
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 1.02;
        double z = pos.getZ() - 0.05;

        int burstCount;
        double speedScale;
        if (sinceStrike < FAST_BLOOD_PHASE_TICKS) {
            burstCount = 30;
            speedScale = 1.0;
        } else if (sinceStrike < SLOW_BLOOD_PHASE_TICKS) {
            burstCount = 10;
            speedScale = 0.35;
        } else {
            burstCount = 4;
            speedScale = 0.12;
        }

        for (int i = 0; i < burstCount; i++) {
            double vx = (world.random.nextDouble() - 0.5) * 0.5 * speedScale;
            double vy = (0.1 + world.random.nextDouble() * 0.24) * speedScale;
            double vz = -(0.45 + world.random.nextDouble() * 0.95) * speedScale;
            world.spawnParticles(
                new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.REDSTONE_BLOCK.getDefaultState()),
                x, y, z,
                0, vx, vy, vz, 1.0
            );
            if ((i & 1) == 0) {
                world.spawnParticles(
                    new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, Blocks.REDSTONE_BLOCK.getDefaultState()),
                    x, y, z,
                    0, vx * 0.65, vy * 0.35, vz * 0.65, 1.0
                );
            }
        }
        world.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, x, y, z, 3, 0.12, 0.08, 0.12, 0.01);
    }

    private void executeFinalKill(ServerWorld world, BlockPos pos) {
        for (PlayerEntity player : world.getPlayers()) {
            if (!isRestrainedAtThisGuillotine(world, pos, player)) continue;
            if (player instanceof ServerPlayerEntity serverPlayer) {
                StructureRollbackTracker.rollbackAll(serverPlayer);
            }
            wipePlayerProgress(player);
            RestrainedHelper.release(player);
            player.damage(world.getDamageSources().genericKill(), Float.MAX_VALUE);
        }
    }

    private boolean isRestrainedAtThisGuillotine(ServerWorld world, BlockPos pos, PlayerEntity player) {
        RestrainedComponent comp = RestrainedComponent.getNullable(player);
        if (comp == null || !comp.isRestrained()) return false;
        if (comp.getAnchorPos() == null || comp.getDimension() == null) return false;
        if (!comp.getAnchorPos().equals(pos)) return false;
        return world.getRegistryKey().equals(comp.getDimension());
    }

    /**
     * Guillotine execution is a full character reset: wipe progression, essence loadout,
     * trinkets, and mutable resources before the kill lands.
     */
    private void wipePlayerProgress(PlayerEntity player) {
        // Vanilla inventory + armor + offhand + ender chest.
        player.getInventory().clear();
        player.getEnderChestInventory().clear();
        player.getInventory().markDirty();

        // Remove all trinkets/essences from equipped slots.
        TrinketsApi.getTrinketComponent(player).ifPresent(comp ->
            comp.getInventory().values().forEach(group ->
                group.values().forEach(inv -> {
                    for (int i = 0; i < inv.size(); i++) {
                        inv.setStack(i, ItemStack.EMPTY);
                    }
                })
            )
        );

        // Reset progression components.
        LevelComponent level = LevelComponent.getNullable(player);
        if (level != null) {
            level.clearUniqueKills();
            level.updateLevel(true);
        }
        CollectorRushComponent collectorRush = CollectorRushComponent.getNullable(player);
        if (collectorRush != null) {
            collectorRush.clear();
        }
        DownedComponent downed = DownedComponent.getNullable(player);
        if (downed != null) {
            downed.setDowned(false);
            downed.clearCorpseLocation();
        }

        // Reset mana channels to default starting values.
        EntityAttributeInstance maxMana = player.getAttributeInstance(ManaAttributeRegistry.getMaxManaEntry());
        EntityAttributeInstance currentMana = player.getAttributeInstance(ManaAttributeRegistry.getCurrentManaEntry());
        EntityAttributeInstance manaRegen = player.getAttributeInstance(ManaAttributeRegistry.getManaRegenEntry());
        if (maxMana != null) maxMana.setBaseValue(100.0);
        if (currentMana != null) currentMana.setBaseValue(0.0);
        if (manaRegen != null) manaRegen.setBaseValue(0.4);

        // Reset combat/runtime state.
        player.clearStatusEffects();
        if (player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.setExperiencePoints(0);
            serverPlayer.setExperienceLevel(0);
        }
        player.setScore(0);
        player.playerScreenHandler.sendContentUpdates();
    }
}
