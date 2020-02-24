package Core;

import Core.Api.API;
import org.osbot.rs07.script.Script;

public class Client {

    public Script osBot;
    public API api;

    public ApiScript script;
    public Antiban antiban;
    public Camera camera;

    public Client(Script osBot) {
        this.osBot = osBot;
        api = new API(osBot.bot);
        antiban = new Antiban(this);
        camera = new Camera(this);
    }

    public void setScript(ApiScript script) {
        this.script = script;
    }

    public void run() {
        if(!antiban.idle && !script.completedTask()) {
            script.run();
        }
    }

    public void pause() {
        script.pause();
    }

    public void runAntiban() {
        if(!antiban.isAlive()) {
            antiban.setup();
            antiban.start();
        }
    }

    public void pauseAntiban() {
        antiban.pause();
    }

    public void shutdown() {
        antiban.runTime = false;
        antiban.shutdown();
    }

}
