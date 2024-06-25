package com.mythmc.tool;

import com.mythmc.ResAddon;




public class Debugger {
    private static boolean debugMode;
    public Debugger() {
    }

    public void loadDebugMode() {
         debugMode = ResAddon.getInstance().getConfig().getBoolean("DebugMode");
        if (debugMode) {
            ResAddon.getInstance().getLogger().warning("[ResAddon] 已开启DEBUG模式.");
        }
    }
    public static void addDebug(String meg) {
        if(debugMode) {
            ResAddon.getInstance().getLogger().info(meg);
        }
    }
}

