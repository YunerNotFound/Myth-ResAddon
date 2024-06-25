package com.mythmc.listener;

import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.event.ResidenceChangedEvent;
import com.bekvon.bukkit.residence.event.ResidenceCreationEvent;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import com.mythmc.ResAddon;
import com.mythmc.file.ConfigManager;
import com.mythmc.main.Bungee;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.bekvon.bukkit.residence.Residence;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

public class PlayerListener implements Listener {
    BukkitScheduler server = Bukkit.getServer().getScheduler();

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        if (!Bungee.fetchedName)
            (new BukkitRunnable() {
                public void run() {
                    Bungee.fetchServerName(e.getPlayer());
                }
            }).runTaskLater(ResAddon.getInstance(), 1L);
        final Player player = e.getPlayer();
        final String targetResidence = Bungee.teleportMap.get(player.getName());
        if (targetResidence != null) {
            Bungee.teleportMap.remove(player.getName());
            (new BukkitRunnable() {
                public void run() {
                    Residence.getInstance().getResidenceManager().getByName(targetResidence).tpToResidence(player, player, false);
                }
            }).runTaskLater((Plugin) ResAddon.getInstance(), 1L);
        }
    }

    @EventHandler
    public void onPlayerChat(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().startsWith("/residence ") || e.getMessage().startsWith("/res ") || e.getMessage().startsWith("/resadmin ")) {
            String[] args = e.getMessage().split(" ");
            if (args.length > 1)
                switch (args[1].toLowerCase()) {
                    case "tp":
                        if (args.length == 3 && Residence.getInstance().getResidenceManager().getByName(args[2]) == null) {
                            String targetServer = (String) Bungee.residenceMap.get(args[2]);
                            if (targetServer != null) {
                                e.setCancelled(true);
                                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                                out.writeUTF("Forward");
                                out.writeUTF(targetServer);
                                out.writeUTF("ResidenceBungee");
                                ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
                                DataOutputStream msgOut = new DataOutputStream(msgBytes);
                                try {
                                    msgOut.writeUTF("Teleport");
                                    msgOut.writeUTF(e.getPlayer().getName());
                                    msgOut.writeUTF(args[2]);
                                } catch (IOException ignored) {
                                }
                                out.writeShort((msgBytes.toByteArray()).length);
                                out.write(msgBytes.toByteArray());
                                e.getPlayer().sendPluginMessage((Plugin) ResAddon.getInstance(), "BungeeCord", out.toByteArray());
                                out = ByteStreams.newDataOutput();
                                out.writeUTF("Connect");
                                out.writeUTF(targetServer);
                                e.getPlayer().sendPluginMessage((Plugin) ResAddon.getInstance(), "BungeeCord", out.toByteArray());
                            }
                        }
                        break;
                    case "set":
//                        if (args.length < 4 || !"tp".equalsIgnoreCase(args[2]) || !"tp".equalsIgnoreCase(args[3]))
//                            break;
                    case "remove":
                        (new BukkitRunnable() {
                            public void run() {
                                Bungee.sendResListMessage("ALL");
                            }
                        }).runTaskLater((Plugin) ResAddon.getInstance(), 20L);
                        break;
                }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        //  this.server.runTaskAsynchronously((Plugin) ResAddon.getInstance(), () -> {
        Block block = event.getClickedBlock();
        if (block != null && block.getType() == Material.CAMPFIRE) {
            Player player = event.getPlayer();
//                ClaimedResidence res;
//                res = Residence.getInstance().getResidenceManager().getByLoc(player.getPlayer().getLocation());
//                if (res == null) {
//                    player.sendMessage("领地不存在");
//                    return;
//                }
//                player.sendMessage("领地存在");
//                Flags flag;
//                flag = Flags.getFlag("build");
//                if (res.getPermissions().playerHas(player, flag, FlagPermissions.FlagCombo.TrueOrNone)) {
//                    event.setCancelled(true);
//                    player.sendMessage("已取消时间");


            String canCreate = PlaceholderAPI.setPlaceholders(player, "%residence_user_current_flag_build%");
            String currentFlagBuild = "%residence_user_current_flag_build%";
            if (canCreate.equals(currentFlagBuild)) {
                event.setCancelled(true);

                return;
            }
            if (!canCreate.equalsIgnoreCase("true")) {
                if (event.getAction().name().contains("LEFT_CLICK")) {
                    event.setCancelled(true);
                    player.sendMessage("§f[领地] §c你没有 §6place-放置方块,覆盖build权限 §c权限.");
                }
            }
        }

        // });
    }

    @EventHandler
    public void onCreateRes(ResidenceCreationEvent event) {
        if (Objects.equals(event.getPlayer().getWorld().getName().split("/")[0], "SelfHomeWorld")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§a[领地] §c家园里不可以创建领地，请前往生存世界圈地!");
        }
    }

    @EventHandler
    public void resJoin(ResidenceChangedEvent event) {
        Player player = event.getPlayer();
        ClaimedResidence from = event.getFrom();
        ClaimedResidence to = event.getTo();
        if (to == null) {
            if (!player.hasPermission("cmi.command.fly")) {
                player.setAllowFlight(false);
                player.setFlying(false);
                player.setFlySpeed(0.1F);
                return;
            }
        }
      //  ClaimedResidence res = (from == null) ? to : from;
    //    Player owner = Bukkit.getPlayer(res.getOwner());
        if (player != null && !player.isOp()) {
            if (player.hasPermission("fly.residence")) {
                if (!player.isFlying()) {
                    player.setAllowFlight(true);
                    player.setFlying(true);
                    player.setFlySpeed(0.1F);
                    player.sendMessage("§a[领地] §b今日是周末领地免费飞行日，领地内可无限飞行!");
                }
            }
        }
    }

//    @EventHandler
//    public void onJoin(PlayerJoinEvent event) {
//        Player player = event.getPlayer();
//        String worldName = player.getWorld().getName();
//
//        // 创建包含时间信息的DateTimeFormatter
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // 注意这里增加了小时和分钟
//
//        try {
//            // 假设我们有一个免费飞行的特定时间点
//            String freeFlyTime = "2023-06-15 13:00"; // 替换为你需要的时间点
//            LocalDateTime inputTime = LocalDateTime.parse(freeFlyTime, formatter);
//            LocalDateTime currentTime = LocalDateTime.now();
//
//            // 如果当前时间等于或超过免费飞行时间，则允许飞行
//            if (currentTime.isAfter(inputTime)) {
//                for (String blackWorld : ConfigManager.BLACK_WORLDS) {
//                    if (worldName.contains(blackWorld)) {
//                        player.sendMessage("§a[飞行] §c此世界禁止飞行");
//                        return;
//                    }
//                }
//                setFly(player);
//                player.sendMessage("§a[飞行] 已进入免费飞行时间，无限畅飞!");
//                return;
//            }
//
//            // 如果还未到免费飞行时间，则不进行任何操作
//            player.sendMessage("§a[飞行] §c目前还未到免费飞行时间，请等待。");
//
//        } catch (Exception e) {
//            System.out.println("日期格式错误 年-月-日 HH:mm 单个数字用0作为补充");
//        }
//    }

    private void setFly(Player player) {
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFlySpeed(0.1F);
    }
}