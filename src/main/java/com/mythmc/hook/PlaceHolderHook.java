package com.mythmc.hook;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.api.ResidencePlayerInterface;
import com.bekvon.bukkit.residence.containers.ResidencePlayer;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class PlaceHolderHook extends PlaceholderExpansion {

    public PlaceHolderHook() {
    }


    public String getIdentifier() {
        return "resaddon";
    }


    public String getAuthor() {
        return "404Yuner";
    }


    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    public String onPlaceholderRequest(Player player, String params) {
        String[] args = params.split("_");
        if (args.length >= 2) {
            String type = args[0];
            String key = args[1];
            String res = null;

            if (args.length == 3) {
                res = args[2];
            }

            switch (type.toLowerCase()) {
                case "res":
                    switch (key.toLowerCase()) {
                        case "own":
                            return String.join(",", getOwnResidenceList(player.getName()));
                        case "all":
                            return String.join(",", getAllResidenceList(player.getName()));
                        case "allowner":
                            return String.join(",", getAllResidenceListOwner(player.getName()));
                        default:
                            break;
                    }
                case "list":
                    switch (key.toLowerCase()) {
                        case "trust":
                            return String.join(",", getTrustedPlayers(res));
                        case "black":

                            return null;
                        case "in":
                            return String.join(",", getPlayersInResidence(res));
                    }
            }
        }
        return "null";
    }

    public ArrayList<String> getOwnResidenceList(String playerName) {
        ResidencePlayerInterface residenceAPI = ResidenceApi.getPlayerManager();
        ArrayList<String> residenceList = residenceAPI.getResidenceList(playerName);

        // 如果返回的列表为空，则返回一个只包含"null"字符串的列表
        if (residenceList == null || residenceList.isEmpty()) {
            return new ArrayList<>(Collections.singletonList("null"));
        }

        // 否则，返回原始的列表
        return residenceList;
    }

    public static List<String> getAllResidenceList(String playerName) {
        TreeMap<String, ClaimedResidence> map = Residence.getInstance().getPlayerManager().getTrustedResidencesMap(playerName, true, false, null);
        if (map == null || map.isEmpty()) {
            // 如果map为空或为null，返回一个只包含"null"字符串的列表
            return Collections.singletonList("null");
        }
        // 否则，返回包含所有居住地名称的列表
        return new ArrayList<>(map.keySet());
    }
    public static List<String> getAllResidenceListOwner(String playerName) {
        List<String> owner = new ArrayList<>();
        TreeMap<String, ClaimedResidence> map = Residence.getInstance().getPlayerManager().getTrustedResidencesMap(playerName,true,false,null);
        for (ClaimedResidence residence : map.values()) {
            owner.add(residence.getOwner());
        }
        return owner;
    }

    public static List<String> getTrustedPlayers(String residenceName) {
        List<String> playersWithBuildPermission = new ArrayList<>();
        ClaimedResidence residence = Residence.getInstance().getResidenceManager().getByName(residenceName);
        if (residence != null) {
            Set<ResidencePlayer> ww = residence.getTrustedPlayers();
            for (ResidencePlayer player : ww) {
                UUID uuid = UUID.fromString(player.getName());
                OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
                playersWithBuildPermission.add(p.getName());
            }
            if (playersWithBuildPermission.isEmpty()) {
                return new ArrayList<>(Collections.singletonList("null"));
            }
            return playersWithBuildPermission;
        }
        return Collections.singletonList("null");
    }
    public static List<String> getPlayersInResidence(String residenceName) {
        List<String> in = new ArrayList<>();
        ClaimedResidence residence = Residence.getInstance().getResidenceManager().getByName(residenceName);
        if (residence != null) {
            List<Player> list = residence.getPlayersInResidence();
            for (Player playerNameInResidence : list) {
                in.add(playerNameInResidence.getName());
            }
            return in;
        }
        return Collections.singletonList("null");
    }
}
