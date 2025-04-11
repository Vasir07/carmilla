package me.vasir;

import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.AuthMeAsyncPreRegisterEvent;
import fr.xephi.authme.events.FailedLoginEvent;
import fr.xephi.authme.events.LoginEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.time.Duration;

public class LoginHandler implements Listener {


    // Plugini al

    private final FakeLobby plugin;
    private final PluginMessage pluginMessage;

    // Yapıcı metot
    public LoginHandler(FakeLobby plugin) {
        this.plugin = plugin;
        this.pluginMessage = new PluginMessage(plugin);
    }


    @EventHandler
    public void onPlayerLogin(LoginEvent e) {

        // Takımı al
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam("Lobby");

        // Oyuncuyu al
        Player player = e.getPlayer();

        // Takıma ekle
        if (team != null) {
            team.addEntry(player.getName());
        }

        // Title
        Title title = Title.title(
                Component.text("Giriş Yapıldı").color(TextColor.fromHexString("#8CC70A")),
                Component.text("lobiye aktarılıyorsun...").color(NamedTextColor.WHITE),
                Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(4), Duration.ofSeconds(0)));

        // Title göster ve ses çal
        player.showTitle(title);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

        new BukkitRunnable() {
            @Override
            public void run() {

                // Giriş kontrol
                if (!AuthMeApi.getInstance().isAuthenticated(player)) return;
                pluginMessage.sendPlayerToServer(player, "Factions");
            }
        }.runTaskLater(plugin, 80L);
    }


    @EventHandler
    public void wrongPassword(FailedLoginEvent e) {

        // Oyuncuyu al
        Player player = e.getPlayer();

        // Title
        Title title = Title.title(
                Component.text("Şifre Yanlış").color(TextColor.fromHexString("#E23434")),  // Başlık (Turuncu-kırmızı)
                Component.text("tekrar deneyin...").color(NamedTextColor.WHITE), // Alt başlık (Altın sarısı)
                Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(2), Duration.ofSeconds(0)) // Süreler
        );

        // Title göster ve ses çal
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
        player.showTitle(title);


        // Zamanlayıcı
        new BukkitRunnable() {
            @Override
            public void run() {

                // Giriş kontrol
                if (AuthMeApi.getInstance().isAuthenticated(player)) {
                    return;
                }

                // Kayıt kontrol
                if (AuthMeApi.getInstance().isRegistered(player.getName())) {
                    Title title = Title.title(
                            Component.text("Giriş yap").color(TextColor.fromHexString("#F1A50B")),  // Başlık
                            Component.text("/giriş şifre").color(NamedTextColor.WHITE), // Alt başlık
                            Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(90), Duration.ofSeconds(0)) // Süreler
                    );
                    player.showTitle(title);
                }
            }
        }.runTaskLater(plugin, 40L);
    }


    @EventHandler
    public void otherPassword(AuthMeAsyncPreRegisterEvent e) {

        // Zamanlayıcı (çünkü register'ı
        new BukkitRunnable() {
            @Override
            public void run() {

                // Oyuncuyu al
                Player player = e.getPlayer();

                // Giriş kontrol
                if (AuthMeApi.getInstance().isAuthenticated(player)) {
                    return;
                }

                // Title
                Title title = Title.title(
                        Component.text("Kayıt başarısız").color(TextColor.fromHexString("#E23434")),  // Başlık
                        Component.text("tekrar deneyiniz...").color(NamedTextColor.WHITE), // Alt başlık
                        Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(2), Duration.ofSeconds(0)) // Süreler
                );

                // Title göster
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                player.showTitle(title);

                // Zamanlayıcı
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Eğer oyuncu zaten kayıtlıysa işlem yapma
                        if (AuthMeApi.getInstance().isAuthenticated(player)) {
                            return;
                        }
                        if (AuthMeApi.getInstance().isRegistered(player.getName())) {
                            return;
                        }

                        // AuthMe başlık
                        Title title = Title.title(
                                Component.text("Kayıt Ol").color(TextColor.fromHexString("#F1A50B")),  // Başlık
                                Component.text("/kayıt şifre").color(NamedTextColor.WHITE), // Alt başlık
                                Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(90), Duration.ofSeconds(0)) // Süreler
                        );
                        player.showTitle(title);
                    }
                }.runTaskLater(plugin, 40L);
                player.showTitle(title);
            }
        }.runTaskLater(plugin, 2L);
    }

}
