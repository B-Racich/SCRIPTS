package Scripts.Quester;

//import Scripts.RuneCrafter;
import Core.API;
import Core.Api.Common.Timing;
import Core.Api.Common.Utility;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;
import java.util.HashMap;

/**
 *  This class is the entry bot into osBot and invokes the run method of the api class.
 *
 *  Some variables are required to be set
 *  Initialization can be done within the first run loop
 *
 *  script - holds the script to run
 *
 *  tasks - create a task list for the script to execute
 */

@ScriptManifest(name = Quester.scriptName, author = "Bones", version = 1.0, info = "", logo = "")
public class Launcher extends Script {

    private API api;

    public HashMap<String, Integer> tasks = new HashMap<String, Integer>() {
    };

    @Override
    public void onStart() {
        //Code here will execute before the loop is started
        log("LAUNCHER: Starting up...");
        api = new API(this);
        Quester script = new Quester(api);
        api.setScript(script);
        api.runAntiban();
        Utility.debug = true;
        log("LAUNCHER: Setup finished...");
    }

    @Override
    public void onExit() {
        //Code here will execute after the script ends
        api.antiban.shutdown();
        api.fighter.shutdown();
        api.myPlayer.shutdown();
        Timing.waitCondition(()->!api.antiban.isAlive(),10000);
        api.shutdown();
    }

    @Override
    public int onLoop() throws InterruptedException {
        api.run();
        return random(250,500); //The amount of time in milliseconds before the loop starts over

    }

    @Override
    public void onPaint(Graphics2D g) {
        g.drawString("LAUNCHER: npc_name: "+api.fighter.getNpcName(), 14, 306);
        g.drawString("LAUNCHER: Player State: "+api.myPlayer.tracker.player_status, 14, 316);
        g.drawString("LAUNCHER: NpcTracker State: "+api.fighter.tracker.getStatus(), 14, 326);
        g.drawString("LAUNCHER: Fighter State: "+api.fighter.fight_state, 14, 336);
        g.drawString("LAUNCHER: Character HP %: "+api.myPlayer.tracker.my_health, 14, 296);

        NPC npc = api.fighter.tracker.npc;
        Entity en = api.myPlayer.targetEn;

        if(npc != null) {
            g.draw(npc.getModel().getBoundingBox(npc.getGridX(),npc.getGridY(),npc.getZ()));
        }

        if(en != null) {
            g.draw(en.getModel().getBoundingBox(en.getGridX(),en.getGridY(),en.getZ()));
        }

        api.script.paint(g);
    }

}
