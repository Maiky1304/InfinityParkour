package dev.maiky.infinityparkour.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.infinityparkour.Main;
import dev.maiky.infinityparkour.game.Game;
import dev.maiky.infinityparkour.gui.ParkourGUI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Door: Maiky
 * Info: InfinityParkour - 04 Jun 2021
 * Package: dev.maiky.infiniteparkour.commands
 */

@CommandAlias("parkour")
@CommandPermission("parkour.use")
@Description("Play the infinite parkour!")
public class ParkourCommand extends BaseCommand {

	@Subcommand("help")
	@Description("Shows all the subcommands")
	@HelpCommand
	public void onHelp(Player player) {
		player.sendMessage("§6/" + this.getExecCommandLabel() + " <subcommand> <arg>...");
		for (String subCommand : getSubCommands().keys()) {
			RegisteredCommand rc = getSubCommands().get(subCommand).iterator().next();
			if (rc.getHelpText() == null || rc.getHelpText().length()==1 || rc.getHelpText().length()==0)
				continue;
			if (rc.getRequiredPermissions().size() != 0) {
				boolean failed = false;
				for (Object s : rc.getRequiredPermissions()) {
					if (!player.hasPermission(s.toString())) {
						failed = true;
					}
				}
				if (failed) continue;
			}
			String message = "§a/%s §2%s §a%s§f- §a%s";
			player.sendMessage(String.format(ChatColor.translateAlternateColorCodes('&', message),
					this.getExecCommandLabel(),
					rc.getPrefSubCommand().equals(" ") ? "" : rc.getPrefSubCommand(),
					rc.getSyntaxText().equals(" ") ? "" : rc.getSyntaxText() + " ",
					rc.getHelpText().equals(" ") ? "" : rc.getHelpText()));
		}
	}

	@Conditions("inGame")
	@Subcommand("leave")
	@Description("Leave the parkour")
	public void onLeave(Player player) {
		Game game = Main.getInstance().getGameManager().getGames().get(player.getUniqueId());
		game.end();

		Main.getInstance().getGameManager().getGames().remove(player.getUniqueId());
	}

	@Subcommand("setparkourstart")
	@Description("Change the start location of the parkour")
	public void setParkourStart(Player player) {
		Location location = player.getLocation();
		Main.getInstance().getConfig().set("parkour-start", location);
		Main.getInstance().saveConfig();
		Main.getInstance().getGameManager().setStartLocation(location);

		player.sendMessage("§aSuccesfully saved the new start location of the parkour.");
	}

	@Default
	@Subcommand("menu")
	public void onMain(Player player) {
		ParkourGUI parkourGUI = new ParkourGUI(player);
		parkourGUI.open();
	}

}
