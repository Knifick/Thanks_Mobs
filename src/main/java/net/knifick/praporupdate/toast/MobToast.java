package net.knifick.praporupdate.toast;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MobToast implements Toast {
    private final Component mobName;
    private final ResourceLocation mobIcon;
    private static final ResourceLocation TOAST_BG =
            ResourceLocation.fromNamespaceAndPath("prapor","textures/screens/toast.png");

    private long firstDrawTime = -1L;
    private Toast.Visibility visibility = Toast.Visibility.SHOW;

    public MobToast(Component mobName, ResourceLocation mobIcon) {
        this.mobName = mobName;
        this.mobIcon = mobIcon;
    }

    @Override
    public void render(GuiGraphics guiGraphics, Font font, long timeSinceLastFrame) {
        if (firstDrawTime == -1L) {
            firstDrawTime = timeSinceLastFrame;
        }

        // фон
        guiGraphics.blit(TOAST_BG,
                0, 0,
                0, 0,
                this.width(), this.height(),
                160, 32
        );

        // ограничиваем текст по ширине: 120 px
        var lines = font.split(mobName, 120);

        int y = 8;
        for (int i = 0; i < lines.size() && i < 2; i++) {
            guiGraphics.drawString(font, lines.get(i), 30, y, 0xFFFFFF, false);
            y += 10;
        }

        // иконка (если нужна, например 16х16)
        guiGraphics.blit(mobIcon, 8, 8, 0, 0, 16, 16, 16, 16);
    }

    @Override
    public void update(ToastManager manager, long currentTime) {
        if (firstDrawTime == -1L) {
            firstDrawTime = currentTime;
        }
        if (currentTime - firstDrawTime >= 5000L) { // 5 секунд
            visibility = Toast.Visibility.HIDE;
        }
    }

    @Override
    public Visibility getWantedVisibility() {
        return visibility;
    }
}
