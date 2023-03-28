package Scripts.Noober;

import Core.API;
import Core.Api.Modules.Banking;
import Core.Api.Common.Interfaces.ApiScript;
import Core.Api.Common.Timing;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;

import java.awt.*;
import java.util.HashMap;

import static Core.API.ScriptState.IDLE;

/**
 * The basic script template
 *
 * state - can be used to setup a state based script, otherwise create a regular onLoop script within the run() method
 *
 * pause() - if setup can be used by other classes to pause the script when needed
 *
 * if tasks are supported complete the task methods
 */

public class Noober implements ApiScript {

    public final static String scriptName = "Noober";

    private API api;
    private MethodProvider mp;

    private long timeBegan;
    private long timeRan;

    private boolean buryBones = true;

    public Noober(API api) {
        this.api = api;
        mp = api.mp;
    }

    enum state {
        FIGHTING,
        BANKING,
        COOKING,
        FLEEING,
        GE,
        MOVING,
        SHOP,
        CRAFTING,
        USING,
        MOVE_TO_ENEMY,
    }

    public API.ScriptState getState() {
        return IDLE;
    }

    public state getScriptState() {
        if (api.myPlayer.hasFood() && !mp.getInventory().isFull() && api.myPlayer.isWithin(fight_area, 30)) {
            return state.FIGHTING;
        } else if (!mp.getInventory().isFull() && api.myPlayer.hasFood() && !api.myPlayer.isWithin(fight_area, 30)) {
            return state.MOVE_TO_ENEMY;
        } else if (api.myPlayer.shouldEat() && !api.myPlayer.hasFood() && mp.getInventory().contains("Raw beef")) {
            return state.COOKING;
        } else if (mp.getInventory().isFull()) {
            return state.BANKING;
        }
        return state.FLEEING;
    }

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
    /**
     * TODO: Refactor into a enum to hold additional data
     */
    private Position lumbridge_bank = new Position(Banks.LUMBRIDGE_UPPER.getCentralPosition());
    private Position lumbridge_goblins = new Position(3247, 3237, 0);
    private Position lumbridge_cows = new Position(3260, 3274, 0);
    private Position lumbridge_chef = new Position(3186, 3274, 0);

    private HashMap<String, String> options = new HashMap<>();

    private Position fight_area;

    private void getZone() {
        getStats();

        if (cmb_lvl <= 15) {
            cmb_zone = "noob_mobs";
        } else if (cmb_lvl <= 25) {
            cmb_zone = "cows";
        }

        switch (cmb_zone) {
            case "noob_mobs":
                api.fighter.setEnemy("Goblin");
                fight_area = lumbridge_goblins;
                break;
            case "cows":
                api.fighter.setEnemy("Cow");
                fight_area = lumbridge_cows;
                break;
        }
    }

    @Override
    public void run() {
        try {
            getZone();

            switch (getScriptState()) {
                case BANKING:
                    if (api.myPlayer.isWithin(lumbridge_bank, 2)) {
                        HashMap<String, Integer> except = new HashMap<String, Integer>() {{
                            put("Cooked meat", 0);
                        }};
                        api.banking.bank(except, null, Banking.methods.DEPOSIT_ALL_EXCEPT);
                    } else {
                        api.myPlayer.moveTo(lumbridge_bank);
                    }
                    break;
                case COOKING:
                    if (api.myPlayer.isWithin(lumbridge_chef, 1)) {
                        while (mp.getInventory().contains("Raw beef")) {
                            mp.getInventory().interact("Use", "Raw beef");
                            if (mp.getInventory().isItemSelected()) {
                                RS2Object range = mp.getObjects().closest("Cooking pot");
                                if (range != null) {
                                    range.interact("Use");
                                    Timing.waitCondition(() -> mp.getWidgets().isVisible(270, 14, 38), 250, 2000);
                                    if (mp.getWidgets().isVisible(270, 14, 38)) {
                                        mp.getWidgets().interact(270, 14, 38, "Make");
                                        Timing.waitCondition(() -> !mp.getInventory().contains("Raw Beef"), 250, 5000);
                                        if (mp.getInventory().contains("Burnt meat")) {
                                            mp.getInventory().dropAll("Burnt meat");
                                            Timing.waitCondition(() -> !mp.getInventory().contains("Burnt meat"), 250, 2000);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        api.myPlayer.moveTo(lumbridge_chef);
                    }
                    break;
                case FIGHTING:
                    api.fighter.fight();
                    break;
                case MOVE_TO_ENEMY:
                    api.myPlayer.moveTo(fight_area);
                    break;
                default:
                    api.myPlayer.moveTo(lumbridge_bank);

            }

        } catch (NullPointerException e) {
            api.osbot.log("Oopsie " + e.toString());
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void paint(Graphics2D g) {

    }
}
