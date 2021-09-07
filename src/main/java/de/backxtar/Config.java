package de.backxtar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private static ConfigData configData;
    private static File file;

    public static class ConfigData {
        public String ts3Host;
        public String ts3Username;
        public String ts3Password;
        public String ts3Nickname = "Der Geraet (Bot)";
        public String dbHost;
        public String dbName;
        public String dbUser;
        public String dbPassword;
        public String prefix = "!";
        public int welcomeMessage = 0;
        public int tradingPostChannelID = 0;
        public int[] afkChannelID;
        public int infoChannelID = 0;
        public int guildChannelID = 0;
        public String[] guildRanks;
        private int[] tempServerGroups;
        public HashMap<String, Integer> serverGroups;
        public List<Integer> ignoreGroups;
        public int arcDpsChannelID = 0;
        public int dailiesChannelID = 0;
        public int tempServerGroupID = 0;
        public String guildID;
        public String guildLeaderApiKey;
    }

    public static void loadConfig() throws IOException {
        Properties cfg = new Properties();
        boolean isLive = true;

        if (isLive) file = new File("config.cfg");
        else file = new File("config_test.cfg");

        if (file.createNewFile())
            logger.info("New config created.");
        else if (!file.createNewFile() && file.length() == 0) {
            logger.info("config.cfg is empty!");
            return;
        }
        InputStreamReader streamReader = new InputStreamReader(new FileInputStream(file.getName()), StandardCharsets.UTF_8);
        cfg.load(streamReader);
        Enumeration<Object> en = cfg.keys();
        configData = new ConfigData();

        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            String value;
            String[] values;

            switch (key) {
                // Teamspeak3 data
                case "ts3Host": configData.ts3Host = (String) cfg.get(key);
                    break;
                case "ts3Username": configData.ts3Username = (String) cfg.get(key);
                    break;
                case "ts3Password": configData.ts3Password = (String) cfg.get(key);
                    break;
                case "ts3Nickname":
                    if (cfg.get(key) != null) configData.ts3Nickname = (String) cfg.get(key);
                    else configData.ts3Nickname = "Der Geraet (Bot)";
                    break;
                // Database data
                case "dbHost": configData.dbHost = (String) cfg.get(key);
                    break;
                case "dbName": configData.dbName = (String) cfg.get(key);
                    break;
                case "dbUser": configData.dbUser = (String) cfg.get(key);
                    break;
                case "dbPassword": configData.dbPassword = (String) cfg.get(key);
                    break;
                // Settings
                case "prefix": configData.prefix = (String) cfg.get(key);
                    break;
                case "afkChannelID":
                    value = (String) cfg.get(key);
                    values = value.split(",");
                    if (values.length == 0 || values[0].equalsIgnoreCase("0")) {
                        configData.afkChannelID = new int[1];
                    } else {
                        configData.afkChannelID = new int[values.length];
                        for (int i = 0; i < configData.afkChannelID.length; i++) {
                            configData.afkChannelID[i] = Integer.parseInt(values[i]);
                        }
                    } break;
                case "infoChannelID": value = (String) cfg.get(key);
                    configData.infoChannelID = Integer.parseInt(value);
                    break;
                case "guildChannelID": value = (String) cfg.get(key);
                    configData.guildChannelID = Integer.parseInt(value);
                    break;
                case "guildRanks": value = (String) cfg.get(key);
                    values = value.split(",");
                    if (values.length == 0) {
                        configData.guildRanks = new String[1];
                        configData.guildRanks[0] = "0";
                    } else {
                        configData.guildRanks = new String[values.length];
                        System.arraycopy(values, 0, configData.guildRanks, 0, configData.guildRanks.length);
                    } break;
                case "serverGroups" : value = (String) cfg.get(key);
                    values = value.split(",");
                    configData.tempServerGroups = new int[values.length];
                    for (int i = 0; i < values.length; i++)
                        configData.tempServerGroups[i] = Integer.parseInt(values[i]);
                    break;
                case "ignoreGroups" : value = (String) cfg.get(key);
                    values = value.split(",");
                    configData.ignoreGroups = new ArrayList<>();
                    for (String str : values)
                        configData.ignoreGroups.add(Integer.parseInt(str));
                    break;
                case "arcDpsChannelID": value = (String) cfg.get(key);
                    configData.arcDpsChannelID = Integer.parseInt(value);
                    break;
                case "tradingPostChannelID": value = (String) cfg.get(key);
                    configData.tradingPostChannelID = Integer.parseInt(value);
                    break;
                case "dailiesChannelID": value = (String) cfg.get(key);
                    configData.dailiesChannelID = Integer.parseInt(value);
                    break;
                case "welcomeMessage": value = (String) cfg.get(key);
                    configData.welcomeMessage = Integer.parseInt(value);
                    break;
                case "tempFriendID": value = (String) cfg.get(key);
                    configData.tempServerGroupID = Integer.parseInt(value);
                    break;
                case "guildID": configData.guildID = (String) cfg.get(key);
                    break;
                case "guildLeaderApiKey": configData.guildLeaderApiKey = (String) cfg.get(key);
                    break;
            }
        }
        if (configData.tempServerGroups.length != configData.guildRanks.length)
            throw new IOException("Number of guildRanks are not equal to number of serverGroups!");
        if (configData.guildRanks.length == 1 && configData.guildRanks[0].equalsIgnoreCase("0"))
            configData.serverGroups = null;

        configData.serverGroups = new HashMap<>();
        for (int i = 0; i < configData.tempServerGroups.length; i++)
            configData.serverGroups.put(configData.guildRanks[i], configData.tempServerGroups[i]);
    }

    public static ConfigData getConfigData() {
        return configData;
    }

    public static File getFile() {
        return file;
    }
}
