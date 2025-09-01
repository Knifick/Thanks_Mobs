package net.knifick.praporupdate.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.knifick.praporupdate.entity.SoulEntity;
import net.knifick.praporupdate.init.PraporModEntities;
import net.knifick.praporupdate.network.PraporModVariables;
import net.knifick.praporupdate.util.misc.UIHelper;
import net.minecraft.ChatFormatting;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BookScreen extends Screen {
	private static final ResourceLocation BACKGROUND =
			ResourceLocation.fromNamespaceAndPath("prapor", "textures/screens/guide_bg.png");
	private static final ResourceLocation BG_FILL =
			ResourceLocation.fromNamespaceAndPath("prapor", "textures/screens/guide_fill.png");
	private static final ResourceLocation UNKNOWN =
			ResourceLocation.fromNamespaceAndPath("prapor", "textures/screens/guide_unknown.png");
	private static final ResourceLocation ARROWS =
			ResourceLocation.fromNamespaceAndPath("prapor", "textures/screens/guide_arrows.png");
	private static final List<ResourceLocation> PAGES = List.of(
			ResourceLocation.fromNamespaceAndPath("prapor", "textures/screens/p1.png"),
			ResourceLocation.fromNamespaceAndPath("prapor", "textures/screens/p2.png"),
			ResourceLocation.fromNamespaceAndPath("prapor", "textures/screens/p3.png")
	);

	// Описание всех мобов
	private static final List<MobEntry> MOB_ENTRIES = List.of(
			new MobEntry(
					"prapor",
					-40, -20, 30,
					() -> PraporModEntities.PRAPOR.get().create(Minecraft.getInstance().level),
					Component.translatable("entity.prapor.prapor"),
					List.of(Component.translatable("guide.prapor.prapor"),
							Component.translatable("guide.prapor.addprapor"))
			),
			new MobEntry(
					"pooker",
					30, -10, 20,
					() -> PraporModEntities.POOKER.get().create(Minecraft.getInstance().level),
					Component.translatable("entity.prapor.pooker"),
					List.of(Component.translatable("guide.prapor.pooker"),
							Component.translatable("guide.prapor.addpooker"))
			),
			new MobEntry(
					"soul",
					-40, -20, 70,
					() -> PraporModEntities.SOUL.get().create(Minecraft.getInstance().level),
					Component.translatable("entity.prapor.soul"),
					List.of(Component.translatable("guide.prapor.soul"))
			),
			new MobEntry(
					"bastard",
					30, -22, 30,
					() -> PraporModEntities.BASTARD.get().create(Minecraft.getInstance().level),
					Component.translatable("entity.prapor.bastard"),
					List.of(Component.translatable("guide.prapor.bastard"),
							Component.translatable("guide.prapor.addbastard"))
			),
			new MobEntry(
					"narrator",
					-30, -20, 40,
					() -> PraporModEntities.NARRATOR.get().create(Minecraft.getInstance().level),
					Component.translatable("entity.prapor.narrator"),
					List.of(Component.translatable("guide.prapor.narrator"),
							Component.translatable("guide.prapor.addnarrator"),
							Component.translatable("guide.prapor.thirdnarrator"))
			),
			new MobEntry(
					"brolem",
					30, -22, 20,
					() -> PraporModEntities.BROLEM.get().create(Minecraft.getInstance().level),
					Component.translatable("entity.prapor.brolem"),
					List.of(Component.translatable("guide.prapor.brolem"),
							Component.translatable("guide.prapor.addbrolem"))
			),
			new MobEntry(
					"bob",
					-30, -20, 40,
					() -> PraporModEntities.BOB.get().create(Minecraft.getInstance().level),
					Component.translatable("entity.prapor.bob"),
					List.of(Component.translatable("guide.prapor.bob"),
							Component.translatable("guide.prapor.abob").withStyle(ChatFormatting.ITALIC),
							Component.translatable("guide.prapor.bbob").withStyle(ChatFormatting.BOLD))
			),
			new MobEntry(
					"darkironkin",
					40, -18, 15,
					() -> PraporModEntities.DARKIRONKIN.get().create(Minecraft.getInstance().level),
					Component.translatable("entity.prapor.darkironkin"),
					List.of(Component.translatable("guide.prapor.darkironkin"),
							Component.translatable("guide.prapor.adddarkironkin"))
			),
			new MobEntry(
					"nymph",
					-20, -20, 30,
					() -> PraporModEntities.NYMPH.get().create(Minecraft.getInstance().level),
					Component.translatable("entity.prapor.nymph"),
					List.of(Component.translatable("guide.prapor.nymph"),
							Component.translatable("guide.prapor.addnymph"))
			),
			new MobEntry(
					"sucker",
					30, -22, 30,
					() -> PraporModEntities.SUCKER.get().create(Minecraft.getInstance().level),
					Component.translatable("entity.prapor.sucker"),
					List.of(Component.translatable("guide.prapor.sucker"))
			)
	);

	private static List<Integer> additionList = new ArrayList<>();
	static {
		for(int i = 0; i<MOB_ENTRIES.size(); i++){
			additionList.add(0);
		}
	}
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

		boolean hasWrites = vars.seenMobs.values().stream().anyMatch(v -> v != 0);

		// если игрок ещё ничего не открыл — делаем затемнение
		if (!hasWrites) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.1F);
		}

		final int pageInnerHalf = 60; // смещение от центра книги к центру страницы
		final int pageCenterY = this.height / 2; // вертикальный центр страниц

		// --- отрисовка закладок ---
		int startIndex = pageIndex * ENTRIES_PER_PAGE;
		for (int i = 0; i<ENTRIES_PER_PAGE; i++){
			for (int x = 0; x < PAGES.size(); x++) {
				int idx = startIndex + i;
				MobEntry entry = MOB_ENTRIES.get(idx);
				if(vars.seenMobs.get(entry.key)-1<x) break;
				int pageSide = i; // 0 = левая, 1 = правая
				int tabStartX = this.width / 2 - 20 + (pageSide == 0 ? -pageInnerHalf : pageInnerHalf+19); // центрируем по X
				int tabY = this.height / 2 - 110; // чуть выше верхней границы книги
				ResourceLocation tabTexture = PAGES.get(x);
                int idi = idx + x;
				if(additionList.get(idx) == x) {
					tabY -= 9;
				}
				boolean seen = vars.seenMobs.getOrDefault(entry.key, 0) != 0;
				if(seen) {
					guiGraphics.blit(tabTexture,
							tabStartX + x * 25, // сдвигаем каждую закладку
							tabY,
							0, 0,
							23, 30, // размер закладки в текстуре
							23, 30
					);
				}
			}
		}

		// фон книги
		guiGraphics.blit(BACKGROUND, this.width / 2 - 142, this.height / 2 - 90, 0, 0, 285, 180, 285, 180);

		for (int i = 0; i<ENTRIES_PER_PAGE; i++){
			int pageSide = i;
			int idx = startIndex + i;
			if(additionList.get(idx)==1 && pageSide==0){
				ResourceLocation BG_FILL =
						ResourceLocation.fromNamespaceAndPath("prapor", "textures/screens/guide_bg2.png");
				guiGraphics.blit(BG_FILL, this.width / 2 - 142, this.height / 2 - 90, 0, 0, 142, 180, 142, 180);
			}
			if(additionList.get(idx)==2 && pageSide==0){
				ResourceLocation BG_FILL =
						ResourceLocation.fromNamespaceAndPath("prapor", "textures/screens/guide_bg3.png");
				guiGraphics.blit(BG_FILL, this.width / 2 - 142, this.height / 2 - 90, 0, 0, 142, 180, 142, 180);
			}
			if(additionList.get(idx)==1 && pageSide==1){
				ResourceLocation BG_FILL =
						ResourceLocation.fromNamespaceAndPath("prapor", "textures/screens/guide_flip_bg2.png");
				guiGraphics.blit(BG_FILL, this.width / 2, this.height / 2 - 90, 0, 0, 143, 180, 143, 180);
			}
			if(additionList.get(idx)==2 && pageSide==1){
				ResourceLocation BG_FILL =
						ResourceLocation.fromNamespaceAndPath("prapor", "textures/screens/guide_flip_bg3.png");
				guiGraphics.blit(BG_FILL, this.width / 2, this.height / 2 - 90, 0, 0, 142, 180, 142, 180);
			}
		}

		float follow = 0.005f; // сила поворота моба при движении мышки

		for (int i = 0; i < ENTRIES_PER_PAGE; i++) {
			int idx = startIndex + i;
			if (idx >= MOB_ENTRIES.size()) break;

			MobEntry entry = MOB_ENTRIES.get(idx);

			// вычисляем центр левой/правой страницы
			int pageSide = i; // 0 = левая, 1 = правая
			int pageCenterX = this.width / 2 + (pageSide == 0 ? -pageInnerHalf : pageInnerHalf);

			int drawX = pageCenterX + entry.offsetX;
			int drawY = pageCenterY + entry.offsetY;

			boolean seen = vars.seenMobs.getOrDefault(entry.key, 0) != 0;

			// если игрок ещё не видел моба — ставим "?"
			if (!seen) {
				guiGraphics.blit(UNKNOWN,
						pageSide == 0 ? width / 2 - 80 : width / 2 + 60,
						height / 2 - 20,
						0, 0, 19, 31, 19, 31);
			} else {
				LivingEntity entity = entry.entitySupplier.get();
				if (entity != null) {
					float angleX = Mth.clamp((drawX - mouseX) * follow, -2, 2);
					float angleY = Mth.clamp((drawY - mouseY) * follow, -0.5f, 0.5f);

					// для SoulEntity небольшая поправка угла
					if (entity instanceof SoulEntity) angleX -= 4.5f;

					renderEntityInInventoryFollowsAngle(guiGraphics, drawX, drawY,
							entry.scale, angleX, angleY, entity);
				}
			}

			// текстовое описание
			int mobStage = vars.seenMobs.getOrDefault(entry.key, 0);
			if (mobStage > 0) {
				int addIndex = additionList.get(idx);
				if (addIndex >= entry.descriptions.size()) {
					addIndex = entry.descriptions.size() - 1; // fallback на последний элемент
				}
				Component mainDesc = entry.descriptions.get(addIndex);

				int textX = (pageSide == 0) ? (this.width / 2 - 120) : (this.width / 2 + 10);
				int textY = this.height / 2 - 70;

				renderDescriptionsWithPriority(guiGraphics, entry.title, mainDesc,
						mobStage, textX, textY, 110, 150, pageSide);
			}
		}

		// стрелки переключения страниц
		Font font = Minecraft.getInstance().font;
		int arrowY = this.height / 2 + 70;
		int leftArrowX = this.width / 2 - 115;
		int rightArrowX = this.width / 2 + 100;

		int totalPages = (MOB_ENTRIES.size() + ENTRIES_PER_PAGE - 1) / ENTRIES_PER_PAGE;

		boolean hoverLeft = mouseX >= leftArrowX && mouseX <= leftArrowX + 17 &&
				mouseY >= arrowY && mouseY <= arrowY + 13;
		boolean hoverRight = mouseX >= rightArrowX && mouseX <= rightArrowX + 23 &&
				mouseY >= arrowY && mouseY <= arrowY + 13;

		guiGraphics.blit(ARROWS, leftArrowX, arrowY, hoverLeft ? 18 : 0, 13, 18, 10, 36, 23);
		guiGraphics.blit(ARROWS, rightArrowX, arrowY, hoverRight ? 18 : 0, 0, 18, 10, 36, 23);

		// индикатор страницы
		String pageIndicator = String.format("%d / %d", pageIndex + 1, Math.max(totalPages, 1));
		guiGraphics.drawString(font, Component.literal(pageIndicator),
				this.width / 2 - font.width(Component.literal(pageIndicator)) / 2,
				arrowY + 25, 0xFFFFFF, false);

		// сообщение, если игрок ничего не открыл
		if (!hasWrites) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1F);
			RenderSystem.disableBlend();
			guiGraphics.drawString(font, Component.translatable("guide.prapor.guide1"), width / 2 - 89, height / 2 - 50, 0xFFFFFF, false);
			guiGraphics.drawString(font, Component.translatable("guide.prapor.guide2"), width / 2 - 80, height / 2 - 40, 0xFFFFFF, false);
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
		int pageY = this.height / 2 - 110;
		int pageX = this.width / 2 - 79;

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

		PraporModVariables.PlayerVariables vars =
				Minecraft.getInstance().player.getData(PraporModVariables.PLAYER_VARIABLES);
		int startIndex = pageIndex * ENTRIES_PER_PAGE;
		for (int i = 0; i<ENTRIES_PER_PAGE; i++){
			for (int x = 0; x < PAGES.size(); x++) {
				int pageSide = i; // 0 = левая, 1 = правая
				int idx = startIndex + i;
				if (idx >= MOB_ENTRIES.size()) break;
				MobEntry entry = MOB_ENTRIES.get(idx);
				if(vars.seenMobs.get(entry.key)-1<x) break;
				int idi = idx + x;
				int offsetX = x*25;
				if(pageSide==1) offsetX+=139;
				if(mouseX>=pageX+offsetX&&mouseX<=pageX+21+offsetX&&
						mouseY>=pageY && mouseY<=pageY+23){
					if(additionList.get(idx)==x) continue;
					additionList.set(idx, x);
					Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK.value());
				}
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
	 * (Старый метод оставлен для совместимости — теперь используется renderDescriptionsWithPriority)
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

	/**
	 * Новый метод: рендерит:
	 * - заголовок (title)
	 * - если mobStage == 1: просто mainDescription (как раньше)
	 * - если mobStage > 1: mainDescription уменьшенным/приглушённым, а под ним priorityDescription большим/обычным цветом
	 */
	private void renderDescriptionsWithPriority(GuiGraphics graphics, Component title, Component mainDescription,
												int mobStage, int x, int y, int maxWidth, int maxHeight, int page) {
		Font font = Minecraft.getInstance().font;

		int nameOffset = 0;
		if (page == 0) nameOffset = 40;
		graphics.drawString(font, title, x + 5 + nameOffset, y, 0x404040, false);

		List<FormattedCharSequence> lines = font.split(mainDescription, maxWidth);
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
			List<Component> descriptions
	) {}
}
