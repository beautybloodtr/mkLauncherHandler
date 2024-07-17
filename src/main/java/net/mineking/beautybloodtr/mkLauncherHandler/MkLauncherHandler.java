package net.mineking.beautybloodtr.mkLauncherHandler;

import org.bukkit.plugin.java.JavaPlugin;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;
import java.util.logging.Level;
public final class MkLauncherHandler extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                URL url = new URL("https://auth.mineking.net/online");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    Set<String> playerNames = new Gson().fromJson(response.toString(), new TypeToken<Set<String>>() {}.getType());

                    if (!playerNames.contains(playerName)) {
                        Bukkit.getScheduler().runTask(this, () -> {
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "iatexture " + playerName);
                        });
                    }
                }
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "An error occurred while fetching the player list", e);
            }
        });
    }

}
