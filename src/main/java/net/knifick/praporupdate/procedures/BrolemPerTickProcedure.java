package net.knifick.praporupdate.procedures;

import org.checkerframework.checker.units.qual.s;

import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;

import net.knifick.praporupdate.entity.BrolemEntity;

public class BrolemPerTickProcedure {
	public static void execute(double y, Entity entity) {
		if (entity == null)
			return;
		if (((((BrolemEntity) entity).animationprocedure).equals("idle") || (((BrolemEntity) entity).animationprocedure).equals("empty")) && !(entity instanceof BrolemEntity _datEntL2 && _datEntL2.getEntityData().get(BrolemEntity.DATA_IsAlive))) {
			if (entity instanceof BrolemEntity) {
				((BrolemEntity) entity).setAnimation("ruins");
			}
		}
		if (!(entity instanceof BrolemEntity _datEntL4 && _datEntL4.getEntityData().get(BrolemEntity.DATA_IsAlive))) {
			{
				Entity _ent = entity;
				_ent.teleportTo(new Object() {
					double convert(String s) {
						try {
							return Double.parseDouble(s.trim());
						} catch (Exception e) {
						}
						return 0;
					}
				}.convert(entity instanceof BrolemEntity _datEntS ? _datEntS.getEntityData().get(BrolemEntity.DATA_xs) : ""), y, new Object() {
					double convert(String s) {
						try {
							return Double.parseDouble(s.trim());
						} catch (Exception e) {
						}
						return 0;
					}
				}.convert(entity instanceof BrolemEntity _datEntS ? _datEntS.getEntityData().get(BrolemEntity.DATA_zs) : ""));
				if (_ent instanceof ServerPlayer _serverPlayer)
					_serverPlayer.connection.teleport(new Object() {
						double convert(String s) {
							try {
								return Double.parseDouble(s.trim());
							} catch (Exception e) {
							}
							return 0;
						}
					}.convert(entity instanceof BrolemEntity _datEntS ? _datEntS.getEntityData().get(BrolemEntity.DATA_xs) : ""), y, new Object() {
						double convert(String s) {
							try {
								return Double.parseDouble(s.trim());
							} catch (Exception e) {
							}
							return 0;
						}
					}.convert(entity instanceof BrolemEntity _datEntS ? _datEntS.getEntityData().get(BrolemEntity.DATA_zs) : ""), _ent.getYRot(), _ent.getXRot());
			}
		}
	}
}
