//package net.knifick.praporupdate.client.effects;
//
//import com.mojang.blaze3d.platform.GlStateManager;
//import com.mojang.blaze3d.systems.RenderSystem;
//import com.mojang.blaze3d.vertex.*;
//import net.minecraft.client.Camera;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.GameRenderer;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.util.Mth;
//import net.minecraft.world.phys.Vec3;
//import net.neoforged.api.distmarker.Dist;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
//import org.joml.Matrix4f;
//
//import java.util.List;
//import java.util.concurrent.CopyOnWriteArrayList;
//
//@EventBusSubscriber(
//        value = Dist.CLIENT,
//        bus = EventBusSubscriber.Bus.GAME,
//        modid = "prapor"
//)
//public class RingSuckEffect {
//    private static final ResourceLocation SPLASH_TEXTURE =
//            ResourceLocation.fromNamespaceAndPath("prapor", "textures/mob_effect/suck_ring.png");
//
//    // Настройки волны
//    private static final float MAX_LIFETIME = 30.0f; // в секундах
//    private static final float INITIAL_SIZE = 0.5f;
//    private static final float GROWTH_RATE = 0.3f; // размер в сек
//
//    // Список активных волн
//    private static final List<Wave> waves = new CopyOnWriteArrayList<>();
//
//    public static void trigger(double x, double y, double z, double dx, double dy, double dz) {
//        double modifier = 0.05;
//        waves.add(new Wave(x, y, z, dx, dy, dz));
//    }
//
//    @SubscribeEvent
//    public static void onRender(RenderLevelStageEvent event) {
//        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)
//            return;
//
//        float partialTicks = event.getPartialTick().getGameTimeDeltaPartialTick(true);
//        PoseStack pose = event.getPoseStack();
//        Camera camera = event.getCamera();
//        Vec3 camPos  = camera.getPosition();
//
//        // Обновляем и рендерим все волны
//        for (Wave wave : waves) {
//            float gameTime = Minecraft.getInstance().level.getGameTime() + event.getPartialTick().getGameTimeDeltaPartialTick(true);
//            wave.age = (gameTime - wave.spawnTick);
//            System.out.println(wave);
//            if (wave.age > MAX_LIFETIME) {
//                waves.remove(wave);
//            } else {
//                renderWave(pose, camPos, wave);
//            }
//        }
//    }
//
//    private static void renderWave(PoseStack poseStack, Vec3 cam, Wave wave) {
//        float halfLife = MAX_LIFETIME / 2f;
//        float alpha = 1f - Math.abs((wave.age / halfLife) - 1f); // плавная альфа
//        alpha = Mth.clamp(alpha, 0f, 1f);
//
//        float size = INITIAL_SIZE + GROWTH_RATE * wave.age;
//
//        // Пропорция от начальной до конечной позиции
//        double t = Mth.clamp(wave.age / MAX_LIFETIME, 0.0f, 1.0f);
//        double currentX = wave.x + (wave.dx - wave.x) * t;
//        double currentY = wave.y + (wave.dy - wave.y) * t;
//        double currentZ = wave.z + (wave.dz - wave.z) * t;
//
//        poseStack.pushPose();
//        poseStack.translate(-cam.x, -cam.y, -cam.z);
//        poseStack.translate(currentX, currentY, currentZ);
//        poseStack.scale(size, size, size);
//
//        // Включаем прозрачность
//        RenderSystem.enableBlend();
//        RenderSystem.blendFuncSeparate(
//                GlStateManager.SourceFactor.SRC_ALPHA,
//                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
//                GlStateManager.SourceFactor.ONE,
//                GlStateManager.DestFactor.ZERO
//        );
//        RenderSystem.disableCull();
//        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
//        RenderSystem.setShaderTexture(0, SPLASH_TEXTURE);
//        RenderSystem.applyModelViewMatrix();
//
//        Tesselator tess = Tesselator.getInstance();
//        BufferBuilder buf = tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
//        Matrix4f m = poseStack.last().pose();
//        float s = 0.5f;
//
//        buf.addVertex(m, -s, 0, -s).setUv(0f, 1f).setColor(1f, 1f, 1f, alpha);
//        buf.addVertex(m,  s, 0, -s).setUv(1f, 1f).setColor(1f, 1f, 1f, alpha);
//        buf.addVertex(m,  s, 0,  s).setUv(1f, 0f).setColor(1f, 1f, 1f, alpha);
//        buf.addVertex(m, -s, 0,  s).setUv(0f, 0f).setColor(1f, 1f, 1f, alpha);
//
//        BufferUploader.drawWithShader(buf.buildOrThrow());
//
//        RenderSystem.enableCull();
//        RenderSystem.disableBlend();
//        poseStack.popPose();
//    }
//
//    // Внутренний класс для описания волны
//    private static class Wave {
//        final double x, y, z, dx, dy, dz;
//        float age;
//        long spawnTick;
//
//        Wave(double x, double y, double z, double dx, double dy, double dz) {
//            this.x = x;
//            this.y = y;
//            this.z = z;
//            this.dx = dx;
//            this.dy = dy;
//            this.dz = dz;
//            this.spawnTick = Minecraft.getInstance().level.getGameTime();
//        }
//    }
//}