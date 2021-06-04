package dev.maiky.infinityparkour;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import dev.maiky.infinityparkour.commands.ParkourCommand;
import dev.maiky.infinityparkour.game.GameManager;
import lombok.Getter;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.plugin.ap.Plugin;

@Plugin(name = "InfinityParkour", authors = {"Maiky Perlee"},
apiVersion = "1.16")
public final class Main extends ExtendedJavaPlugin {

	@Getter
	private static Main instance;

	@Getter
	private GameManager gameManager;

	@Override
	protected void enable() {
		// Instance
		instance = this;

		// Manager
		this.gameManager = new GameManager();

		// Commands
		this.registerCommands();

		// Events
		this.registerEvents();
	}

	private void registerCommands() {
		BukkitCommandManager commandManager = new BukkitCommandManager(this);

		commandManager.getCommandConditions().addCondition("inGame", context -> {
			if (!context.getIssuer().isPlayer()) return;

			if (!this.gameManager.getGames().containsKey(context.getIssuer().getPlayer().getUniqueId()))
				throw new ConditionFailedException("You are not in a game.");
		});

		commandManager.registerCommand(new ParkourCommand());
	}

	private void registerEvents() {

	}

}
