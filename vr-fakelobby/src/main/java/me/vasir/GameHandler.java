package me.vasir;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class GameHandler implements Listener {



    // Takım oluştur
    public void createTeam() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        String teamName = "Lobby";

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        // Takım seçeneklerini ayarlama
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
        team.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);

        // seeInvisibleFriends ayarını FALSE yapma
        team.setCanSeeFriendlyInvisibles(true);
    }

    // Bazı ayarlar
    @EventHandler
    public void hideNames(TabCompleteEvent e) {e.getCompletions().clear();}
    @EventHandler
    public void onPlayerChat(AsyncChatEvent e) {e.setCancelled(true);}
    @EventHandler
    public void onRecipeDiscover(PlayerRecipeDiscoverEvent event) {event.setCancelled(true);}
    @EventHandler
    public void onDamage(EntityDamageEvent e) {if(e.getEntity().getType() == EntityType.PLAYER) {e.setCancelled(true);}}
}
