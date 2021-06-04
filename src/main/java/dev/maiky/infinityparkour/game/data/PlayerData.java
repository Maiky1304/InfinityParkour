package dev.maiky.infinityparkour.game.data;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Door: Maiky
 * Info: InfinityParkour - 04 Jun 2021
 * Package: dev.maiky.infiniteparkour.game.data
 */

public class PlayerData {

	// Inventory
	@Getter
	private final ItemStack[] inventoryContents, armorContents;
	@Getter
	private final ItemStack offhand;

	// Location
	@Getter
	private final Location location;

	public PlayerData(Player player) {
		this.inventoryContents = player.getInventory().getContents();
		this.armorContents = player.getInventory().getArmorContents();
		this.offhand = player.getInventory().getItemInOffHand();

		this.location = player.getLocation();
	}
}
