package me.vasir;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class PluginMessage implements PluginMessageListener {

    private final FakeLobby plugin;

    public PluginMessage(FakeLobby plugin) {this.plugin = plugin;}

    @Override
    public void onPluginMessageReceived(String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!channel.equals("BungeeCord")) return;

        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(message))) {
            String subChannel = in.readUTF();
            if (subChannel.equals("Connect")) {
                String serverName = in.readUTF();
                sendPlayerToServer(player, serverName);
            }
        } catch (Exception e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }


    public void sendPlayerToServer(Player player, String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }
}
