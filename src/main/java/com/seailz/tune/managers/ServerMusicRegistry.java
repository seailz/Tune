package com.seailz.tune.managers;

import java.util.HashMap;

public class ServerMusicRegistry {

    private final static HashMap<String, ServerMusicManager> registry = new HashMap<>();

    public static ServerMusicManager get(String guildId, String channelId) {
        ServerMusicManager manager = registry.get(guildId);
        if (manager == null) register(guildId, channelId);
        return registry.get(guildId);
    }

    public static void register(String guildId, String channelId) {
        registry.put(guildId, new ServerMusicManager(guildId, channelId));
    }

}
