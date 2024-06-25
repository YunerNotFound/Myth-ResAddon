package com.mythmc;

import com.mythmc.hook.PlaceHolderHook;
import com.mythmc.listener.BungeeListener;
import com.mythmc.listener.PlayerListener;
import com.mythmc.tool.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class ResAddon extends JavaPlugin {
    private static ResAddon instance;
    public static Plugin plugin;
    private boolean bungeecord;
    public void onLoad() {
        saveDefaultConfig();
    }
    @Override
    public void onEnable() {
        instance = this;
        plugin = this;
        (new Debugger()).loadDebugMode();
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        this.bungeecord = Bukkit.getServer().spigot().getConfig().getBoolean("settings.bungeecord", false);
        if (this.bungeecord) {
            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeListener());
        } else {
            getLogger().warning("[ResAddon] 请在spigot.yml中将bungeecord设置为true后重启服务器再试！");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
         //   getCommand("resaddon").setExecutor(this);
         //   loadConfig();
            PlaceHolderHook placeholderHook = new PlaceHolderHook();
            placeholderHook.register();
            getLogger().info("[ResAddon] 领地附属已加载,领地跨服tp监听已开启");
        } else {
            getLogger().warning("[ResAddon] 无法注册 PlaceholderHook，取消加载此插件。");
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    public void loadConfig() {
     //   (new mainManager()).load();
        //   (new RewardsManager()).load();
        //   (new MenuManager()).load();
    }
    @Override
    public void onDisable() {
        PlaceHolderHook placeholderHook = new PlaceHolderHook();
        placeholderHook.unregister();
        if (this.bungeecord) {
            getServer().getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");
            getServer().getMessenger().unregisterIncomingPluginChannel(this, "BungeeCord");
        }
    }
    public static ResAddon getInstance() {
        return instance;
    }

    public boolean isBungeecord() {
        return this.bungeecord;
    }
}
