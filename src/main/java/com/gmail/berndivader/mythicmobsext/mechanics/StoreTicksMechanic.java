package com.gmail.berndivader.mythicmobsext.mechanics;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.INoTargetSkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;

@ExternalAnnotation(name="storetick,storeservertick",author="BerndiVader")
public class StoreTicksMechanic 
extends 
SkillMechanic 
implements
INoTargetSkill {
	boolean bl1;
	String s1;

	public StoreTicksMechanic(String skill, MythicLineConfig mlc) {
		super(skill, mlc);
		bl1=mlc.getBoolean("meta",false);
		s1=mlc.getString("tag",null);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean cast(SkillMetadata data) {
		int i1=NMSUtils.getCurrentTick(data.getCaster().getEntity().getBukkitEntity().getServer());
		if (!bl1) {
			Scoreboard sb=Bukkit.getServer().getScoreboardManager().getMainScoreboard();
			Objective obj=sb.getObjective(s1);
			if (obj==null) obj=sb.registerNewObjective(s1,"emtpy");
			Score score=null;
			if (data.getCaster().getEntity().isPlayer()) {
				score=obj.getScore((OfflinePlayer)data.getCaster().getEntity().getBukkitEntity());
			} else {
				score=obj.getScore(data.getCaster().getEntity().getUniqueId().toString());
			}
			score.setScore(i1);
		} else {
			data.getCaster().getEntity().getBukkitEntity().setMetadata(Utils.parseMobVariables(s1,data,data.getCaster().getEntity(),null,null),new FixedMetadataValue(Main.getPlugin(),i1));
		}
		return true;
	}
}
