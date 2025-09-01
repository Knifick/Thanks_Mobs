package net.knifick.praporupdate.util.misc;

public class UIHelper {
    public static int rgbaToColor(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                (b & 0xFF);
    }

    public static int hexToColor(String hex) {
        // Убираем # если есть
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        // Парсим RRGGBB
        int rgb = Integer.parseInt(hex, 16);
        // Добавляем альфу = 255
        return 0xFF000000 | rgb;
    }

}
