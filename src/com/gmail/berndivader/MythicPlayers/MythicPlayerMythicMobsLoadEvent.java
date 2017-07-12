package com.gmail.berndivader.MythicPlayers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gmail.berndivader.MythicPlayers.Mechanics.mmCreateActivePlayer;
import com.gmail.berndivader.MythicPlayers.Mechanics.mmNormalPlayer;
import com.gmail.berndivader.MythicPlayers.Mechanics.mmSetTarget;
import com.gmail.berndivader.MythicPlayers.Targeters.mmCrosshairTargeter;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicTargeterLoadEvent;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillTargeter;

public class MythicPlayerMythicMobsLoadEvent implements Listener {
	
	private String MechName, TargeterName;
	private SkillMechanic skill;
	private SkillTargeter targeter;
	
	@EventHandler
	public void onMMSkillLoad(MythicMechanicLoadEvent e) {
		MechName = e.getMechanicName().toLowerCase();
		switch (MechName) {
		case("activeplayer"): {
			skill = new mmCreateActivePlayer(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
			break;
		}
		case("normalplayer"): {
			skill = new mmNormalPlayer(e.getContainer().getConfigLine(), e.getConfig());
			e.register(skill);
			break;
		}
		case ("settarget"): {
			skill = new mmSetTarget(e.getContainer().getConfigLine(), e.getConfig());
			e.register(skill);
		}}
	}
	
	@EventHandler
	public void onMythicMobsTargetersLoad(MythicTargeterLoadEvent e) {
		TargeterName = e.getTargeterName().toLowerCase();
		switch (TargeterName) {
		case ("crosshairentity"):
		case ("che"): {
			targeter = new mmCrosshairTargeter(e.getConfig());
			e.register(targeter);
			break;
		}}
	}
	
}
