package com.mythmc.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import com.mythmc.ResAddon;
import com.mythmc.main.Bungee;
import com.mythmc.tool.Debugger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeListener implements PluginMessageListener {
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String subChannel = in.readUTF();
        if ("GetServer".equals(subChannel)) {
            Bungee.serverName = in.readUTF();
            Bungee.sendResListMessage("ALL");
            Bungee.sendResRequestMessage();
        } else if ("ResidenceBungee".equals(subChannel)) {
            short length = in.readShort();
            byte[] msgBytes = new byte[length];
            in.readFully(msgBytes);
            DataInputStream msgIn = new DataInputStream(new ByteArrayInputStream(msgBytes));
            try {
                String serverName, targetServer, playerName, buildingId, msg = msgIn.readUTF();
                switch (msg) {
                    case "ResList":
                        serverName = msgIn.readUTF();
                        if (!"Invalid".equals(serverName)) {
                            String resList = msgIn.readUTF();
                            Arrays.<String>stream(resList.split(",")).forEach(item -> Bungee.residenceMap.put(item, serverName));

                            ResAddon.getInstance().getLogger().info("服务器: " + serverName + ", 领地有: " + resList);
                        }
                        break;
                    case "ResRequest":
                        targetServer = msgIn.readUTF();
                        Bungee.sendResListMessage(targetServer);
                        break;
                    case "Teleport":
                        playerName = msgIn.readUTF();
                        buildingId = msgIn.readUTF();
                        Bungee.teleportMap.put(playerName, buildingId);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
