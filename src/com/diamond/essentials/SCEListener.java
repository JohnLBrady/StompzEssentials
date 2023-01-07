package com.diamond.essentials;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.Collection;

public class SCEListener implements Listener {

    Main pl;
    PluginManager pm;


    public SCEListener(Main plugin){
        pl = plugin;
        pm = pl.getServer().getPluginManager();
        pm.registerEvents(this, pl);
    }

    @EventHandler
    public void onPVP(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Player){
            if(e.getDamager() instanceof Player){
                if(!e.getEntity().equals(e.getDamager())) {
                    Player p = (Player) e.getDamager();
                    pvpFly(p);
                }
            }
            if(e.getDamager() instanceof Projectile){
                if(e.getDamage() != 0) {
                    Projectile proj = (Projectile) e.getDamager();
                    if (proj.getShooter() instanceof Player) {
                        if(!e.getEntity().equals(proj.getShooter())) {
                            Player p = (Player) proj.getShooter();
                            pvpFly(p);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSplashPotion(PotionSplashEvent e){
        Collection<PotionEffect> cop = e.getPotion().getEffects();
        for(PotionEffect pe : cop){
            if(pe.getType().equals(PotionEffectType.POISON) || pe.getType().equals(PotionEffectType.WEAKNESS)
                    || pe.getType().equals(PotionEffectType.SLOW) || pe.getType().equals((PotionEffectType.HARM))){
                Collection<LivingEntity> afflist = e.getAffectedEntities();
                for(Entity ent : afflist){
                    if(ent instanceof Player){
                        if(e.getEntity().getShooter() instanceof Player){
                            if(!ent.equals(e.getEntity().getShooter())) {
                                Player p = (Player) e.getEntity().getShooter();
                                pvpFly(p);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onLingerPotion(AreaEffectCloudApplyEvent e){
        if(e.getEntity().getBasePotionData().getType().equals(PotionType.POISON)
                || e.getEntity().getBasePotionData().getType().equals(PotionType.WEAKNESS)
                || e.getEntity().getBasePotionData().getType().equals(PotionType.SLOWNESS)
                || e.getEntity().getBasePotionData().getType().equals(PotionType.INSTANT_DAMAGE)){
            Collection<LivingEntity> afflist = e.getAffectedEntities();
            for(Entity ent : afflist){
                if(ent instanceof Player){
                    if(e.getEntity().getSource() instanceof Player){
                        if(!ent.equals(e.getEntity().getSource())) {
                            Player p = (Player) e.getEntity().getSource();
                            pvpFly(p);
                        }
                    }
                }
            }
        }
    }


    @EventHandler
    public void onSuperPick(BlockDamageEvent e){
        Player p = e.getPlayer();
        if(pl.hasSuperPick(p)){
            ItemStack item = e.getItemInHand();
            if(item.getType().equals(Material.WOODEN_PICKAXE) || item.getType().equals(Material.GOLDEN_PICKAXE)
                    || item.getType().equals(Material.IRON_PICKAXE) || item.getType().equals(Material.DIAMOND_PICKAXE)
                    || item.getType().equals(Material.STONE_PICKAXE) || item.getType().equals(Material.NETHERITE_PICKAXE)){

                Material[] noBreak = {
                        Material.BARRIER,
                        Material.SPAWNER,
                        Material.BEDROCK,
                        Material.END_PORTAL_FRAME,
                        Material.DRAGON_EGG,
                        Material.END_GATEWAY,
                        Material.END_PORTAL,
                        Material.NETHER_PORTAL
                };

                if(Arrays.stream(noBreak).noneMatch(mat -> mat == e.getBlock().getType())) {
                    e.setInstaBreak(true);
                    ItemMeta info = item.getItemMeta();
                    if(((Damageable)info).hasDamage()){
                        ((Damageable)info).setDamage(((Damageable)info).getDamage() - 2);
                    }
                    item.setItemMeta(info);
                }
            }
        }
    }

    public void pvpFly(Player p){
        if(pl.ifFlyPerm(p)){
            if(!Main.timeout.containsKey(p)){
                p.sendMessage(ChatColor.RED + "Flight has been disabled because you are engaged in pvp.");
            }
            Main.timeout.put(p,30);
            if(p.isFlying()){
                p.setFlying(false);
            }
            p.setAllowFlight(false);

        }
    }
}
