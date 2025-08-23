package net.knifick.praporupdate.toast;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MobToast implements Toast {
    private final Component mobName;
    private final ResourceLocation mobIcon; // текстура иконки моба
    private long firstDrawTime = -1L;
    private static final ResourceLocation TOAST_BG =
            ResourceLocation.fromNamespaceAndPath("prapor","textures/screens/toast.png");


    public MobToast(Component mobName, ResourceLocation mobIcon) {
        this.mobName = mobName;
        this.mobIcon = mobIcon;
    }

    @Override
    public Visibility render(GuiGraphics guiGraphics, ToastComponent toastGui, long delta) {
        if (firstDrawTime == -1L) firstDrawTime = delta;

        // фон
        guiGraphics.blit(TOAST_BG,
                0, 0,
                0, 0,
                160, 32,
                160, 32
        );

        // название моба
        var font = Minecraft.getInstance().font;

        // ограничиваем по ширине: 120 px (чтобы влезло в тост)
        var lines = font.split(mobName, 120);

        int y = 8; // верхняя позиция текста
        for (int i = 0; i < lines.size() && i < 2; i++) { // максимум 2 строки (иначе не влезет по высоте)
            guiGraphics.drawString(font, lines.get(i), 30, y, 0xFFFFFF, false);
            y += 10;
        }

        // показываем 5 секунд
        return delta - firstDrawTime >= 5000L ? Visibility.HIDE : Visibility.SHOW;
    }

}
