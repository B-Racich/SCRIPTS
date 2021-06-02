package Core.Api.Common;

import Core.API;
import org.osbot.rs07.Bot;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class StatTracker {

    private API api;
    private Bot bot;
    private MethodProvider mp;

    private HashMap<String, Integer> startLvls = new HashMap<>();

    public StatTracker(API api) {
        this.api = api;
        bot = api.bot;
        mp = bot.getMethods();
    }

    public void track(Skill skill) {
        mp.getSkills().experienceTracker.start(skill);
        startLvls.put(skill.name(), mp.getSkills().getStatic(skill));
    }

    public String getTTL(Skill skill) {
        long millis = mp.getSkills().experienceTracker.getTimeToLevel(skill);
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        return hms;
    }

    public int getXp(Skill skill) {
        return mp.getSkills().experienceTracker.getGainedXP(skill);
    }

    public int getXpHr(Skill skill) {
        return mp.getSkills().experienceTracker.getGainedXPPerHour(skill);
    }

    public int getLvls(Skill skill) {
        return mp.getSkills().experienceTracker.getGainedLevels(skill);
    }

    public String print(Skill skill) {
        return skill.name()+": "+mp.getSkills().getStatic(skill)+" ("+getLvls(skill)+")     XpHr: "+getXpHr(skill)+"     TTL: "+getTTL(skill);
    }

}
