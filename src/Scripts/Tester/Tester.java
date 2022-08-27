package Scripts.Tester;

import Core.API;
import Core.Api.Common.ApiScript;
import Core.Client;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.Skill;

import java.awt.*;
import java.util.HashMap;

/**
 * The basic script template
 *
 * state - can be used to setup a state based script, otherwise create a regular onLoop script within the run() method
 *
 * pause() - if setup can be used by other classes to pause the script when needed
 *
 * if tasks are supported complete the task methods
 */

public class Tester implements ApiScript {

    public final static String scriptName = "Miner Yeet";

    private Client client;
    private API api;

    private long timeBegan;
    private long timeRan;

    public Tester(Client client) {
        this.client = client;
        api = client.api;
    }

    enum state {}

    private int[] combat_stats = new int[7];
    private int cmb_lvl;

    private void getStats() {
        combat_stats[0] = api.mp.getSkills().getStatic(Skill.HITPOINTS);
        combat_stats[1] = api.mp.getSkills().getStatic(Skill.ATTACK);
        combat_stats[2] = api.mp.getSkills().getStatic(Skill.STRENGTH);
        combat_stats[3] = api.mp.getSkills().getStatic(Skill.DEFENCE);
        combat_stats[4] = api.mp.getSkills().getStatic(Skill.PRAYER);
        combat_stats[5] = api.mp.getSkills().getStatic(Skill.RANGED);
        combat_stats[6] = api.mp.getSkills().getStatic(Skill.MAGIC);

        cmb_lvl = api.mp.myPlayer().getCombatLevel();
    }

    private String cmb_zone;
    private Position lumbridge_goblins = new Position(3247,3237,0);

    private Position fight_area;
    private void getZone() {
        getStats();

        if(cmb_lvl <= 15) {
            cmb_zone = "noob_mobs";
        }

        switch(cmb_zone) {
            case "noob_mobs":
                api.fighter.setEnemy("Goblin");
                fight_area = lumbridge_goblins;
                break;
        }
    }

    @Override
    public void run() {
        try {
            getZone();
            if(api.myPlayer.ableToFight() && api.myPlayer.isWithin(fight_area, 30)) {
                client.osbot.log("Fight");
                api.fighter.fight();
            } else if(!api.myPlayer.isWithin(fight_area, 30)){
                client.osbot.log("Move");
                api.myPlayer.moveTo(fight_area);
            }
        } catch(NullPointerException e) {
            client.osbot.log("Oopsie");
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void paint(Graphics2D g) {

    }

    @Override
    public void setTask(HashMap<String, Integer> tasks) {

    }

    @Override
    public boolean hasTask() {
        return false;
    }

    @Override
    public boolean completedTask() {
        return false;
    }

    @Override
    public state getState() {
        return null;
    }
}
