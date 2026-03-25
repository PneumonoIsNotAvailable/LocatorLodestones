package net.pneumono.locator_lodestones;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.waypoints.WaypointStyleAsset;
import net.minecraft.world.waypoints.WaypointStyleAssets;
import net.pneumono.locator_lodestones.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocatorLodestones implements ClientModInitializer {
	public static final String MOD_ID = "locator_lodestones";

	public static final Logger LOGGER = LoggerFactory.getLogger("Locator Lodestones");

	public static final ResourceKey<WaypointStyleAsset> LODESTONE_STYLE = style("lodestone");
	public static final ResourceKey<WaypointStyleAsset> DEATH_STYLE = style("death");

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing Locator Lodestones");
		ConfigManager.initConfig();
		ClientTickEvents.END_CLIENT_TICK.register(client -> WaypointTracking.updateWaypoints(client.player));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> WaypointTracking.resetWaypoints());
	}

	private static ResourceKey<WaypointStyleAsset> style(String path) {
		return ResourceKey.create(WaypointStyleAssets.ROOT_ID, id(path));
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}