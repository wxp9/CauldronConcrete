package me.gudev.cauldronconcrete;

import org.bukkit.plugin.java.JavaPlugin;

public class CauldronConcretePlugin extends JavaPlugin {

    private static CauldronConcretePlugin instance;

    public static CauldronConcretePlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new CauldronListener(), this);
        getLogger().info("CauldronConcrete has been enabled! Developed by Gudev.");
    }

    @Override
    public void onDisable() {
        getLogger().info("CauldronConcrete has been disabled!");
    }
}
