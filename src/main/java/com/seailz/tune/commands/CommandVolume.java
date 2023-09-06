package com.seailz.tune.commands;

import com.seailz.discordjar.command.CommandOption;
import com.seailz.discordjar.command.CommandOptionType;
import com.seailz.discordjar.command.annotation.SlashCommandInfo;
import com.seailz.discordjar.command.listeners.slash.SlashCommandListener;
import com.seailz.discordjar.events.model.interaction.command.SlashCommandInteractionEvent;
import com.seailz.discordjar.voice.model.VoiceState;
import com.seailz.tune.managers.ServerMusicManager;
import com.seailz.tune.managers.ServerMusicRegistry;

@SlashCommandInfo(
        name = "volume",
        description = "Change the volume of the bot",
        canUseInDms = false
)
public class CommandVolume extends SlashCommandListener {

    public CommandVolume() {
        addOption(new CommandOption(
                "volume",
                "The volume to set",
                CommandOptionType.INTEGER,
                true
        ));
    }

    @Override
    protected void onCommand(SlashCommandInteractionEvent command) {
        int vol = command.getOption("volume").getAsInt();
        if (vol < 0 || vol > 100) {
            command.getHandler().followup("Volume must be between 0 and 100!").run();
            return;
        }

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
        manager.getScheduler().setVolume(vol);
        command.reply("Volume set to " + vol + "%").run();
    }
}
