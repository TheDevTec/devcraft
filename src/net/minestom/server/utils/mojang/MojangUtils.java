package net.minestom.server.utils.mojang;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minestom.server.utils.url.URLUtils;

/**
 * Utils class using mojang API.
 */
public final class MojangUtils {

    public static JsonObject fromUuid(String uuid) {
        final String url = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false";
        try {
            final String response = URLUtils.getText(url);
            return JsonParser.parseString(response).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonObject fromUsername(String username) {
        final String url = "https://api.mojang.com/users/profiles/minecraft/" + username;
        try {
            // Retrieve the mojang uuid from the name
            final String response = URLUtils.getText(url);
            return JsonParser.parseString(response).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
