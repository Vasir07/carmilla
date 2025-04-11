package me.vasir;

import fr.xephi.authme.api.v3.AuthMeApi;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class CommandHandler implements Listener {



    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {

        // Oyuncu al
        Player player = e.getPlayer();

        // Bypass
        if (player.isOp()) return;

        // Girilen komutu al
        String command = e.getMessage().split(" ")[0].toLowerCase().replace("/", "");

        // Kaydı kontrol et
        if (AuthMeApi.getInstance().isRegistered(player.getName())) {
            if (!(command.equals("giriş") || command.equals("kayıt"))) {
                e.setCancelled(true);
                Component message = Component.text("\uD87B\uDE5F ", TextColor.fromHexString("#FFFFFF")).append(Component.text("Böyle bir komut yok!", TextColor.fromHexString("#E23434"))); // Kırmızı metin
                player.sendMessage(message);
            }
        }
    }



    @EventHandler
    public void onCommand(PlayerCommandSendEvent e) {

        // Oyuncu al
        Player player = e.getPlayer();

        // Bypass
        if (player.isOp()) return;

        // Komutları sil
        e.getCommands().clear();

        // Kaydı kontrol et
        if (AuthMeApi.getInstance().isRegistered(player.getName())) {
            e.getCommands().add("giriş");
            return;
        }
        if (!AuthMeApi.getInstance().isRegistered(player.getName())) {
            e.getCommands().add("kayıt");
        }
    }
}
