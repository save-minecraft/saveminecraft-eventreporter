package kr.saveminecraft.plugin.eventreporter;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public static String host = "https://api.saveminecraft.kr";
    public static Plugin plugin = null;

    public static Reporter reporter = null;
    public static EventReporterListener listener = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Main.plugin = this;
        this.saveDefaultConfig();

        FileConfiguration config = this.getConfig();
        boolean isValidToken = true;
        isValidToken = isValidToken && config.get("token") != null;

        if (isValidToken) isValidToken = config.getString("token").length() > 0;

        if (isValidToken) {
            String token = config.getString("token");
            Main.reporter = new Reporter(token);

            Main.listener = new EventReporterListener();
            Main.listener.registerEvent();
        } else {
            this.getLogger().severe("Token is missing!");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (Main.listener != null) {
            Main.listener.unregisterEvent();
        }
    }
}
