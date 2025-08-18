package net.knifick.praporupdate.client.effects;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
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

@EventBusSubscriber(
        value = Dist.CLIENT,
        bus = EventBusSubscriber.Bus.GAME,
        modid = "prapor"
)
public class RingSuckEffectD {
    private static final ResourceLocation SPRITE_SHEET =
            ResourceLocation.fromNamespaceAndPath("prapor", "textures/mob_effect/suck_ring.png");

    // Настройки волны
    private static final float MAX_LIFETIME = 10.0f; // в секундах

    // Список активных волн
    private static final List<Wave> waves = new CopyOnWriteArrayList<>();
    public static void trigger(double x, double y, double z, double dx, double dy, double dz) {
        double modifier = 0.05;
        waves.add(new Wave(x, y, z, dx, dy, dz));
    }

    // настройки анимации
    private static final int FRAME_COUNT = 8;     // всего кадров
    private static final int FRAME_TIME = 1;      // сколько тиков показывать 1 кадр

    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)
            return;

        float partialTicks = event.getPartialTick().getGameTimeDeltaPartialTick(true);
        PoseStack pose = event.getPoseStack();
        Camera camera = event.getCamera();
        Vec3 camPos  = camera.getPosition();

        // Обновляем и рендерим все волны
        for (Wave wave : waves) {
            float gameTime = Minecraft.getInstance().level.getGameTime() + event.getPartialTick().getGameTimeDeltaPartialTick(true);
            wave.age = (gameTime - wave.spawnTick);
            if (wave.age > MAX_LIFETIME) {
                waves.remove(wave);
            } else {
                renderAnimatedQuad(pose, camPos, wave, partialTicks);
            }
        }
    }

    private static void renderAnimatedQuad(PoseStack poseStack, Vec3 cam, Wave wave, float partialTicks) {
        poseStack.pushPose();

        // === вычисляем текущий кадр анимации ===
        long gameTime = Minecraft.getInstance().level.getGameTime();
        int frame = (int)((gameTime / FRAME_TIME) % FRAME_COUNT);

        // === считаем плавное смещение к цели ===
        float t = (wave.age + partialTicks) / MAX_LIFETIME;
        if (t > 1.0f) t = 1.0f;

        // направление движения
        Vec3 dir = new Vec3(wave.dx - wave.x, wave.dy - wave.y, wave.dz - wave.z).normalize();

        // смещение старта вперёд (например, 0.3 блока)
        double offset = 0.3;
        double startX = wave.x + dir.x * offset;
        double startY = wave.y + dir.y * offset;
        double startZ = wave.z + dir.z * offset;

        // итоговая позиция
        double cx = Mth.lerp(t, startX, wave.dx);
        double cy = Mth.lerp(t, startY, wave.dy);
        double cz = Mth.lerp(t, startZ, wave.dz);


        // Ставим в мир
        poseStack.translate(-cam.x, -cam.y, -cam.z);
        poseStack.translate(cx, cy+0.7, cz);

        // === ориентация в сторону цели ===
        //Vec3 dir = new Vec3(wave.dx - wave.x, wave.dy - wave.y, wave.dz - wave.z).normalize();

        // forward-вектор квадрата: сейчас он смотрит вдоль +Y (так как мы его "поднимем")
        Vec3 forward = new Vec3(0, 1, 0);

        // теперь выровнять его лицом к цели
        float dot = (float) forward.dot(dir);
        dot = Mth.clamp(dot, -1.0f, 1.0f);
        float angle = (float) Math.acos(dot);

        Vec3 axis = forward.cross(dir).normalize();
        Quaternionf q;
        if (axis.lengthSqr() < 1e-6) {
            q = new Quaternionf().rotationY(dot > 0 ? 0f : (float) Math.PI);
        } else {
            q = new Quaternionf().fromAxisAngleRad(axis.toVector3f(), angle);
        }
        poseStack.mulPose(q);

        // === масштаб ===
        float baseSize = 4.0f;      // финальный размер
        float minSize  = 0f;      // стартовый размер
        float growTime = MAX_LIFETIME * 0.6f; // за сколько времени вырастет

        float size;
        if (wave.age < growTime) {
            float progress = (wave.age + partialTicks) / growTime;
            size = Mth.lerp(progress, minSize, baseSize);
        } else {
            size = baseSize;
        }
        poseStack.scale(size, size, size);

        // === альфа-затухание ===
        float alpha;
        float fadeStart = MAX_LIFETIME * 0.5f;
        if (wave.age < fadeStart) {
            alpha = 1.0f;
        } else {
            float fadeProgress = (wave.age - fadeStart) / (MAX_LIFETIME - fadeStart);
            alpha = 1.0f - fadeProgress;
            if (alpha < 0f) alpha = 0f;
        }

        // Настройки рендера
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, SPRITE_SHEET);
        RenderSystem.applyModelViewMatrix();

        // кадры идут ВЕРТИКАЛЬНО
        float vSize = 1.0f / FRAME_COUNT;
        float u0 = 0f;
        float u1 = 1f;
        float v0 = frame * vSize;
        float v1 = v0 + vSize;

        // квадрат
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        Matrix4f m = poseStack.last().pose();
        float s = 0.5f;

        buf.addVertex(m, -s, 0, -s).setUv(u0, v1).setColor(1f, 1f, 1f, alpha);
        buf.addVertex(m,  s, 0, -s).setUv(u1, v1).setColor(1f, 1f, 1f, alpha);
        buf.addVertex(m,  s, 0,  s).setUv(u1, v0).setColor(1f, 1f, 1f, alpha);
        buf.addVertex(m, -s, 0,  s).setUv(u0, v0).setColor(1f, 1f, 1f, alpha);

        BufferUploader.drawWithShader(buf.buildOrThrow());

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }


    //    // Внутренний класс для описания волны
    private static class Wave {
        final double x, y, z;   // старт
        final double dx, dy, dz; // цель
        float age;
        long spawnTick;
        final float travelTime = 20f; // сколько тиков летит волна (напр. 1 сек = 20)

        Wave(double x, double y, double z, double dx, double dy, double dz) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
            this.spawnTick = Minecraft.getInstance().level.getGameTime();
        }
    }
}
