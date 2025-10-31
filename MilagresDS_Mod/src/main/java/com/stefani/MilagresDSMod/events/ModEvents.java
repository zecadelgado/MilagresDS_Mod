package com.stefani.MilagresDSMod.events;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.attribute.playerattributesprovider;
import com.stefani.MilagresDSMod.capability.playermanaprovider;
import com.stefani.MilagresDSMod.capability.playerspellsprovider;
import com.stefani.MilagresDSMod.config.ModCommonConfig;
import com.stefani.MilagresDSMod.network.modpackets;
import com.stefani.MilagresDSMod.server.stats.ConstitutionApplier;
import com.stefani.MilagresDSMod.util.BloodstainHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MilagresDSMod.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            playermanaprovider manaProvider = new playermanaprovider();
            playerspellsprovider spellsProvider = new playerspellsprovider();
            playerattributesprovider attributesProvider = new playerattributesprovider();
            event.addCapability(playermanaprovider.ID, manaProvider);
            event.addCapability(playerspellsprovider.ID, spellsProvider);
            event.addCapability(playerattributesprovider.ID, attributesProvider);

            attributesProvider.getCapability(playerattributesprovider.PLAYER_ATTRIBUTES, null).ifPresent(attributes -> {
                if (attributes.getLevel() <= 0) {
                    attributes.setLevel(Math.max(1, ModCommonConfig.STARTING_LEVEL.get()));
                }
                if (attributes.getPoints() < 0) {
                    attributes.setPoints(Math.max(0, ModCommonConfig.STARTING_POINTS.get()));
                } else if (attributes.getPoints() == 0 && ModCommonConfig.STARTING_POINTS.get() > 0
                        && attributes.getIntelligence() == 0 && attributes.getFaith() == 0 && attributes.getArcane() == 0) {
                    attributes.setPoints(ModCommonConfig.STARTING_POINTS.get());
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            modpackets.sendManaSync(serverPlayer);
            event.getEntity().getCapability(playerattributesprovider.PLAYER_ATTRIBUTES).ifPresent(attributes -> {
                ConstitutionApplier.apply(serverPlayer, attributes);
                modpackets.sendAttributesSync(serverPlayer, attributes);
                modpackets.sendRunesSync(serverPlayer, attributes);
                modpackets.sendBloodstainSync(serverPlayer, attributes);
            });
            modpackets.sendSpellSnapshot(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        boolean wasDeath = event.isWasDeath();

        if (wasDeath) {
            event.getOriginal().reviveCaps();
        }

        event.getOriginal().getCapability(playermanaprovider.PLAYER_MANA).ifPresent(oldMana ->
                event.getEntity().getCapability(playermanaprovider.PLAYER_MANA).ifPresent(newMana -> {
                    newMana.setMaxMana(oldMana.getMaxMana());
                    newMana.setMana(oldMana.getMana());
                }));

        event.getOriginal().getCapability(playerspellsprovider.PLAYER_SPELLS).ifPresent(oldSpells ->
                event.getEntity().getCapability(playerspellsprovider.PLAYER_SPELLS)
                        .ifPresent(newSpells -> newSpells.deserializeNBT(oldSpells.serializeNBT())));

        event.getOriginal().getCapability(playerattributesprovider.PLAYER_ATTRIBUTES).ifPresent(oldAttributes ->
                event.getEntity().getCapability(playerattributesprovider.PLAYER_ATTRIBUTES)
                        .ifPresent(newAttributes -> {
                            newAttributes.deserializeNBT(oldAttributes.serializeNBT());
                            if (wasDeath && event.getEntity() instanceof ServerPlayer newServerPlayer
                                    && event.getOriginal() instanceof ServerPlayer oldServerPlayer) {
                                MinecraftServer server = newServerPlayer.serverLevel().getServer();
                                GlobalPos previous = newAttributes.getBloodstainLocation().orElse(null);
                                BloodstainHelper.remove(server, previous);

                                long lostRunes = Math.max(0L, oldAttributes.getXp());
                                newAttributes.setLostRunes(lostRunes);
                                newAttributes.setXp(0L);
                                newAttributes.clearBloodstain();

                                if (lostRunes > 0L) {
                                    BlockPos deathPos = oldServerPlayer.blockPosition();
                                    BloodstainHelper.place(oldServerPlayer.serverLevel(), deathPos,
                                            newServerPlayer.getUUID(), lostRunes)
                                            .ifPresent(placed -> newAttributes.setBloodstainLocation(
                                                    GlobalPos.of(oldServerPlayer.serverLevel().dimension(), placed)));
                                }

                                modpackets.sendRunesSync(newServerPlayer, newAttributes);
                                modpackets.sendBloodstainSync(newServerPlayer, newAttributes);
                            }
                            if (event.getEntity() instanceof ServerPlayer newServerPlayer) {
                                ConstitutionApplier.apply(newServerPlayer, newAttributes);
                            }
                        }));

        if (wasDeath) {
            event.getOriginal().invalidateCaps();
        }

        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            modpackets.sendManaSync(serverPlayer);
            event.getEntity().getCapability(playerattributesprovider.PLAYER_ATTRIBUTES)
                    .ifPresent(attributes -> {
                        ConstitutionApplier.apply(serverPlayer, attributes);
                        modpackets.sendAttributesSync(serverPlayer, attributes);
                    });
            modpackets.sendSpellSnapshot(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            modpackets.sendManaSync(serverPlayer);
            event.getEntity().getCapability(playerattributesprovider.PLAYER_ATTRIBUTES).ifPresent(attributes -> {
                ConstitutionApplier.apply(serverPlayer, attributes);
                modpackets.sendAttributesSync(serverPlayer, attributes);
                modpackets.sendRunesSync(serverPlayer, attributes);
                modpackets.sendBloodstainSync(serverPlayer, attributes);
            });
            modpackets.sendSpellSnapshot(serverPlayer);
        }
    }
}
