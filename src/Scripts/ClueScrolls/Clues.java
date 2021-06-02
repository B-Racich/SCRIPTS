package Scripts.ClueScrolls;

import Core.API;
import Core.Api.Banking;
import Core.Api.Common.ApiScript;
import Core.Api.Common.Timing;
import Core.Api.MyPlayer;
import Core.Client;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;

import java.awt.*;
import java.util.ArrayList;
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

public class Clues implements ApiScript {

    public final static String scriptName = "Clues";

    private Client client;
    private API api;
    private MethodProvider mp;
    private MyPlayer player;

    private long timeBegan;
    private long timeRan;

    private boolean buryBones = true;

    public Clues(Client client) {
        this.client = client;
        api = client.api;
        mp = api.mp;
        player = api.myPlayer;
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
        NULL,
    }

    @Override
    public state getState() {
        return state.NULL;
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


    private String clue_beg = "Clue scroll (beginner)";
    private boolean has_clue = false;
    private HashMap<String, Boolean> clue_steps = new HashMap<>();

    private void readClue() {
        if(player.hasItem(clue_beg)) {
            int clue = 162;
            RS2Widget widgets = mp.getWidgets().getWidgetContainingText(clue);
            if(widgets != null)
                mp.log(widgets);
        }
    }

    @Override
    public void run() {
        readClue();
        client.shutdown();
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
}
