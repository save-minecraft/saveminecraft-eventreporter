package kr.saveminecraft.plugin.eventreporter;

import me.alex4386.gachon.network.common.http.HttpRequest;
import me.alex4386.gachon.network.common.http.HttpRequestMethod;
import me.alex4386.gachon.network.common.http.HttpResponse;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Reporter {
    private String token;

    public Reporter(String token) {
        this.token = token;
    }

    private void addToken(HttpRequest request) {
        Map<String, String> headers = new HashMap<>();

        headers.put("Authorization", "Bearer "+this.token);
        request.addHeaders(headers);
    }

    public JSONObject buildPlayerPayload(Player player) {
        JSONObject payload = new JSONObject();

        payload.put("uuid", player.getUniqueId().toString());
        payload.put("name", player.getName());
        payload.put("metadata", this.buildMetadata(player));

        return payload;
    }

    public JSONObject buildMetadata(Player player) {
        JSONObject metadata = new JSONObject();

        return metadata;
    }

    public boolean updatePlayer(Player player) {
        return updatePlayer(player, true);
    }

    public boolean updatePlayer(Player player, boolean markAsOnline) {
        UUID uuid = player.getUniqueId();
        JSONObject payload = this.buildPlayerPayload(player);

        payload.put("isOnline", markAsOnline);

        try {
            HttpRequest request = new HttpRequest(HttpRequestMethod.PUT, new URL(Main.host + "/v1/admin/players/" + uuid), payload);
            this.addToken(request);

            HttpResponse response = request.getResponse();

            if (response.code.isOK()) {
                JSONObject json = response.toJson();
                return (boolean) json.get("success");
            } else {
                return false;
            }
        } catch (IOException | ParseException e) {
            return false;
        }
    }
}
