package net.knifick.praporupdate.client.renderer.item;

import net.knifick.praporupdate.client.model.Modelhat;
import net.knifick.praporupdate.init.PraporModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import java.util.Collections;
import java.util.Map;

@EventBusSubscriber(Dist.CLIENT)
public class HatArmor {
//    @SubscribeEvent
//    public static void registerItemExtensions(RegisterClientExtensionsEvent event) {
//        event.registerItem(new IClientItemExtensions() {
//            @Override
//            public HumanoidModel getHumanoidArmorModel(ItemStack itemStack, EquipmentClientInfo.LayerType layerType, Model original) {
//                return new HumanoidModel(new ModelPart(Collections.emptyList(),
//                        Map.of("head",
//                                new ModelPart(Collections.emptyList(),
//                                        Map.of("head", new Modelhat(Minecraft.getInstance().getEntityModels().bakeLayer(Modelhat.LAYER_LOCATION)).Hat, "hat", new ModelPart(Collections.emptyList(), Collections.emptyMap()))),
//                                "body", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "left_arm",
//                                new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "left_leg",
//                                new ModelPart(Collections.emptyList(), Collections.emptyMap()))));
//            }
//
//            @Override
//            public ResourceLocation getArmorTexture(ItemStack stack, EquipmentClientInfo.LayerType type, EquipmentClientInfo.Layer layer, ResourceLocation _default) {
//                return ResourceLocation.parse("prapor:textures/item/hat.png");
//            }
//        }, PraporModItems.HAT_HELMET.get());
//    }
}