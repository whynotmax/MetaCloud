package eu.metacloudservice.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import eu.metacloudservice.CloudAPI;
import eu.metacloudservice.Driver;
import eu.metacloudservice.config.Motd;
import eu.metacloudservice.velocity.VeloCityBootstrap;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public class MOTDListener {

    @Subscribe
    public void handel(ProxyPingEvent event){
        ServerPing.Builder ping = event.getPing().asBuilder();
        if (VeloCityBootstrap.getInstance().configuration == null){
            return;
        }else if (!VeloCityBootstrap.getInstance().configuration.isMotdEnabled()){
            return;
        }else {
            VeloCityBootstrap bungeeBootstrap = VeloCityBootstrap.getInstance();
            CloudAPI cloudAPI = CloudAPI.getInstance();
            int motdIndex = bungeeBootstrap.motdCount;
            Motd motd = bungeeBootstrap.group.isMaintenance() ? bungeeBootstrap.configuration.getMaintenancen().get(motdIndex) : bungeeBootstrap.configuration.getDefaults().get(motdIndex);

            String protocol = motd.getProtocol() != null && !motd.getProtocol().isEmpty() ?
           motd.getProtocol()
                            .replace("&", "§")
                            .replace("%proxy_name%", bungeeBootstrap.getLiveService().getService())
                            .replace("%proxy_node%", bungeeBootstrap.getLiveService().getRunningNode())
                            .replace("%online_players%", "" + cloudAPI.getPlayerPool().getPlayers().size())
                            .replace("%max_players%", "" + bungeeBootstrap.group.getMaxPlayers()):
                    "§7" + cloudAPI.getPlayerPool().getPlayers().size() + "/" + bungeeBootstrap.group.getMaxPlayers();


            String firstLine = motd.getFirstline()
                    .replace("&", "§")
                    .replace("%proxy_name%", bungeeBootstrap.getLiveService().getService())
                    .replace("%proxy_node%", bungeeBootstrap.getLiveService().getRunningNode())
                    .replace("%proxy_group%", bungeeBootstrap.getLiveService().getGroup())
                    .replace("%online_players%", "" + cloudAPI.getPlayerPool().getPlayers().size())
                    .replace("%max_players%", "" + bungeeBootstrap.group.getMaxPlayers());

            String secondLine = motd.getSecondline()
                    .replace("&", "§")
                    .replace("%proxy_name%", bungeeBootstrap.getLiveService().getService())
                    .replace("%proxy_node%", bungeeBootstrap.getLiveService().getRunningNode())
                    .replace("%proxy_group%", bungeeBootstrap.getLiveService().getGroup())
                    .replace("%online_players%", "" + cloudAPI.getPlayerPool().getPlayers().size())
                    .replace("%max_players%", "" + bungeeBootstrap.group.getMaxPlayers());

            String description = firstLine + "\n" + secondLine;




    String[] players = motd.getPlayerinfos().stream()
                    .map(info -> info.replace("&", "§"))
                    .toArray(String[]::new);


            ServerPing.SamplePlayer[] samplePlayers = new ServerPing.SamplePlayer[players == null ? 0 : players.length];
            if (players != null) {
                for (int i = 0; i < samplePlayers.length; i++) {
                    samplePlayers[i] = new ServerPing.SamplePlayer(players[i], UUID.randomUUID());
                }
            }

            int cloudPlayersplayers = cloudAPI.getPlayerPool().getPlayers().size();
            int max = cloudAPI.getGroupPool().getGroup(cloudAPI.getCurrentService().getGroup()).getMaxPlayers();
            ping
                    .description(Component.text(description))
                    .maximumPlayers(max)
                    .onlinePlayers(cloudPlayersplayers)
                    .build();

            if (players != null) {
                ping.clearSamplePlayers().samplePlayers(samplePlayers);
            }

            if (protocol != null) {
                ping.version(new ServerPing.Version(1, protocol));
            }

            event.setPing(ping.build());

    }
    }
}
