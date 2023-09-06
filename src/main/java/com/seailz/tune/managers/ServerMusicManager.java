package com.seailz.tune.managers;

import com.seailz.tune.Tune;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

public class ServerMusicManager {

    private final AudioPlayer player;
    private final ServerMusicScheduler scheduler;

    public ServerMusicManager(String guildId, String channelId) {
        this.player = Tune.getPlayerManager().createPlayer();
        this.scheduler = new ServerMusicScheduler(guildId, player, channelId);
    }

    public ServerMusicScheduler getScheduler() {
        return scheduler;
    }

}
