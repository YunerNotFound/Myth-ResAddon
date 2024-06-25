package com.mythmc.file;

import com.mythmc.ResAddon;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {


    public static List<String> FREE_FLY_DAY = new ArrayList<>();

    public static List<String> BLACK_WORLDS = new ArrayList<>();

    public void init() {
        FREE_FLY_DAY.clear();

        FileConfiguration config = ResAddon.plugin.getConfig();
        FREE_FLY_DAY = config.getStringList("Fly.FreeFlyDay");
        BLACK_WORLDS = config.getStringList("Fly.BlackWorld");
    }
}
