package com.mythmc.main;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mythmc.ResAddon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Bungee {
    public static String serverName = "Invalid";

    public static boolean fetchedName = false;

    public static Map<String, String> residenceMap = new ConcurrentHashMap<>();

    public static Map<String, String> teleportMap = new ConcurrentHashMap<>();

    public static void fetchServerName(Player player) {
        if (ResAddon.getInstance().isBungeecord()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("GetServer");
            player.sendPluginMessage((Plugin)ResAddon.getInstance(), "BungeeCord", out.toByteArray());
            fetchedName = true;
        }
    }

    public static void sendResListMessage(String targetServer) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF(targetServer);
        out.writeUTF("ResidenceBungee");
        ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(msgBytes);
        try {
            msgOut.writeUTF("ResList");
            msgOut.writeUTF(serverName);
            msgOut.writeUTF(String.join(",", Residence.getInstance().getResidenceManager().getResidences().entrySet().stream().filter(item -> ((ClaimedResidence)item.getValue()).getPermissions().has(Flags.tp, false, false)).map(Map.Entry::getKey).collect(Collectors.toList())));
        } catch (IOException iOException) {}
        out.writeShort((msgBytes.toByteArray()).length);
        out.write(msgBytes.toByteArray());
        Player player = (Player) Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (player != null)
            player.sendPluginMessage(ResAddon.plugin, "BungeeCord", out.toByteArray());
    }

    public static void sendResRequestMessage() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF("ResidenceBungee");
        ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(msgBytes);
        try {
            msgOut.writeUTF("ResRequest");
            msgOut.writeUTF(serverName);
        } catch (IOException iOException) {}
        out.writeShort((msgBytes.toByteArray()).length);
        out.write(msgBytes.toByteArray());
        Player player = (Player)Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (player != null)
            player.sendPluginMessage((Plugin)ResAddon.getInstance(), "BungeeCord", out.toByteArray());
    }
}


