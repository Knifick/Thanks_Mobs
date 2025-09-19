package net.knifick.praporupdate.client.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@EventBusSubscriber(value = Dist.CLIENT, modid = "prapor")
public class RingSuckEffectD {
    private static final ResourceLocation SPRITE_SHEET =
            ResourceLocation.fromNamespaceAndPath("prapor", "textures/mob_effect/suck_ring.png");

    // время жизни эффекта в ТИКАХ (10 секунд)
    private static final float MAX_LIFETIME_TICKS = 10f * 20f;

    // анимация атласа (кадры вертикально)
    private static final int FRAME_COUNT = 8;
    private static final int FRAME_TIME  = 1; // тиков на кадр

    private static final List<Wave> waves = new CopyOnWriteArrayList<>();

    public static void trigger(double x, double y, double z, double dx, double dy, double dz) {
        waves.add(new Wave(x, y, z, dx, dy, dz));
    }

    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent.AfterTranslucentBlocks event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        float pt = event.getPartialTick().getGameTimeDeltaPartialTick(true);
        float nowTicks = mc.level.getGameTime() + pt;

        Camera cam = event.getCamera();
        Vec3 camPos = cam.getPosition();
        PoseStack pose = event.getPoseStack();

        // буфер уровня (идём через RenderPipeline выбранного RenderType)
        LevelRenderer lr = event.getLevelRenderer();
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();

        for (Wave w : waves) {
            w.ageTicks = nowTicks - w.spawnTick;
            if (w.ageTicks > MAX_LIFETIME_TICKS) {
                waves.remove(w);
                continue;
            }
            renderAnimatedQuad(pose, buffers, camPos, w, pt);
        }
    }

    private static void renderAnimatedQuad(PoseStack poseStack,
                                           MultiBufferSource.BufferSource buffers,
                                           Vec3 cam,
                                           Wave wave,
                                           float partialTicks) {
        poseStack.pushPose();

        // текущий кадр по игровым тикам
        long gt = Minecraft.getInstance().level.getGameTime();
        int frame = (int)((gt / FRAME_TIME) % FRAME_COUNT);

        // прогресс [0..1]
        float t = Mth.clamp(wave.ageTicks / MAX_LIFETIME_TICKS, 0f, 1f);

        // направление к цели
        Vec3 dir = new Vec3(wave.dx - wave.x, wave.dy - wave.y, wave.dz - wave.z).normalize();

        // небольшой стартовый оффсет вперёд
        double offset = 0.3;
        double sx = wave.x + dir.x * offset;
        double sy = wave.y + dir.y * offset;
        double sz = wave.z + dir.z * offset;

        // позиция кольца
        double cx = Mth.lerp(t, sx, wave.dx);
        double cy = Mth.lerp(t, sy, wave.dy);
        double cz = Mth.lerp(t, sz, wave.dz);

        // в пространство уровня
        poseStack.translate(-cam.x, -cam.y, -cam.z);
        poseStack.translate(cx, cy + 0.7, cz);

        // ориентируем “квадрат-вверх” (ось +Y) на вектор dir
        Vec3 up = new Vec3(0, 1, 0);
        float dot = (float)Mth.clamp(up.dot(dir), -1.0, 1.0);
        float angle = (float)Math.acos(dot);
        Vec3 axis = up.cross(dir);
        Quaternionf q = axis.lengthSqr() < 1e-6
                ? new Quaternionf().rotationY(dot > 0 ? 0f : (float)Math.PI)
                : new Quaternionf().fromAxisAngleRad(axis.toVector3f(), angle);
        poseStack.mulPose(q);

        // рост размера
        float baseSize = 4.0f;
        float growTime = MAX_LIFETIME_TICKS * 0.6f;
        float size = wave.ageTicks < growTime
                ? Mth.lerp((wave.ageTicks + partialTicks) / growTime, 0f, baseSize)
                : baseSize;
        poseStack.scale(size, size, size);

        // альфа затухает к концу
        float alpha;
        float fadeStart = MAX_LIFETIME_TICKS * 0.5f;
        if (wave.ageTicks < fadeStart) {
            alpha = 1.0f;
        } else {
            float fade = (wave.ageTicks - fadeStart) / (MAX_LIFETIME_TICKS - fadeStart);
            alpha = Mth.clamp(1.0f - fade, 0f, 1f);
        }

        // выдаём вершины в пайплайн через готовый RenderType (emissive)
        var vc = buffers.getBuffer(RenderType.eyes(SPRITE_SHEET));
        Matrix4f m = poseStack.last().pose();

        float vUnit = 1f / FRAME_COUNT;
        float u0 = 0f, u1 = 1f;
        float v0 = frame * vUnit, v1 = v0 + vUnit;

        int overlay = OverlayTexture.NO_OVERLAY;
        int lightPacked   = LightTexture.FULL_BRIGHT; // эффект светится сам
        int lightU = lightPacked & 0xFFFF;
        int lightV = (lightPacked >>> 16) & 0xFFFF;

        float s = 0.5f; // половина стороны юнита (после scale — “реальный” размер)

        // QUAD в плоскости XZ (после поворота смотрит на цель)
        vc.addVertex(m, -s, 0, -s).setColor(1f, 1f, 1f, alpha).setUv(u0, v1).setOverlay(overlay).setUv2(lightU, lightV);
        vc.addVertex(m,  s, 0, -s).setColor(1f, 1f, 1f, alpha).setUv(u1, v1).setOverlay(overlay).setUv2(lightU, lightV);
        vc.addVertex(m,  s, 0,  s).setColor(1f, 1f, 1f, alpha).setUv(u1, v0).setOverlay(overlay).setUv2(lightU, lightV);
        vc.addVertex(m, -s, 0,  s).setColor(1f, 1f, 1f, alpha).setUv(u0, v0).setOverlay(overlay).setUv2(lightU, lightV);

        // флашим ТОЛЬКО наш тип, не ломая батчи других систем
        buffers.endBatch(RenderType.eyes(SPRITE_SHEET));

        poseStack.popPose();
    }

    private static class Wave {
        final double x, y, z;     // старт
        final double dx, dy, dz;  // цель
        final long   spawnTick;   // момент запуска (ticks)
        float ageTicks;           // возраст в тиках (float)

        Wave(double x, double y, double z, double dx, double dy, double dz) {
            this.x = x; this.y = y; this.z = z;
            this.dx = dx; this.dy = dy; this.dz = dz;
            this.spawnTick = Minecraft.getInstance().level.getGameTime();
        }
    }
}
