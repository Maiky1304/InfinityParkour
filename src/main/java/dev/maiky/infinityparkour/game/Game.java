package dev.maiky.infinityparkour.game;

import dev.maiky.infinityparkour.Main;
import dev.maiky.infinityparkour.game.data.PlayerData;
import fr.minuskube.netherboard.Netherboard;
import lombok.Getter;
import lombok.Setter;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Door: Maiky
 * Info: InfinityParkour - 04 Jun 2021
 * Package: dev.maiky.infiniteparkour.game
 */

public class Game {

	private final CompositeTerminable terminable = CompositeTerminable.create();

	public static Game with(Player bukkitPlayer) {
		return new Game(bukkitPlayer);
	}

	@Getter @Setter
	private double time;
	@Getter @Setter
	private int score;
	@Getter
	private final Player bukkitPlayer;
	@Getter @Setter
	private Location nextBlock, previousBlock;
	@Getter
	private final List<Location> allBlocks = new ArrayList<>();
	@Getter
	private Scoreboard scoreboard;

	private Game(Player bukkitPlayer) {
		this.time = 0;
		this.bukkitPlayer = bukkitPlayer;
	}

	public void drawBar() {
		double copy = this.time;
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

		this.getBukkitPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
				new TextComponent("§9Score: §a§n" + this.score + "§r  §9Time: §f" + display));
	}

	public void launch() {
		// Scoreboard
		this.scoreboard = new Scoreboard(this);
		this.scoreboard.initialize();

		// Tasks
		Schedulers.sync().runRepeating(this::drawBar, 0, 1).bindWith(terminable);
		Schedulers.sync().runRepeating(this::incrementTime, 0, 1).bindWith(terminable);
		Schedulers.sync().runRepeating(this::hide, 0, 1).bindWith(terminable);
		Schedulers.sync().runRepeating(scoreboard::update, 0, 1).bindWith(terminable);

		// Events
		Events.subscribe(PlayerMoveEvent.class)
				.filter(e -> e.getFrom().distanceSquared(Objects.requireNonNull(e.getTo())) > 0)
				.filter(e -> e.getPlayer().equals(bukkitPlayer))
				.filter(e -> {
					Block block = e.getPlayer().getLocation().getBlock();
					Block under = block.getRelative(BlockFace.DOWN);
					return under.equals(this.nextBlock.getBlock());
				})
				.handler(e -> {
					this.getBukkitPlayer().sendBlockChange(previousBlock, Material.AIR.createBlockData());
					this.randomBlock();
					this.getBukkitPlayer().sendBlockChange(nextBlock, Material.BLACK_CONCRETE.createBlockData());
					this.particleEffect(nextBlock);
					this.incrementScore();
					e.getPlayer().sendMessage("§9§lParkour §8» §7Good Job! Score: §f" + this.score);
				}).bindWith(terminable);
		Events.subscribe(PlayerMoveEvent.class)
				.filter(e -> e.getFrom().distanceSquared(Objects.requireNonNull(e.getTo())) > 0)
				.filter(e -> e.getPlayer().equals(bukkitPlayer))
				.filter(e -> e.getTo().getY() <= (nextBlock.getY() - 3))
				.handler(e -> {
					this.end();
				}).bindWith(terminable);

		// Teleport Player
		this.getBukkitPlayer().sendBlockChange(Main.getInstance().getGameManager().getStartLocation(), Material.BLACK_CONCRETE.createBlockData());
		this.getBukkitPlayer().teleport(center(Main.getInstance().getGameManager().getStartLocation().clone()).add(0, 1, 0));
		this.getBukkitPlayer().sendMessage("§9§lParkour §8» §7Good luck, §a" + this.getBukkitPlayer().getName() + "§7!");

		// Generate First Block
		this.nextBlock = Main.getInstance().getGameManager().getStartLocation().clone();
		this.previousBlock = Main.getInstance().getGameManager().getStartLocation().clone();
		this.randomBlock();
		this.getBukkitPlayer().sendBlockChange(nextBlock, Material.BLACK_CONCRETE.createBlockData());
	}

	private void hide() {
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (onlinePlayer == bukkitPlayer)continue;
			bukkitPlayer.hidePlayer(Main.getInstance(), onlinePlayer);
		}
	}

	public Location center(Location location) {
		location.setX(Double.parseDouble(String.valueOf(location.getX()).split("\\.")[0] + ".5"));
		location.setZ(Double.parseDouble(String.valueOf(location.getZ()).split("\\.")[0] + ".5"));
		location.setYaw(0f);
		location.setPitch(0f);
		return location;
	}

	private void particleEffect(Location location) {
		if (location.getWorld() == null)
			return;
		bukkitPlayer.spawnParticle(Particle.CLOUD, location, 25);
	}

	private void randomBlock() {
		Location current = this.nextBlock.clone();
		this.previousBlock = current.clone();
		SecureRandom random = new SecureRandom();

		Block currentBlock = current.getBlock();

		int direction = random.nextInt(4);

		BlockFace face;
		if (direction == 0) face = BlockFace.NORTH;
		else if (direction == 1) face = BlockFace.EAST;
		else if (direction == 2) face = BlockFace.SOUTH;
		else face = BlockFace.WEST;

		int distance = random.nextInt(3) + 2;

		for (int i = 0; i < distance; i++) {
			currentBlock = currentBlock.getRelative(face);
		}

		this.nextBlock = currentBlock.getLocation().add(0, random.nextInt(2), 0);

		allBlocks.add(this.nextBlock);
	}

	public void end() {
		PlayerData data = Main.getInstance().getGameManager().getPlayerData().get(bukkitPlayer.getUniqueId());

		bukkitPlayer.getInventory().setContents(data.getInventoryContents());
		bukkitPlayer.getInventory().setArmorContents(data.getArmorContents());
		bukkitPlayer.getInventory().setItemInOffHand(data.getOffhand());
		bukkitPlayer.teleport(data.getLocation());

		bukkitPlayer.sendMessage("§9§lParkour §8» §7Thanks for playing your score was §f" + this.score + "§7.");

		try {
			this.terminable.close();
		} catch (CompositeClosingException e) {
			e.printStackTrace();
		} finally {
			Netherboard.instance().deleteBoard(getBukkitPlayer());
			this.allBlocks.forEach(b -> bukkitPlayer.sendBlockChange(b, Material.AIR.createBlockData()));
			Main.getInstance().getGameManager().getGames().remove(bukkitPlayer.getUniqueId());
			for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				if (onlinePlayer == bukkitPlayer)continue;
				bukkitPlayer.showPlayer(Main.getInstance(), onlinePlayer);
			}
		}
	}

	private void incrementTime() {
		this.time+=0.05;
	}

	private void incrementScore() {
		this.score++;
	}

}
