package com.afg.tess.init;

import de.btobastian.javacord.DiscordApi;

import java.util.Random;

/**
 * Created by AFlyingGrayson on 9/7/17
 */
public class Tess
{
	public static Random rand = new Random();
	public static DiscordApi api;
//	public static DiscordAPI arcApi;
	public static String playerDataFolderPath = "tessData/players";
	public static String locationFolderPath = "tessData/locations";

	public static void main(String[] args) { Main.main(); }

}
