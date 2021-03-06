package com.gmail.berndivader.mythicmobsext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.berndivader.MythicPlayers.MythicPlayers;
import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import com.gmail.berndivader.mythicmobsext.conditions.factions.FactionsFlags;
import com.gmail.berndivader.mythicmobsext.cachedowners.CachedOwnerHandler;
import com.gmail.berndivader.mythicmobsext.conditions.CustomConditions;
import com.gmail.berndivader.mythicmobsext.conditions.factions.FactionsFlagConditions;
import com.gmail.berndivader.mythicmobsext.conditions.mobarena.MobArenaConditions;
import com.gmail.berndivader.mythicmobsext.conditions.worldguard.WorldGuardFlags;
import com.gmail.berndivader.mythicmobsext.config.Config;
import com.gmail.berndivader.mythicmobsext.externals.Externals;
import com.gmail.berndivader.mythicmobsext.externals.Internals;
import com.gmail.berndivader.mythicmobsext.mechanics.CustomMechanics;
import com.gmail.berndivader.mythicmobsext.healthbar.HealthbarHandler;
import com.gmail.berndivader.mythicmobsext.javascript.JavaScript;
import com.gmail.berndivader.mythicmobsext.targeters.CustomTargeters;
import com.gmail.berndivader.mythicmobsext.thiefs.Thiefs;
import com.gmail.berndivader.mythicmobsext.conditions.worldguard.WorldGuardFlag;
import com.gmail.berndivader.mythicmobsext.nanpatch.NaNpatch;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

public class Main extends JavaPlugin {
	private static Main plugin;
	public static HealthbarHandler healthbarhandler;
	public static CachedOwnerHandler cachedOwnerHandler;
	public static Random random;
	public static Integer wgVer;
	public static WorldGuardFlags wgf;
	public static FactionsFlags fflags;
	public static boolean hasRpgItems = false;
	public static Logger logger;
	public static PluginManager pluginmanager;
	public static boolean slappyNewBorn = true;
	private static MythicPlayers mythicplayers;
	public static HashSet<Entity>entityCache=new HashSet<Entity>();
	public static boolean disguisepresent;
	public Thiefs thiefs;
	
	public Internals internals;
	public Externals externals;

	public void onEnable() {
		plugin = this;
		random = new Random();
		pluginmanager = plugin.getServer().getPluginManager();
		logger = plugin.getLogger();
		
		if(Utils.serverV<10) {
			logger.warning("******************************************************");
			logger.warning("     This version of MythicMobsExtension is only");
			logger.warning("     supported on server versions 1.10 to 1.12.");
			logger.warning("     We cant garantie that it runs properly.");
			logger.warning("******************************************************");
		}

		if (Config.update) {
			String version = null;
			PluginDescriptionFile pdf = getDescription();
			try {
				URL url = new URL("https://raw.githubusercontent.com/BerndiVader/MythicMobsExtension/master/version.txt");
				try (InputStream in=url.openStream();
				BufferedReader br=new BufferedReader( new InputStreamReader(in))) {
					version=br.readLine().toString();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				logger.warning("Could not read version file!");
			}
			if (!pdf.getVersion().endsWith("SNAPSHOT")&&!pdf.getVersion().equals(version)) {
				logger.info("MythicMobsExtension v"+version+" is available, get it here:");
				logger.info("https://www.spigotmc.org/resources/mythicmobsextension.51884/");
			} else {
				logger.info("Plugin is up-to-date!");
			}
		}
		if (pluginmanager.isPluginEnabled("MythicMobs")) {
			new Utils();
			internals=new Internals();
			if (Config.externals) {
				externals=new Externals();
				logger.info("enabled externals");
			}
			new CustomMechanics();
			logger.info("registered mechanics!");
			new CustomConditions();
			logger.info("registered conditions!");
			new CustomTargeters();
			logger.info("registered targeters!");
			if (Config.javascript) {
				new JavaScript();
				logger.info("enabled javascript!");
			}
			if (Config.m_players) {
				Main.mythicplayers=new MythicPlayers(this);
				logger.info("registered mythicplayers!");
			}
			if (Config.m_thiefs) {
				thiefs=new Thiefs();
				logger.info("registered thiefs!");
			}
			if (Config.nan) {
				new NaNpatch();
				logger.info("NaN patch applied!");
			}
			if (Config.wguard&&pluginmanager.getPlugin("WorldGuard")!=null) {
				wgf = new WorldGuardFlags();
				new WorldGuardFlag();
				logger.info("Worldguard support enabled!");
			}
			if (Config.factions&&pluginmanager.getPlugin("Factions")!=null&&pluginmanager.getPlugin("MassiveCore")!=null) {
				fflags = new FactionsFlags();
				new FactionsFlagConditions();
				logger.info("Faction support enabled!");
			}
			if (Config.rpgitems&&pluginmanager.getPlugin("RPGItems") != null) {
				logger.info("RPGItems support enabled!");
				hasRpgItems = true;
			}
			if (Config.mobarena&&pluginmanager.isPluginEnabled("MobArena")) {
				new MobArenaConditions();
				logger.info("MobArena support enabled!");
			}
			if (Config.h_displays&&pluginmanager.isPluginEnabled("HolographicDisplays")) {
				Main.healthbarhandler=new HealthbarHandler(this);
				logger.info("HolographicDisplays support enabled!");
			}
			Main.disguisepresent=pluginmanager.isPluginEnabled("LibsDisguise");
			if (Config.c_owners) {
				cachedOwnerHandler = new CachedOwnerHandler(plugin);
				logger.info("CachedOwner support enabled!");
			}
			logger.info("External mechanics, conditions, targeters loaded!");
			
	        new BukkitRunnable() {
				@Override
				public void run() {
					Utils.mythicmobs.getRandomSpawningManager().reload();
				}
			}.runTask(this);
			new BukkitRunnable() {
				@Override
				public void run() {
					for (Iterator<Entity>it=Main.entityCache.iterator();it.hasNext();) {
						Entity entity=it.next();
						if (entity!=null) {
							if (NMSUtils.getEntity(entity.getWorld(),entity.getUniqueId())==null) it.remove();
						} else {
							it.remove();
						}
					}
				}
			}.runTaskTimerAsynchronously(this,600L,600L);
		}
	}

	@Override
	public void onDisable() {
		for(Iterator<Entity>a=entityCache.iterator();a.hasNext();) {
			Entity e=null;
			if ((e=a.next())!=null) e.remove();
		}
		if (healthbarhandler!=null) {
			Main.healthbarhandler.removeHealthbars();
			Main.healthbarhandler.removeSpeechBubbles();
		}
		if (Main.cachedOwnerHandler!=null) CachedOwnerHandler.saveCachedOwners();
		Main.mythicplayers = null;
		Main.cachedOwnerHandler = null;
		Main.wgf = null;
		Main.fflags = null;
		pluginmanager.disablePlugin(this);
	}

	public static Main getPlugin() {
		return plugin;
	}

	public MythicPlayers getMythicPlayers() {
		return Main.mythicplayers;
	}

}
