package com.seailz.tune.commands;

import com.seailz.discordjar.command.annotation.SlashCommandInfo;
import com.seailz.discordjar.command.listeners.slash.SlashCommandListener;
import com.seailz.discordjar.events.model.interaction.command.SlashCommandInteractionEvent;
import com.seailz.discordjar.model.embed.Embeder;
import com.seailz.discordjar.voice.model.VoiceState;
import com.seailz.tune.managers.ServerMusicManager;
import com.seailz.tune.managers.ServerMusicRegistry;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;

@SlashCommandInfo(
        name = "skip",
        description = "Skips the current song",
        canUseInDms = false
)
public class CommandSkip extends SlashCommandListener {
    @Override
    protected void onCommand(SlashCommandInteractionEvent command) {
        VoiceState memberVoiceState = command.getMember().getVoiceState();
        if (memberVoiceState == null) {
            command.reply("You are not in a voice channel!").run();
            return;
        }

        String channelId = memberVoiceState.channelId();
        if (channelId == null) {
            command.reply("You are not in a voice channel!").run();
            return;
        }

        ServerMusicManager manager = ServerMusicRegistry.get(command.getGuild().id(), channelId);
        manager.getScheduler().skip();

        List<AudioTrack> queue = manager.getScheduler().getQueue();
        if (queue.isEmpty()) {
            command.reply("Skipped the current song. Queue is now empty!").run();
            return;
        }

        AudioTrack track = queue.get(0);
        Embeder currentSong = Embeder.e()
                .title("Now Playing: **" + track.getInfo().title + "**")
                .field("Duration", track.getInfo().isStream ? ":red_circle: Live Stream" : String.valueOf(track.getDuration()), true)
                .field("Author", track.getInfo().author, true)
                .field("URL", track.getInfo().uri, true)
                .field("Position in Queue", "1", true);

        command.replyWithEmbeds(currentSong).run();
    }
}
