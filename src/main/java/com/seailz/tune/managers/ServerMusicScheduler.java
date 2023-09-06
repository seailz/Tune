package com.seailz.tune.managers;

import com.seailz.discordjar.voice.model.provider.VoiceProvider;
import com.seailz.tune.Tune;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import java.util.ArrayList;
import java.util.List;

public class ServerMusicScheduler {

    private final String guildId;
    private final String channelId;
    private final AudioPlayer player;
    private final List<AudioTrack> queue = new ArrayList<>();
    private final VoiceProvider vp;
    private AudioTrack currentTrack;
    private AudioFrame lastFrame;
    private boolean isLooping = false;

    public ServerMusicScheduler(String guildId, AudioPlayer player, String channelId) {
        this.guildId = guildId;
        this.player = player;
        this.channelId = channelId;

        vp = new VoiceProvider() {
            @Override
            public byte[] provide20ms() {
                return lastFrame.getData();
            }

            @Override
            public boolean canProvide() {
                lastFrame = player.provide();
                return lastFrame != null;
            }
        };


        new Thread(() -> {
            while (true) {
                if (player.getPlayingTrack() == null) {
                    if (queue.size() > 0) {
                        player.playTrack(queue.get(0));
                        currentTrack = queue.get(0);
                        queue.remove(0);
                    }
                }
            }
        }).start();
    }

    public void connect() {
        Tune.getDjar()
                .getVoiceChannelById(channelId)
                .connect(vp);
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public AudioTrack getCurrentTrack() {
        return currentTrack;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public List<AudioTrack> getQueue() {
        return queue;
    }

    public void setLooping(boolean isLooping) {
        this.isLooping = isLooping;
    }

    public void setVolume(int vol) {
        player.setVolume(vol);
    }

    public void queue(AudioTrack track) {
        if (player.getPlayingTrack() == null) {
            player.playTrack(track);
            currentTrack = track;
        } else {
            queue.add(track);
        }
    }

    public void skip() {
        if (queue.size() > 0) {
            player.playTrack(queue.get(0));
            currentTrack = queue.get(0);
            queue.remove(0);
        } else {
            player.stopTrack();
            currentTrack = null;
        }
    }
}
