package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.entity.NuisanceAllayHelper;
import com.github.shap_po.essencelib.entity.allay.behavior.NuisanceFleeBehavior;
import com.github.shap_po.essencelib.entity.allay.behavior.NuisanceHideOutOfLOSBehavior;
import com.github.shap_po.essencelib.entity.allay.behavior.NuisanceHoldingBehavior;
import com.github.shap_po.essencelib.entity.allay.behavior.NuisanceIdleBehavior;
import com.github.shap_po.essencelib.entity.allay.behavior.NuisanceAcquireFocusBehavior;
import com.github.shap_po.essencelib.entity.allay.behavior.NuisanceStalkBehavior;
import com.github.shap_po.essencelib.entity.allay.behavior.NuisanceStealBehavior;
import com.github.shap_po.essencelib.entity.allay.sensor.NuisanceChaseSensor;
import com.github.shap_po.essencelib.entity.allay.sensor.NuisanceFocusPlayerSensor;
import com.github.shap_po.essencelib.entity.allay.sensor.NuisanceLineOfSightSensor;
import com.github.shap_po.essencelib.entity.allay.sensor.NuisanceStealTargetSensor;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.passive.AllayEntity;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
@Mixin(AllayEntity.class)
public abstract class AllayEntitySBLMixin implements SmartBrainOwner {
    @Inject(method = "createBrainProfile", at = @At("HEAD"), cancellable = true)
    private void essencelib$useSmartBrainProfile(CallbackInfoReturnable<Brain.Profile<AllayEntity>> cir) {
        AllayEntity allay = (AllayEntity) (Object) this;
        // #region agent log
        NuisanceAllayHelper.debugLog(
            allay,
            "H1",
            "AllayEntitySBLMixin#createBrainProfile",
            "SBL brain profile injection hit",
            "{\"entityId\":" + allay.getId() + "}"
        );
        // #endregion
        cir.setReturnValue(new SmartBrainProvider((AllayEntity) (Object) this));
    }

    @Inject(method = "mobTick", at = @At("HEAD"))
    private void essencelib$tickSmartBrain(CallbackInfo ci) {
        AllayEntity allay = (AllayEntity) (Object) this;
        if (allay.getWorld().isClient) return;
        boolean isNuisance = NuisanceAllayHelper.isNuisance(allay);
        if ((allay.age + allay.getId()) % 20 == 0) {
            // #region agent log
            NuisanceAllayHelper.debugLog(
                allay,
                "H2",
                "AllayEntitySBLMixin#mobTick",
                "SBL tick gate evaluation",
                "{\"entityId\":" + allay.getId() + ",\"isNuisance\":" + isNuisance + ",\"age\":" + allay.age + "}"
            );
            // #endregion
        }
        if (!isNuisance) return;

        NuisanceAllayHelper.ensureNuisanceTag(allay);
        ((SmartBrainOwner) (Object) this).tickBrain(allay);
    }

    @Override
    public List<? extends ExtendedSensor<? extends AllayEntity>> getSensors() {
        return List.of(
            new NuisanceFocusPlayerSensor(),
            new NuisanceChaseSensor(),
            new NuisanceStealTargetSensor(),
            new NuisanceLineOfSightSensor()
        );
    }

    @Override
    public BrainActivityGroup<? extends AllayEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(new MoveToWalkTarget<>());
    }

    @Override
    public BrainActivityGroup<? extends AllayEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
            new FirstApplicableBehaviour<>(
                new NuisanceAcquireFocusBehavior(),
                new NuisanceHoldingBehavior(),
                new NuisanceStealBehavior(),
                new NuisanceFleeBehavior(),
                new NuisanceHideOutOfLOSBehavior(),
                new NuisanceStalkBehavior(),
                new NuisanceIdleBehavior()
            )
        );
    }

    @Override
    public BrainActivityGroup<? extends AllayEntity> getFightTasks() {
        return BrainActivityGroup.empty();
    }
}
