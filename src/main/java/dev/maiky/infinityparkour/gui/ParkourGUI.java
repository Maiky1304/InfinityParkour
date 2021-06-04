package dev.maiky.infinityparkour.gui;

import dev.maiky.infinityparkour.Main;
import dev.maiky.infinityparkour.game.GameManager;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.Slot;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Door: Maiky
 * Info: InfinityParkour - 04 Jun 2021
 * Package: dev.maiky.infiniteparkour.gui
 */

public class ParkourGUI extends Gui {

	public ParkourGUI(Player player) {
		super(player, 3, "&8&lInfinity Parkour");
	}

	private MenuScheme buttons = new MenuScheme()
			.mask("000000000")
			.mask("001010100")
			.mask("000000000");

	@Override
	public void redraw() {
		Item[] items = new Item[]{
				ItemStackBuilder.of(Material.BOOK)
				.name("&9&lHow to play?")
				.lore("&7Your only mission is to jump","&7on the closest block you can see.",
						"&7After jumping, look around and find the next one.","","&fTo start playing, click",
						"&fon the &bdiamond boots&f.")
				.buildItem().build(),
				ItemStackBuilder.of(Material.DIAMOND_BOOTS)
				.name("&9&lPlay")
				.lore("&7Once you click play you will be", "&7teleported and your items will be taken.",
						"&7To get your items back, fall down or leave", "&7by using &c/parkour leave")
				.build(this::startParkour),
				ItemStackBuilder.of(Material.OAK_SIGN)
				.name("&9&lCredits")
				.lore("&7Original idea by &bMeldiron&7.","&7Coded in &9Java &7by &bMaiky1304&7.",
						"&7Requested for &b&lDevRoom Trial&7.").buildItem().build()
		};
		int i = 0;
		MenuPopulator populator = this.buttons.newPopulator(this);
		while(populator.hasSpace()) {
			populator.accept(items[i]);
			i++;
		}

		for (int j = 0; j < 27; j++) {
			Slot slot = getSlot(j);
			if (slot.hasItem()) continue;
			slot.setItem(ItemStackBuilder.of(Material.WHITE_STAINED_GLASS_PANE).name(" ").build());
		}
	}

	private void startParkour() {
		GameManager gameManager = Main.getInstance().getGameManager();
		gameManager.createGame(this.getPlayer());
	}

}
