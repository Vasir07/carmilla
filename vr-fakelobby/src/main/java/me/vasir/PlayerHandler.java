package me.vasir;

import fr.xephi.authme.api.v3.AuthMeApi;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.time.Duration;

public class PlayerHandler implements Listener {


    private final FakeLobby plugin;
    public PlayerHandler(FakeLobby plugin) {this.plugin = plugin;}

    @EventHandler
    public void onLeft(PlayerQuitEvent e) {

        // Diğer
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam("Lobby");
        World world = e.getPlayer().getWorld();
        Player player = e.getPlayer();

        // Giriş mesajını sil
        e.quitMessage(null);

        // Parçacık oluştur
        world.spawnParticle(Particle.CLOUD, player.getLocation().add(0,1,0), 1,0,0,0,0);

        // Ses çal
        world.playSound(player.getLocation(), "custom.ui.toast.out", 0.05f, 1);

        // Takıma ekle
        if (team != null) {team.removeEntry(player.getName());}
    }



    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        // Diğer
        World world = e.getPlayer().getWorld();
        Player player = e.getPlayer();
        startAuctionBar(player);

        // İlk giriş
        if (!e.getPlayer().hasPlayedBefore()) {

            // Efekt ver
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, -1, 0, false, false));

            // Hasarı 0 yap
            AttributeInstance damage = player.getAttribute(Attribute.ATTACK_DAMAGE);
            if (damage == null) return;damage.setBaseValue(0);

        }

        // Ses çal
        world.playSound(player.getLocation(), "custom.ui.toast.in", 0.05f, 1);
        player.playSound(player.getLocation(), "custom.ui.toast.in", 1, 1);

        // Giriş mesajını sil
        e.joinMessage(null);

        // Görünmez yap
        player.setInvisible(true);

        // Efekt ver
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0, false, false));


        // Merkeze ışınla
        Location location = new Location(player.getWorld(), 0.5, 65, 0.5);
        player.teleport(location);

        // Tablist
        int playerCount = Bukkit.getOnlinePlayers().size() -1;
        Component header = Component.text("\n\uD87B\uDE5E\n\n\n");
        MiniMessage miniMessage = MiniMessage.miniMessage();
        Component footer = miniMessage.deserialize("\n<color:#515151>  Şu anda oturum lobisinde " + playerCount + " kişi ile birliktesin  <color:#515151>\n" + "<gradient:#f7dc5a:#f3b541>play.victoryrealms.com</gradient> \n");
        player.sendPlayerListFooter(footer);
        player.sendPlayerListHeader(header);

        // Authme Başlık
        if (!AuthMeApi.getInstance().isRegistered(player.getName())) {
            Title title = Title.title(
                    Component.text("Kayıt Ol").color(TextColor.fromHexString("#F1A50B")),  // Başlık
                    Component.text("/kayıt şifre").color(NamedTextColor.WHITE), // Alt başlık
                    Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(90), Duration.ofSeconds(0)) // Süreler
            );
            player.showTitle(title);
            return;
        }
        if (AuthMeApi.getInstance().isRegistered(player.getName())) {
            Title title = Title.title(
                    Component.text("Giriş Yap").color(TextColor.fromHexString("#F1A50B")),  // Başlık
                    Component.text("/giriş şifre").color(NamedTextColor.WHITE), // Alt başlık
                    Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(90), Duration.ofSeconds(0)) // Süreler
            );
            player.showTitle(title);
        }
    }

    public void startAuctionBar(Player player) {
        // Başlangıç sayacı 90
        final int[] startCount = {90};

        new BukkitRunnable() {
            @Override
            public void run() {
                // Eğer oyuncu giriş yaptıysa sayacı durdur
                if (AuthMeApi.getInstance().isAuthenticated(player)) {cancel(); return;}

                // Sayaç 0'a ulaşırsa durdur
                if (startCount[0] <= 0) {cancel();}

                // XP barı için ilerleme oranı hesapla (0.0 - 1.0 arasında)
                float progress = startCount[0] / 90f;

                // Oyuncunun XP barını güncelle
                player.setExp(progress);

                // Sayaç bir azalt
                startCount[0]--;
            }
        }.runTaskTimer(plugin, 0L, 20L); // Her saniyede bir çalıştırılır (20L = 1 saniye)
    }
}
