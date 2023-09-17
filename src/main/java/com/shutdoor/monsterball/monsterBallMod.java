package com.shutdoor.monsterball;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import com.shutdoor.monsterball.config.monsterballConfig;
import com.shutdoor.monsterball.entity.monsterballEntity;
import com.shutdoor.monsterball.item.monsterballItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class monsterBallMod implements ModInitializer {
	public static final String MODID = "monsterball";

	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final Item MONSTERBALL = new monsterballItem(new FabricItemSettings());
	public static void addItemsCreative(FabricItemGroupEntries ent){
		ent.add(MONSTERBALL);
	}

	public static final EntityType<monsterballEntity> MONSTERBALL_ENTITY = Registry.register(
			Registries.ENTITY_TYPE, new Identifier(MODID, "monsterball"),
			EntityType.Builder.<monsterballEntity>create(monsterballEntity::new, SpawnGroup.MISC).setDimensions(0.25F, 0.25F).trackingTickInterval(10).build("monsterball")
	);

	@Override
	public void onInitialize() {
		AutoConfig.register(monsterballConfig.class, JanksonConfigSerializer::new);
		Registry.register(Registries.ITEM, new Identifier(MODID, "monsterball"), MONSTERBALL);
		EntityRendererRegistry.register(MONSTERBALL_ENTITY, FlyingItemEntityRenderer::new);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(monsterBallMod::addItemsCreative);
	}

}
