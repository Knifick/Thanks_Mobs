
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.knifick.praporupdate.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

import net.knifick.praporupdate.PraporMod;

public class PraporModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, PraporMod.MODID);
	public static final DeferredHolder<SoundEvent, SoundEvent> HURT = REGISTRY.register("hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "hurt")));
	public static final DeferredHolder<SoundEvent, SoundEvent> DEATH = REGISTRY.register("death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "death")));
	public static final DeferredHolder<SoundEvent, SoundEvent> POOKER_DISSAPEAR = REGISTRY.register("pooker_dissapear", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "pooker_dissapear")));
	public static final DeferredHolder<SoundEvent, SoundEvent> IDLE = REGISTRY.register("idle", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "idle")));
	public static final DeferredHolder<SoundEvent, SoundEvent> POOKER_IDLE = REGISTRY.register("pooker_idle", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "pooker_idle")));
	public static final DeferredHolder<SoundEvent, SoundEvent> SCREAM = REGISTRY.register("scream", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "scream")));
	public static final DeferredHolder<SoundEvent, SoundEvent> NARATOR_HIT = REGISTRY.register("narator_hit", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "narator_hit")));
	public static final DeferredHolder<SoundEvent, SoundEvent> ELECTROCUTE = REGISTRY.register("electrocute", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "electrocute")));
	public static final DeferredHolder<SoundEvent, SoundEvent> PLEASWWFTHENARRATOR = REGISTRY.register("pleaswwfthenarrator", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "pleaswwfthenarrator")));
	public static final DeferredHolder<SoundEvent, SoundEvent> NARRATOR_VOICE_HIT = REGISTRY.register("narrator_voice_hit", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "narrator_voice_hit")));
	public static final DeferredHolder<SoundEvent, SoundEvent> SOUL_SOUNDS = REGISTRY.register("soul_sounds", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "soul_sounds")));
	public static final DeferredHolder<SoundEvent, SoundEvent> BASTARD_IDLE = REGISTRY.register("bastard_idle", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "bastard_idle")));
	public static final DeferredHolder<SoundEvent, SoundEvent> BASTARD_HURT = REGISTRY.register("bastard_hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "bastard_hurt")));
	public static final DeferredHolder<SoundEvent, SoundEvent> BASTARD_DEATH = REGISTRY.register("bastard_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "bastard_death")));
	public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_TRACK = REGISTRY.register("sound_track", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "sound_track")));
	public static final DeferredHolder<SoundEvent, SoundEvent> BUILD_BRO = REGISTRY.register("build_bro", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "build_bro")));
	public static final DeferredHolder<SoundEvent, SoundEvent> GOLEM_HIT = REGISTRY.register("golem_hit", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "golem_hit")));
	public static final DeferredHolder<SoundEvent, SoundEvent> GOLEM_DEATH = REGISTRY.register("golem_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "golem_death")));
	public static final DeferredHolder<SoundEvent, SoundEvent> GOLEM_STEPS = REGISTRY.register("golem_steps", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "golem_steps")));
	public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_IRON = REGISTRY.register("idle_iron", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "idle_iron")));
	public static final DeferredHolder<SoundEvent, SoundEvent> IRONKIN_GROUND_HIT = REGISTRY.register("ironkin_ground_hit", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "ironkin_ground_hit")));
	public static final DeferredHolder<SoundEvent, SoundEvent> SUMMON = REGISTRY.register("summon", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "summon")));
	public static final DeferredHolder<SoundEvent, SoundEvent> PLUH = REGISTRY.register("pluh", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "pluh")));
	public static final DeferredHolder<SoundEvent, SoundEvent> BROLEM_RUINS = REGISTRY.register("brolem_ruins", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "brolem_ruins")));
	public static final DeferredHolder<SoundEvent, SoundEvent> ADOPT = REGISTRY.register("adopt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "adopt")));
	public static final DeferredHolder<SoundEvent, SoundEvent> THANKS_STREET = REGISTRY.register("thanks_street", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "thanks_street")));
	public static final DeferredHolder<SoundEvent, SoundEvent> DICTOR_SOUND = REGISTRY.register("dictor_sound", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("prapor", "dictor_sound")));
}
