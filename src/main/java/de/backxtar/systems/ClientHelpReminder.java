package de.backxtar.systems;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroupClient;
import de.backxtar.Config;
import de.backxtar.DerGeraet;

import java.util.ArrayList;
import java.util.List;

public class ClientHelpReminder {
    private static final TS3Api api = DerGeraet.getInstance().api;

    public static void doSupport(ClientMovedEvent e) {
        Client client = api.getClientInfo(e.getClientId());
        if (!Config.getConfigData().supportChannels.contains(e.getTargetChannelId())) return;

        for (int serverGroup : client.getServerGroups()) {
            if (Config.getConfigData().supportGroups.contains(serverGroup))
                return;
        }
        ChannelInfo channelInfo = api.getChannelInfo(e.getTargetChannelId());
        List<Client> clients = new ArrayList<>();

        for (int i = 0; i < Config.getConfigData().supportGroups.size(); i++) {
            List<ServerGroupClient> sClients = api.getServerGroupClients(Config.getConfigData().supportGroups.get(i));
            for (ServerGroupClient sClient : sClients) {
                if(api.isClientOnline(sClient.getUniqueIdentifier())) {
                    Client supporter = api.getClientByUId(sClient.getUniqueIdentifier());
                    if (!clients.contains(supporter))
                        clients.add(supporter);
                }
            }
        }
        String sendHelp = clients.size() > 0 ? "Momentan " + (clients.size() > 1 ? "sind" : "ist") +
                        " [color=" + Config.getColors().mainColor + "][b]" + clients.size() + " Supporter[/b][/color] online! " +
                        "Es wird sich sofort jemand um Dich kümmern!" :
                        "Momentan ist leider [color=" + Config.getColors().mainColor + "][b]kein Supporter[/b][/color] online. " +
                        "Bitte komme zu einem späteren Zeitpunkt wieder!";
        api.sendPrivateMessage(client.getId(), sendHelp);

        clients.parallelStream().forEach(supporter -> api.sendPrivateMessage(supporter.getId(),
                "[color=" + Config.getColors().mainColor + "][b]" + client.getNickname() + "[/b][/color] " +
                "wartet in [color=" + Config.getColors().mainColor + "][b]" + channelInfo.getName() + "[/b][/color] auf Hilfe!"));
    }
}
