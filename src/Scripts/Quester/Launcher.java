package Scripts.Quester;

//import Scripts.RuneCrafter;
import Core.API;
import Core.Client;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;
import java.util.HashMap;

/**
 *  This class is the entry bot into osBot and invokes the run method of the client class.
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

    private Client client;
    private Quester script;
    private API api;

    public HashMap<String, Integer> tasks = new HashMap<String, Integer>() {
    };

    @Override
    public void onStart() {
        //Code here will execute before the loop is started
        log("LAUNCHER: Starting up...");
        client = new Client(this);
        api = client.api;
        script = new Quester(client);
        client.setScript(script);
        client.runAntiban();
        client.api.util.debug = true;
        log("LAUNCHER: Setup finished...");
    }

    @Override
    public void onExit() {
        //Code here will execute after the script ends
        client.shutdown();
    }

    @Override
    public int onLoop() throws InterruptedException {
        client.run();
        return random(250,500); //The amount of time in milliseconds before the loop starts over

    }

    @Override
    public void onPaint(Graphics2D g) {
        g.drawString("LAUNCHER: npc_name: "+api.fighter.getNpcName(), 14, 306);
        g.drawString("LAUNCHER: Player State: "+api.myPlayer.tracker.player_status, 14, 316);
        g.drawString("LAUNCHER: NpcTracker State: "+api.fighter.tracker.getStatus(), 14, 326);
        g.drawString("LAUNCHER: Fighter State: "+api.fighter.fight_state, 14, 336);
        g.drawString("LAUNCHER: Character HP %: "+api.myPlayer.tracker.my_health, 14, 296);
        client.script.paint(g);
    }

}
