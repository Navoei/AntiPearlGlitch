package me.navoei.antipearlglitch;

import me.navoei.antipearlglitch.event.PearlInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class AntiPearlGlitch extends JavaPlugin {

    Logger log = Bukkit.getLogger();
    static AntiPearlGlitch instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        AntiPearlGlitch.instance = this;
        Bukkit.getPluginManager().registerEvents(new PearlInteractEvent(), this);
        this.saveDefaultConfig();
        log.info("[AntiPearlGlitch] Plugin enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        log.info("[AntiPearlGlitch] Plugin disabled!");
    }

    public static AntiPearlGlitch getInstance() {
        return instance;
    }
}
