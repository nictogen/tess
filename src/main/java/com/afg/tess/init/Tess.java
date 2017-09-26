package com.afg.tess.init;

import de.btobastian.javacord.DiscordAPI;

import java.util.Random;

/**
 * Created by AFlyingGrayson on 9/7/17
 */
public class Tess
{
	public static Random rand = new Random();
	public static DiscordAPI api;
	public static DiscordAPI arcApi;
	public static String playerDataFolderPath = "tessData/arp/players";
	public static String playerListFilePath = "tessData/arp/playerList";

	public static void main(String[] args) { Main.main(); }

}
