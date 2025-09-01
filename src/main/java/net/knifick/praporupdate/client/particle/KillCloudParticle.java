package net.knifick.praporupdate.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KillCloudParticle extends TextureSheetParticle {
	public static KillParticleProvider provider(SpriteSet spriteSet) {
		return new KillParticleProvider(spriteSet);
	}

	public static class KillParticleProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public KillParticleProvider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn,
									   double x, double y, double z,
									   double xSpeed, double ySpeed, double zSpeed) {
			return new KillCloudParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

	private final SpriteSet sprites;
	private final float initialSize;

	protected KillCloudParticle(ClientLevel world, double x, double y, double z,
								double vx, double vy, double vz,
								SpriteSet spriteSet) {
		super(world, x, y, z, vx, vy, vz);
		this.sprites = spriteSet;

		// начальный размер ~как у smoke
		this.quadSize = 0.25F;
		this.initialSize = this.quadSize;

		// лайфтайм 80–120 тиков
		this.lifetime = 200 + this.random.nextInt(40);

		// базовый серый цвет
		float f = this.random.nextFloat() * 0.3F + 0.6F; // 0.6–0.9
		this.rCol = f;
		this.gCol = f;
		this.bCol = f;

		// очень маленькая гравитация (дым вверх)
		this.gravity = -0.0005F;
		this.hasPhysics = true;

		// стартовая скорость
		this.xd = vx * 0.1;
		this.yd = vy * 0.1 + 0.02;
		this.zd = vz * 0.1;

		this.setSpriteFromAge(spriteSet);
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.removed) {
			// размер растёт с возрастом
			float progress = (float)this.age / (float)this.lifetime;
			this.quadSize = this.initialSize * (1.0F + progress * 3.0F);

			// альфа постепенно исчезает
			this.alpha = 1.0F - progress;

			// смена спрайта по возрасту
			this.setSpriteFromAge(this.sprites);
		}
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
}
