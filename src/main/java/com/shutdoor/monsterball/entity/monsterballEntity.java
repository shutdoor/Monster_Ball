package com.shutdoor.monsterball.entity;


import com.shutdoor.monsterball.config.monsterballConfig;
import com.shutdoor.monsterball.monsterBallMod;
import com.shutdoor.monsterball.util.NBTHelper;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.Optional;

public class monsterballEntity extends ThrownItemEntity {

	private boolean hasEntity;
	private ItemStack currentMonsterball = new ItemStack(monsterBallMod.MONSTERBALL, 1);
	public monsterballEntity(EntityType<? extends monsterballEntity> type, World worldIn) {
		super(type, worldIn);
	}

	public monsterballEntity(EntityType<? extends monsterballEntity> type, double x, double y, double z, World worldIn) {
		super(type, x, y, z, worldIn);
	}

	public monsterballEntity(LivingEntity livingEntityIn, World worldIn, ItemStack stack) {
		super(monsterBallMod.MONSTERBALL_ENTITY, livingEntityIn, worldIn);
		this.currentMonsterball = stack;
		this.hasEntity = false;
		if (this.currentMonsterball.hasNbt()) {
			this.hasEntity = NBTHelper.hasNbt(stack, "StoredEntity");
		}
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!getWorld().isClient) {
			if (hitResult.getType() == HitResult.Type.BLOCK) {
				if (this.hasEntity) {
					Optional<Entity> loadEntity = EntityType.getEntityFromNbt(NBTHelper.getNbt(currentMonsterball, "StoredEntity"), this.getWorld());
					if (loadEntity.isPresent()) {
						Entity spawnEntity = loadEntity.get();
						spawnEntity.refreshPositionAndAngles(this.getX(), this.getY() + 1.0D, this.getZ(), this.getYaw(), 0.0F);
						this.getWorld().spawnEntity(spawnEntity);
					}
					// Always reset monsterball
					NBTHelper.removeNbt(this.currentMonsterball, "StoredEntity");
				}
			} else if (hitResult.getType() == HitResult.Type.ENTITY) {
				EntityHitResult entityResult = (EntityHitResult) hitResult;
				if (entityResult != null) {
					Entity hitEntity = entityResult.getEntity();
					Identifier id = EntityType.getId(hitEntity.getType());
					if (!this.hasEntity && !(hitEntity instanceof PlayerEntity || hitEntity instanceof WitherEntity || hitEntity instanceof WitherSkullEntity || hitEntity instanceof EnderDragonEntity || hitEntity instanceof EnderDragonPart || monsterballConfig.getConfig().BLACKLIST.contains(id.toString()))) {
						boolean flag = hitEntity instanceof LivingEntity;
						if (flag) {
							NbtCompound ret = new NbtCompound();
							if (hitEntity.saveSelfNbt(ret)) {
								NbtCompound entity = hitEntity.writeNbt(ret);
								entity.putString("monsterball_name", hitEntity.getType().getTranslationKey());

								NBTHelper.putNbt(this.currentMonsterball, "StoredEntity", entity);

								this.currentMonsterball.setCount(1);
								if (hitEntity instanceof LivingEntity)
									hitEntity.discard();
								else hitEntity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
							}
						}
					}
				}
			}
				this.dropStack(this.currentMonsterball, 0.2F);
				this.getWorld().sendEntityStatus(this, (byte) 3);
				this.removeFromDimension();
		}
	}

	@Override
	public void handleStatus(byte id) {
		if (id == 3) {
			for (int i = 0; i < 8; ++i) {
				this.getWorld().addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, currentMonsterball)/*ParticleTypes.ITEM_SNOWBALL*/, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	protected Item getDefaultItem() {
		return monsterBallMod.MONSTERBALL;
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
