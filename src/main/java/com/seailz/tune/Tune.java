package com.seailz.tune;

import com.seailz.discordjar.DiscordJar;
import com.seailz.discordjar.DiscordJarBuilder;
import com.seailz.discordjar.model.status.Status;
import com.seailz.discordjar.model.status.StatusType;
import com.seailz.discordjar.model.status.activity.Activity;
import com.seailz.discordjar.model.status.activity.ActivityType;
import com.seailz.tune.commands.CommandPlay;
import com.seailz.tune.commands.CommandSkip;
import com.seailz.tune.commands.CommandVolume;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

public class Tune {

    private static AudioPlayerManager playerManager;
    private static DiscordJar djar;

    public static void main(String[] args) {
        new Tune(args[0]);
    }

    public Tune(String botToken) {
        Tune.djar = new DiscordJarBuilder(botToken)
                .defaultCacheTypes()
                .defaultIntents()
                .build();

        djar.registerCommands(new CommandPlay(), new CommandSkip(), new CommandVolume());
        djar.setStatus(new Status(new Activity("music", ActivityType.STREAMING).setStreamUrl("https://twitch.tv/seailzlive"), StatusType.ONLINE));

        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    public static AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

    public static DiscordJar getDjar() {
        return djar;
    }
}