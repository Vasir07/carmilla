package me.vasir;

import org.bukkit.plugin.java.JavaPlugin;


public class FakeLobby extends JavaPlugin {



    @Override
    public void onEnable() {

        // Takım oluştur
        GameHandler gameHandler = new GameHandler();
        gameHandler.createTeam();

        // PluginMessage kaydı
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new PluginMessage(this));
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // Event kaydı
        getServer().getPluginManager().registerEvents(new LoginHandler(this), this);
        getServer().getPluginManager().registerEvents(new CommandHandler(), this);
        getServer().getPluginManager().registerEvents(new PlayerHandler(this), this);
        getServer().getPluginManager().registerEvents(new GameHandler(), this);
    }

    @Override
    public void onDisable() {

        // Kayıtları temizle
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

}
