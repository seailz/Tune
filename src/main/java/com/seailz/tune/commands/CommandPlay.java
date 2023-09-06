package com.seailz.tune.commands;

import com.seailz.discordjar.command.CommandOption;
import com.seailz.discordjar.command.CommandOptionType;
import com.seailz.discordjar.command.annotation.SlashCommandInfo;
import com.seailz.discordjar.command.listeners.slash.SlashCommandListener;
import com.seailz.discordjar.events.model.interaction.command.SlashCommandInteractionEvent;
import com.seailz.discordjar.model.embed.Embeder;
import com.seailz.discordjar.voice.model.VoiceState;
import com.seailz.tune.Tune;
import com.seailz.tune.managers.ServerMusicManager;
import com.seailz.tune.managers.ServerMusicRegistry;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.awt.*;

@SlashCommandInfo(
        name = "play",
        description = "Plays a song",
        canUseInDms = false
)
public class CommandPlay extends SlashCommandListener {

    public CommandPlay() {
        addOption(new CommandOption(
                "name",
                "The name of the song",
                CommandOptionType.STRING,
                true
        ));
    }

    @Override
    protected void onCommand(SlashCommandInteractionEvent command) {
        String song = command.getOption("name").getAsString();
        command.defer(false);

        VoiceState memberVoiceState = command.getMember().getVoiceState();
        if (memberVoiceState == null) {
            command.getHandler().followup("You are not in a voice channel!").run();
            return;
        }

        String channelId = memberVoiceState.channelId();
        if (channelId == null) {
            command.getHandler().followup("You are not in a voice channel!").run();
            return;
        }

        ServerMusicManager manager = ServerMusicRegistry.get(command.getGuild().id(), channelId);
        song = song.startsWith("http") ? song : "ytsearch:" + song;

        Tune.getPlayerManager().loadItem(song, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                manager.getScheduler().queue(track);
                manager.getScheduler().connect();

                Embeder embeder = Embeder.e();
                embeder.title("Queued Song");
                embeder.field("Title", track.getInfo().title, false);
                embeder.field("Duration", track.getInfo().isStream ? ":red_circle: Live Stream" : String.valueOf(track.getDuration()), true);
                embeder.field("Author", track.getInfo().author, true);
                embeder.field("URL", track.getInfo().uri, false);
                embeder.thumbnail("https://img.youtube.com/vi/" + track.getIdentifier() + "/hqdefault.jpg");
                embeder.color(Color.MAGENTA);
                command.getHandler().followup("").addEmbed(embeder).run();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    manager.getScheduler().queue(track);
                }
                manager.getScheduler().connect();
                command.getHandler().followup("Queued " + playlist.getTracks().size() + " songs from " + playlist.getName()).run();
            }

            @Override
            public void noMatches() {
                command.getHandler().followup("No matches found!").run();
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                command.getHandler().followup("Failed to load song!").run();
            }
        });
    }
}
