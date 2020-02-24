import Core.Api.API;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;


@ScriptManifest(name = "", author = "Bones", version = 1.0, info = "", logo = "")
public class Template extends Script {

    private API api;

    @Override
    public void onStart() {
        //Code here will execute before the loop is started
        api = new API(this.bot);
    }

    @Override
    public void onExit() {
        //Code here will execute after the script ends

    }

    @Override
    public int onLoop() throws InterruptedException {

        return random(250,500); //The amount of time in milliseconds before the loop starts over

    }

    @Override
    public void onPaint(Graphics2D g) {

    }

}
