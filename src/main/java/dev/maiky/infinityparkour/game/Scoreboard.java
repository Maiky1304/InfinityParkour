package dev.maiky.infinityparkour.game;

import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.bukkit.entity.Player;

/**
 * Door: Maiky
 * Info: InfinityParkour - 04 Jun 2021
 * Package: dev.maiky.infiniteparkour.game
 */

public class Scoreboard {

	private final Player player;
	private final Game game;

	public Scoreboard(Game game) {
		this.player = game.getBukkitPlayer();
		this.game = game;
	}

	public void initialize() {
		double copy = this.game.getTime();
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		int milliseconds;

		while(copy >= 3600) {
			copy-=3600;
			hours++;
		}
		while(copy >= 60) {
			copy-=60;
			minutes++;
		}
		while(copy >= 1) {
			copy-=1;
			seconds++;
		}
		milliseconds = (int) (copy * 1000);

		String format = "%sh %sm %ss %sms";
		String format2 = "%ss %sms";
		String format3 = "%sm %ss %sms";
		String display;
		final Object o = String.valueOf(milliseconds).length() > 3 ? String.valueOf(milliseconds).substring(0, 3) :
				milliseconds;
		if (hours == 0 && minutes == 0) {
			display = String.format(format2, seconds, o);
		} else if (hours == 0) {
			display = String.format(format3, minutes, seconds, o);
		} else {
			display = String.format(format, hours, minutes, seconds, o);
		}

		BPlayerBoard board = Netherboard.instance().createBoard(player, "§f§lPARKOUR");
		board.set("§1", 6);
		board.set("§9Score:", 5);
		board.set("§f" + this.game.getScore(), 4);
		board.set("§2", 3);
		board.set("§9Time:", 2);
		board.set("§f" + display, 1);
		board.set("§3", 0);
	}

	public void update() {
		double copy = this.game.getTime();
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		int milliseconds;

		while(copy >= 3600) {
			copy-=3600;
			hours++;
		}
		while(copy >= 60) {
			copy-=60;
			minutes++;
		}
		while(copy >= 1) {
			copy-=1;
			seconds++;
		}
		milliseconds = (int) (copy * 1000);

		String format = "%sh %sm %ss %sms";
		String format2 = "%ss %sms";
		String format3 = "%sm %ss %sms";
		String display;
		final Object o = String.valueOf(milliseconds).length() > 3 ? String.valueOf(milliseconds).substring(0, 3) :
				milliseconds;
		if (hours == 0 && minutes == 0) {
			display = String.format(format2, seconds, o);
		} else if (hours == 0) {
			display = String.format(format3, minutes, seconds, o);
		} else {
			display = String.format(format, hours, minutes, seconds, o);
		}

		BPlayerBoard board = Netherboard.instance().getBoard(player);
		board.set("§f" + this.game.getScore(), 4);
		board.set("§f" + display, 1);
	}

}
