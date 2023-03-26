package me.navoei.antipearlglitch;

import me.navoei.antipearlglitch.event.PearlInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class AntiPearlGlitch extends JavaPlugin {

    Logger log = Bukkit.getLogger();

    @Override
    public void onEnable() {
        // Plugin startup logic
        log.info("[AntiPearlGlitch] Plugin enabled!");
        Bukkit.getPluginManager().registerEvents(new PearlInteractEvent(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        log.info("[AntiPearlGlitch] Plugin disabled!");
    }
}
