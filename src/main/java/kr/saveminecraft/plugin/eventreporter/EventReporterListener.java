package kr.saveminecraft.plugin.eventreporter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

public class EventReporterListener implements Listener {
    private boolean isRegistered = false;
    private int scheduleId = -1;

    public void registerEvent() {
        if (!isRegistered) {
            PluginManager pm = Bukkit.getPluginManager();
            pm.registerEvents(this, Main.plugin);
        }

        if (scheduleId < 0) {
            this.scheduleId = Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, () -> {
                this.updateAllPlayers();
            }, 0, 20 * 30);
        }
    }

    public void unregisterEvent() {
        if (isRegistered) {
            PlayerJoinEvent.getHandlerList().unregister(this);
            PlayerQuitEvent.getHandlerList().unregister(this);
            isRegistered = false;
        }

        if (scheduleId >= 0) {
            Main.plugin.getServer().getScheduler().cancelTask(scheduleId);
            scheduleId = -1;
        }
    }

    public void updatePlayer(Player player, boolean isOnline) {
        new Thread(() -> {
            if (Main.reporter != null) {
                Main.reporter.updatePlayer(player, isOnline);
            }
        }).run();
    }

    public void updateAllPlayers() {
        new Thread(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Main.reporter != null) {
                    Main.reporter.updatePlayer(player);
                }
            }
        }).run();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.updatePlayer(player, true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.updatePlayer(player, false);
    }
}
