package net.knifick.praporupdate.init;

import software.bernie.geckolib.constant.dataticket.DataTicket;

public class PraporModTickets {
    public static final DataTicket<String> ENTITY_TEXTURE =
            DataTicket.create("entity_texture", String.class);
    public static final DataTicket<String> ENTITY_NAME =
            DataTicket.create("entity_name", String.class);
    public static final DataTicket<Boolean> ENTITY_TAME =
            DataTicket.create("entity_tame", Boolean.class);
    public static final DataTicket<Integer> ENTITY_COLOR =
            DataTicket.create("entity_color", Integer.class);
    public static final DataTicket<Boolean> ENTITY_SUCK =
            DataTicket.create("entity_suck", Boolean.class);
}
