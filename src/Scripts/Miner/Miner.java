package Scripts.Miner;

import Core.API;
import Core.Api.Common.ApiScript;
import Core.Client;
import org.osbot.rs07.Bot;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.ui.Skill;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The basic script template
 *
 * state - can be used to setup a state based script, otherwise create a regular onLoop script within the run() method
 *
 * pause() - if setup can be used by other classes to pause the script when needed
 *
 * if tasks are supported complete the task methods
 */

public class Miner implements ApiScript {

    public final static String scriptName = "Miner";

    private Client client;
    private API api;

    private long timeBegan;
    private long timeRan;

    public Miner(Client client) {
        this.client = client;
        api = client.api;
    }

    enum state {}

    private int[] combat_stats = new int[7];
    private int[] skill_stats = new int[7];
    private int mining_lvl = 0;
    private int cmb_lvl;

    private void getStats() {
        combat_stats[0] = api.mp.getSkills().getStatic(Skill.HITPOINTS);
        combat_stats[1] = api.mp.getSkills().getStatic(Skill.ATTACK);
        combat_stats[2] = api.mp.getSkills().getStatic(Skill.STRENGTH);
        combat_stats[3] = api.mp.getSkills().getStatic(Skill.DEFENCE);
        combat_stats[4] = api.mp.getSkills().getStatic(Skill.PRAYER);
        combat_stats[5] = api.mp.getSkills().getStatic(Skill.RANGED);

        skill_stats[5] = api.mp.getSkills().getStatic(Skill.MINING);
        mining_lvl = skill_stats[5];

        cmb_lvl = api.mp.myPlayer().getCombatLevel();
    }

    private String cmb_zone;
    private Position lumbridge_goblins = new Position(3247,3237,0);

    private Position fight_area;

    public enum PICKAXE {
        BRONZE("Bronze pickaxe", 1, 1),
        IRON("Black pickaxe", 11, 10),
        STEEL("Iron pickaxe", 1, 1),
        BLACK("Steel pickaxe", 6, 5),
        MITHRIL("Mithril pickaxe", 21, 20),
        ADAMANT("Adamant pickaxe", 31, 30),
        RUNE("Rune pickaxe", 41, 40),
        DRAGON("Dragon pickaxe", 61, 60);

        public String name;
        public int miningLvl;
        public int attackLvl;

//        public List inInventory() {
//            HashMap<String, Integer> picks = new HashMap<String, Integer>();
//
//            PICKAXE.stream().filter(e -> e.miningLvl <= miningLvl)
//            .forEach(pick -> picks.put(pick.name,pick.miningLvl));
//
//            return picks.t();
//    }

        public static Stream<PICKAXE> stream() {
            return Stream.of(PICKAXE.values());
        }

//        public List<String> getList() {
//            List names = new ArrayList<String>() {{
//                add(BRONZE.name);
//                add(IRON.name);
//                add(STEEL.name);
//                add(BLACK.name);
//                add(MITHRIL.name);
//                add(ADAMANT.name);
//                add(RUNE.name);
//                add(DRAGON.name);
//            }};
//
//            return names;
//        }

        PICKAXE(String name, int miningLvl, int attackLvl) {
            this.name = name;
            this.miningLvl = miningLvl;
            this.attackLvl = attackLvl;
        }
    }

    private boolean hasPickInventory(String picks) {
        PICKAXE.stream()
                .filter(pick -> pick.miningLvl <= mining_lvl)
                .collect(Collectors.toList());
        return true;
    }

    private void getZone() {
        getStats();

        if(mining_lvl <= 20) {
           if(api.myPlayer.moveTo(Banks.LUMBRIDGE_LOWER)) {
//                api.miner.
           }
        }

        else if(cmb_lvl <= 15) {
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
