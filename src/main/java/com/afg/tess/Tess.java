package com.afg.tess;

import org.javacord.api.DiscordApi;

import java.util.Random;

/**
 * Created by AFlyingGrayson on 9/7/17
 */
public class Tess
{
	public static Random rand = new Random();
	public static DiscordApi api;
	public static String playerDataFolderPath = "tessData/arp/players";
	public static String factionDataFolderPath = "tessData/arp/factions";
	public static String factionListFilePath = "tessData/arp/factionList";
	public static String playerListFilePath = "tessData/arp/playerList";

	public static void main(String[] args)
	{
		Main.main(PrivateTokens.INSTANCE.getAPI());
	}
}
