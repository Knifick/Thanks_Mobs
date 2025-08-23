package net.knifick.praporupdate.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.knifick.praporupdate.entity.SoulEntity;
import net.knifick.praporupdate.init.PraporModEntities;
import net.knifick.praporupdate.network.PraporModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BookScreen extends Screen {
	private static final ResourceLocation BACKGROUND =
			ResourceLocation.fromNamespaceAndPath("prapor", "textures/screens/guide_bg.png");
	private static final ResourceLocation UNKNOWN =
			ResourceLocation.fromNamespaceAndPath("prapor", "textures/screens/guide_unknown.png");
	private static final ResourceLocation ARROWS =
			ResourceLocation.fromNamespaceAndPath("prapor", "textures/screens/guide_arrows.png");

	// Описание всех мобов
	private static final List<MobEntry> MOB_ENTRIES = List.of(
			new MobEntry(
					"prapor",
					-40, -20, 30,
					() -> PraporModEntities.PRAPOR.get().create(Minecraft.getInstance().level),
					Component.translatable("entity.prapor.prapor"),
					Component.translatable("guide.prapor.prapor")
			),
			new MobEntry(
					"pooker",
					30, -10, 20,
					() -> PraporModEntities.POOKER.get().create(Minecraft.getInstance().level),
					Component.translatable("entity.prapor.pooker"),
					Component.translatable("guide.prapor.pooker")
			),
			new MobEntry(
					"soul",
					-40, -20, 70,
					() -> PraporModEntities.SOUL.get().create(Minecraft.getInstance().level),
					Component.translatable("entity.prapor.soul"),
					Component.translatable("guide.prapor.soul")
			),
			new MobEntry(
					"bastard",
					30, -22, 30,
					() -> PraporModEntities.BASTARD.get().create(Minecraft.getInstance().level),
					Component.translatable("entity.prapor.bastard"),
					Component.translatable("guide.prapor.bastard")
			),
			new MobEntry(
					"narrator",
					-30, -20, 40,
					() -> PraporModEntities.NARRATOR.get().create(Minecraft.getInstance().level),
					Component.translatable("entity.prapor.narrator"),
					Component.translatable("guide.prapor.narrator")
			),
			new MobEntry(
					"brolem",
					30, -22, 20,
					() -> PraporModEntities.BROLEM.get().create(Minecraft.getInstance().level),
					Component.translatable("entity.prapor.brolem"),
					Component.translatable("guide.prapor.brolem")
			),
			new MobEntry(
					"bob",
					-30, -20, 40,
					() -> PraporModEntities.BOB.get().create(Minecraft.getInstance().level),
					Component.translatable("entity.prapor.bob"),
					Component.translatable("guide.prapor.bob")
			),
			new MobEntry(
					"darkironkin",
					40, -18, 15,
					() -> PraporModEntities.DARKIRONKIN.get().create(Minecraft.getInstance().level),
					Component.translatable("entity.prapor.darkironkin"),
					Component.translatable("guide.prapor.darkironkin")
			),
			new MobEntry(
					"nymph",
					-20, -20, 30,
					() -> PraporModEntities.NYMPH.get().create(Minecraft.getInstance().level),
					Component.translatable("entity.prapor.nymph"),
					Component.translatable("guide.prapor.nymph")
			),
			new MobEntry(
					"sucker",
					30, -22, 30,
					() -> PraporModEntities.SUCKER.get().create(Minecraft.getInstance().level),
					Component.translatable("entity.prapor.sucker"),
					Component.translatable("guide.prapor.sucker")
			)
	);

	// Пагинация: по 2 записи на разворот (левая + правая страницы)
	private int pageIndex = 0;
	private static final int ENTRIES_PER_PAGE = 2;

	public BookScreen(Component title) {
		super(title);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		PraporModVariables.PlayerVariables vars =
				Minecraft.getInstance().player.getData(PraporModVariables.PLAYER_VARIABLES);
		boolean hasWrites = false;
		for (Map.Entry<String, Integer> entry : vars.seenMobs.entrySet()){
			if(entry.getValue()==1){
				hasWrites = true;
				break;
			}
		}
		if (!hasWrites) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.1F);
		}

		guiGraphics.blit(BACKGROUND, this.width / 2 - 142, this.height / 2 - 90, 0, 0, 285, 180, 285, 180);
		Level level = Minecraft.getInstance().level;
		float follow = 0.005f;

		// вычисляем границы/центры страниц внутри фона
		// Условно: каждая внутренняя страница занимает ~120px по ширине (итого 240)
		final int pageInnerHalf = 60; // смещение от центра книги к центру страницы
		final int pageCenterY = this.height / 2; // вертикальный центр страниц

		// отрисовываем записи на текущей странице (по 2 элемента: 0 - левая, 1 - правая)
		int startIndex = pageIndex * ENTRIES_PER_PAGE;
		for (int i = 0; i < ENTRIES_PER_PAGE; i++) {
			int idx = startIndex + i;
			if (idx >= MOB_ENTRIES.size()) break;

			MobEntry entry = MOB_ENTRIES.get(idx);

			// определяем центр страницы для элемента: левая или правая
			int pageSide = i; // 0 - левая, 1 - правая
			int pageCenterX = this.width / 2 + (pageSide == 0 ? -pageInnerHalf : pageInnerHalf);

			// учитываем смещение из записи (offsetX и offsetY) относительно центра страницы
			int drawX = pageCenterX + entry.offsetX;
			int drawY = pageCenterY + entry.offsetY;

			boolean seen = vars.seenMobs.getOrDefault(entry.key, 0) != 0;

			// моб или вопросик
			if (!seen) {
				if (pageSide==0)
					guiGraphics.blit(UNKNOWN, width/2-80, height/2-20, 0, 0, 19, 31, 19, 31);
				else
					guiGraphics.blit(UNKNOWN, width/2+60, height/2-20, 0, 0, 19, 31, 19, 31);
			} else {
				LivingEntity entity = entry.entitySupplier.get();
				if (entity != null) {
					if(entity instanceof SoulEntity){
						renderEntityInInventoryFollowsAngle(guiGraphics,
								drawX, drawY, entry.scale,
								Mth.clamp((drawX - mouseX) * follow, -2, 2)-4.5f,
								Mth.clamp((drawY - mouseY) * follow, -0.5f, 0.5f),
								entity);
					}
					else {
						renderEntityInInventoryFollowsAngle(guiGraphics,
								drawX, drawY, entry.scale,
								Mth.clamp((drawX - mouseX) * follow, -2, 2),
								Mth.clamp((drawY - mouseY) * follow, -0.5f, 0.5f),
								entity);
					}
				}
			}

			// текст (на соответствующей странице)
			if (seen) {
				// для текста используем меньшую ширину на страницу (чтобы разделить лево/право)
				int textX = (pageSide == 0) ? (this.width / 2 - 120) : (this.width / 2 + 10);
				int textY = this.height / 2 - 70;
				int textWidth = 110;
				int textHeight = 150;
				renderWrappedText(guiGraphics, entry.title, entry.description,
						textX, textY,
						textWidth, textHeight, pageSide);
			}
		}

		// отрисуем простые стрелки навигации и индикатор страницы
		Font font = Minecraft.getInstance().font;
		int arrowY = this.height / 2 + 70;
		int leftArrowX = this.width / 2 - 115;
		int rightArrowX = this.width / 2 + 100;

		int totalPages = (MOB_ENTRIES.size() + ENTRIES_PER_PAGE - 1) / ENTRIES_PER_PAGE;
		// стрелки (серым если недоступны)
		int arrowColor = 0x404040;
		int disabledColor = 0xB0B0B0;
        // вычисляем состояния
		boolean canGoLeft = pageIndex > 0;
		boolean canGoRight = pageIndex < totalPages - 1;

		// наведение мыши
		boolean hoverLeft = mouseX >= leftArrowX && mouseX <= leftArrowX + 17 &&
				mouseY >= arrowY && mouseY <= arrowY + 13;
		boolean hoverRight = mouseX >= rightArrowX && mouseX <= rightArrowX + 23 &&
				mouseY >= arrowY && mouseY <= arrowY + 13;

		guiGraphics.blit(ARROWS,
				leftArrowX, arrowY,
				hoverLeft ? 18 : 0, 13,
				18, 10,
				36, 23
		);
		guiGraphics.blit(ARROWS,
				rightArrowX, arrowY,
				hoverRight ? 18 : 0, 0,
				18, 10,
				36, 23
		);
		// индикатор страницы
		String pageIndicator = String.format("%d / %d", pageIndex + 1, Math.max(totalPages, 1));
		guiGraphics.drawString(font, Component.literal(pageIndicator), this.width / 2 - font.width(Component.literal(pageIndicator)) / 2, arrowY+25, 0xFFFFFF, false);
		if(!hasWrites){
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1F);
			RenderSystem.disableBlend();
			guiGraphics.drawString(font, Component.translatable("guide.prapor.guide1"), width/2-89, height/2-50, 0xFFFFFF, false);
			guiGraphics.drawString(font, Component.translatable("guide.prapor.guide2"), width/2-80, height/2-40, 0xFFFFFF, false);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	/**
	 * Обрабатываем клики по стрелкам для переключения страниц
	 */
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		int arrowY = this.height / 2 + 70;
		int leftArrowX = this.width / 2 - 120;
		int rightArrowX = this.width / 2 + 100;

		int totalPages = (MOB_ENTRIES.size() + ENTRIES_PER_PAGE - 1) / ENTRIES_PER_PAGE;

		// левая кнопка (23x13)
		if (mouseX >= leftArrowX && mouseX <= leftArrowX + 23 &&
				mouseY >= arrowY && mouseY <= arrowY + 13) {
			if (pageIndex > 0) {
				Minecraft.getInstance().player.playSound(SoundEvents.VILLAGER_WORK_LIBRARIAN);
				pageIndex--;
				return true;
			}
		}

		// правая кнопка (23x13)
		if (mouseX >= rightArrowX && mouseX <= rightArrowX + 23 &&
				mouseY >= arrowY && mouseY <= arrowY + 13) {
			if (pageIndex < totalPages - 1) {
				Minecraft.getInstance().player.playSound(SoundEvents.VILLAGER_WORK_LIBRARIAN);
				pageIndex++;
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	private void renderEntityInInventoryFollowsAngle(GuiGraphics guiGraphics, int x, int y, int scale,
													 float angleXComponent, float angleYComponent, LivingEntity entity) {
		Quaternionf pose = new Quaternionf().rotateZ((float) Math.PI);
		Quaternionf cameraOrientation = new Quaternionf().rotateX(angleYComponent * 20 * ((float) Math.PI / 180F));
		pose.mul(cameraOrientation);

		float prevBodyRot = entity.yBodyRot;
		float prevYRot = entity.getYRot();
		float prevXRot = entity.getXRot();
		float prevHeadRotO = entity.yHeadRotO;
		float prevHeadRot = entity.yHeadRot;

		entity.yBodyRot = 180.0F + angleXComponent * 20.0F;
		entity.setYRot(180.0F + angleXComponent * 40.0F);
		entity.setXRot(-angleYComponent * 20.0F);
		entity.yHeadRot = entity.getYRot();
		entity.yHeadRotO = entity.getYRot();

		InventoryScreen.renderEntityInInventory(guiGraphics, x, y, scale, new Vector3f(0, 0, 0), pose, cameraOrientation, entity);

		entity.yBodyRot = prevBodyRot;
		entity.setYRot(prevYRot);
		entity.setXRot(prevXRot);
		entity.yHeadRotO = prevHeadRotO;
		entity.yHeadRot = prevHeadRot;
	}

	/**
	 * Отрисовывает заголовок и описание, оборачивая текст по ширине
	 */
	private void renderWrappedText(GuiGraphics graphics, Component title, Component text,
								   int x, int y, int maxWidth, int maxHeight, int page) {
		Font font = Minecraft.getInstance().font;

		int nameOffset = 0;
		// Заголовок
		if(page == 0) nameOffset = 40;
		graphics.drawString(font, title, x+5+nameOffset, y, 0x404040, false);

		// Переносим текст по ширине
		List<FormattedCharSequence> lines = font.split(text, maxWidth);
		int lineY = y + 55;
		for (int i = 0; i < lines.size() && lineY < y + maxHeight; i++) {
			graphics.drawString(font, lines.get(i), x, lineY, 0x202020, false);
			lineY += 10;
		}
	}

	private record MobEntry(
			String key,
			int offsetX, int offsetY, int scale,
			Supplier<LivingEntity> entitySupplier,
			Component title,
			Component description
	) {}
}
