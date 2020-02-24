import Core.Client;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;
import java.util.HashMap;


@ScriptManifest(name = "AIO Launcher", author = "Bones", version = 1.0, info = "", logo = "")
public class AIOLauncher extends Script {

    private Client client;
    private boolean firstRun = true;

    @Override
    public void onStart() {
        //Code here will execute before the loop is started
    }

    @Override
    public void onExit() {
        //Code here will execute after the script ends
        client.shutdown();
    }

    @Override
    public int onLoop() throws InterruptedException {
        if(firstRun) {
            client = new Client(this);
            RuneCrafter script = new RuneCrafter(client);
            script.altar = RuneCrafter.Altar.AIR;
            HashMap<String, Integer> tasks = new HashMap<String, Integer>() {{put(script.altar.rune, 50);}};
            script.setTask(tasks);
            client.setScript(script);
            client.runAntiban();
            client.api.util.debug = true;
            firstRun = false;
        }
        client.run();
        return random(250,500); //The amount of time in milliseconds before the loop starts over

    }

    @Override
    public void onPaint(Graphics2D g) {
        client.script.paint(g);
    }

}
