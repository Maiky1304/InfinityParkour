package dev.maiky.infinityparkour.game;

import dev.maiky.infinityparkour.Main;
import dev.maiky.infinityparkour.game.data.PlayerData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Door: Maiky
 * Info: InfinityParkour - 04 Jun 2021
 * Package: dev.maiky.infiniteparkour.game
 */

public class GameManager {

	@Getter @Setter
	private Location startLocation;

	public GameManager() {
		if (Main.getInstance().getConfig().contains("parkour-start")) {
			this.startLocation = Main.getInstance().getConfig().getLocation("parkour-start");
		} else {
			this.startLocation = null;
		}
	}

	@Getter
	private LinkedHashMap<UUID, PlayerData> playerData = new LinkedHashMap<>();

	@Getter
	private LinkedHashMap<UUID, Game> games = new LinkedHashMap<>();

	public void createGame(Player player) {
		Game game = Game.with(player);
		playerData.put(player.getUniqueId(), new PlayerData(player));
		player.getInventory().clear();
		game.launch();
		this.games.put(player.getUniqueId(), game);
	}

}
