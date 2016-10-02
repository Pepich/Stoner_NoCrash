package com.redstoner.nocrash;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener
{
	/**
	 * Runs when the plugin gets enabled. Registers the listeners
	 */
	@Override
	public void onEnable()
	{
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	/**
	 * Runs when the plugin gets disabled. Does nothing
	 */
	@Override
	public void onDisable()
	{
	
	}
	
	/**
	 * Runs when someone executes a command. Executes the code necessary to perform the action specified by the command.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		header(sender, "NoCrash");
		if (args.length < 2)
			return help(sender);
		else
		{
			String name = args[0];
			Player player = null;
			for (Player p : Bukkit.getServer().getOnlinePlayers())
			{
				if (p.getName().equalsIgnoreCase(name)) player = p;
			}
			
			if (player == null)
			{
				sender.sendMessage("§cPlayer §e" + name + " §ccould not be found!");
				return true;
			}
			int action = 7;
			if (args.length == 3)
			{
				if (args[2].equalsIgnoreCase("drop"))
					action = 1;
				else if (args[2].equalsIgnoreCase("move"))
					action = 2;
				else if (args[2].equalsIgnoreCase("use"))
					action = 4;
				else if (args[2].equalsIgnoreCase("all"))
					action = 7;
				else
					return false;
			}
			if ((action & 1) > 0) sender.sendMessage("§ePlayer " + player.getDisplayName() + " §ecan "
					+ (player.hasPermission("utils.item.drop." + args[1]) ? "" : "not ") + "drop the item!");
			if ((action & 2) > 0) sender.sendMessage("§ePlayer " + player.getDisplayName() + " §ecan "
					+ (player.hasPermission("utils.item.move." + args[1]) ? "" : "not ") + "move the item!");
			if ((action & 4) > 0) sender.sendMessage("§ePlayer " + player.getDisplayName() + " §ecan "
					+ (player.hasPermission("utils.item.use." + args[1]) ? "" : "not ") + "use the item!");
			return true;
		}
	}
	
	/**
	 * This method will display the plugin header to the person specified
	 * 
	 * @param retriever the person to get the plugin header sent to
	 * @param name the plugin name
	 */
	public static void header(CommandSender retriever, String name)
	{
		retriever.sendMessage("§2=== " + name + " §2===");
		retriever.sendMessage("");
	}
	
	/**
	 * This method will print the help screen to the person specified
	 * 
	 * @param sender whom to show the help screen to
	 * @return true
	 */
	public boolean help(CommandSender sender)
	{
		sender.sendMessage("&eNoCrash Help:");
		sender.sendMessage(
				"§eUsage: /<command> <player> <item> [drop/move/use/&aall&e] &7or &e/<command> help &7for help");
				
		if (sender.hasPermission("utils.nocrash.seedocs"))
		{
			sender.sendMessage("§eBleh, the link to the permissions doc is on slack you derp :3");
		}
		
		if (sender.hasPermission("utils.nocrash.seeperms"))
		{
			sender.sendMessage("§eDrop permissions: §7utils.item.drop.MATERIAL:DAMAGE_VALUE");
			sender.sendMessage("§eMove permissions: §7utils.item.move.MATERIAL:DAMAGE_VALUE");
			sender.sendMessage("§eUse permissions: §7utils.item.use.MATERIAL:DAMAGE_VALUE");
			sender.sendMessage("§eAdmin permissions: §7utils.nocrash.seedocs §7and §eutils.nocrash.seeperms");
		}
		
		return true;
	}
	
	/**
	 * This method will prevent dropping disallowed items
	 * 
	 * @param event the PlayerDropItemEvent to check
	 */
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		
		String material = event.getItemDrop().getItemStack().getData().getItemType().toString();
		int durability = event.getItemDrop().getItemStack().getDurability();
		Player player = event.getPlayer();
		
		if (!player.hasPermission("utils.item.drop." + material)
				|| !player.hasPermission("utils.item.drop." + material + ":" + durability))
		{
			player.sendMessage("§cYou are not allowed to drop this item!");
			event.setCancelled(true);
		}
	}
	
	/**
	 * This method will prevent putting disallowed items into containers
	 * 
	 * @param event the InventoryMoveItemEvent to check
	 */
	@EventHandler
	public void onItemMove(InventoryClickEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		
		String material = event.getCurrentItem().getData().getItemType().toString();
		int durability = event.getCurrentItem().getDurability();
		Player player = (Player) event.getClickedInventory().getHolder();
		
		if (!player.hasPermission("utils.item.move." + material)
				|| !player.hasPermission("utils.item.move." + material + ":" + durability))
		{
			player.sendMessage("§cYou are not allowed to move this item around!");
			event.setCancelled(true);
		}
	}
	
	/**
	 * This method will prevent the use of certain items
	 * 
	 * @param event the PlayerInteractEvent to check
	 */
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
		{
			return;
		}
		
		Player player = event.getPlayer();
		String material = player.getItemInHand().getType().toString();
		int durability = player.getItemInHand().getDurability();
		
		if (!player.hasPermission("utils.item.use." + material)
				|| !player.hasPermission("utils.item.use." + material + ":" + durability))
		{
			player.sendMessage("§cYou are not allowed to use this item!");
			event.setCancelled(true);
		}
	}
	
	/**
	 * This method will prevent the use of certain items
	 * 
	 * @param event the PlayerInteractEvent to check
	 */
	@EventHandler
	public void onPlayerEntityInteract(PlayerInteractEntityEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		if (event.getRightClicked() == null)
		{
			return;
		}
		
		Player player = event.getPlayer();
		String material = player.getItemInHand().getType().toString();
		int durability = player.getItemInHand().getDurability();
		
		if (!player.hasPermission("utils.item.use." + material)
				|| !player.hasPermission("utils.item.use." + material + ":" + durability))
		{
			player.sendMessage("§cYou are not allowed to use this item!");
			event.setCancelled(true);
		}
	}
}
