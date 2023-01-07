package com.diamond.essentials;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    static public HashMap<Player,Integer> timeout = new HashMap<>();
    public List<Player> superpicks = new ArrayList<>();

    public void onEnable() {
        getLogger().log(Level.INFO,"This is StompzEssentials Enabling!");

        new SCEListener(this);
        runTimer();
        getLogger().log(Level.INFO,"StompzEssentials has Enabled.");
    }

    public void onDisable() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("fly")) {
                if (ifFlyPerm(player)) {
                    if(timeout.containsKey(player)){
                        player.sendMessage(ChatColor.RED + "You are engaged in pvp and cannot fly for " + ChatColor.BLUE + timeout.get(player) + ChatColor.RED + " seconds.");
                    }else{
                        if (args.length == 0) {
                            if (player.getAllowFlight()) {
                                player.setAllowFlight(false);
                                player.sendMessage(ChatColor.RED + "Flight has been disabled.");
                                if (player.isFlying()) {
                                    player.setFlying(false);
                                }
                            } else {
                                player.setAllowFlight(true);
                                player.sendMessage(ChatColor.BLUE + "Flight has been enabled.");
                            }
                        } else if (args.length == 1) {
                            if (args[0].equalsIgnoreCase("on")) {
                                player.setAllowFlight(true);
                                player.sendMessage(ChatColor.BLUE + "Flight has been enabled.");
                            } else if (args[0].equalsIgnoreCase("off")) {
                                player.setAllowFlight(false);
                                player.sendMessage(ChatColor.RED + "Flight has been disabled.");
                                if (player.isFlying()){
                                    player.setFlying(false);
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "Improper Usage. /fly [on/off]");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Improper Usage. /fly [on/off]");
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have the permission to fly.");
                }
            }else if(cmd.getName().equalsIgnoreCase("sp")){
                if(ifSPPerm(player)){
                    if(hasSuperPick(player)){
                        superpicks.remove(player);
                        player.sendMessage(ChatColor.RED + "StompzHammer has been disabled.");
                    }else{
                        superpicks.add(player);
                        player.sendMessage(ChatColor.BLUE + "StompzHammer has been enabled.");
                    }
                }else{
                    player.sendMessage(ChatColor.RED + "You don't have permission to use StompzHammer.");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You are not a person.");
        }
        return true;
    }

    public boolean ifFlyPerm(Player p){
        if(p.hasPermission("SE.fly")){
            return true;
        }
        return false;
    }

    public boolean ifSPPerm(Player p){
        if(p.hasPermission("SE.superpick")){
            return true;
        }
        return false;
    }

    public boolean hasSuperPick(Player p){
        if(superpicks.contains(p)){
            return true;
        }
        return false;
    }

    public void runTimer(){
        BukkitScheduler bs = getServer().getScheduler();
        bs.runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                List<Player> takeout = new ArrayList<>();
                for (Player p : timeout.keySet()) {
                    int temp = timeout.get(p) - 1;
                    timeout.put(p, temp);
                    if (temp == 0) {
                        takeout.add(p);
                    }
                }
                for(Player p : takeout){
                    timeout.remove(p);
                    p.sendMessage(ChatColor.BLUE + "You can now fly.");
                }
            }
        }, 20L, 20L);
    }

}