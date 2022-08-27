package Core;

import Core.Api.Common.ApiScript;
import org.osbot.rs07.script.Script;

/**
 * This class is the parent to:
 *  - osBot
 *  - API
 *  - script
 *  - Antiban
 *  - Camera
 * As such it connects and wraps all the classes together so they can communicate
 *
 *  The Antiban is ran as a thread as is the Camera so that they run concurrent/parallel to the script execution
 */

public class Client {

    public Script osbot;
    public API api;

    public ApiScript script;
    public Antiban antiban;
    public Camera camera;

    enum Debug {
        ALL,
        BANKING,
        COOKING,
        ENEMY,
        FIGHTER,
        MINER,
        MYPLAYER,
        ANTIBAN,
        CAMERA,
        SCRIPT,
        NONE
    }

    private Debug debugLevel = Debug.NONE;

    public Client(Script osbot) {
        osbot.log("CLIENT: initiating...");
        this.osbot = osbot;
        api = new API(this);
        antiban = new Antiban(this);
        camera = new Camera(this);
        osbot.log("CLIENT: initiated...");
    }

    public void setScript(ApiScript script) {
        this.script = script;
    }

    public void run() {
        if(!antiban.idle && !script.completedTask()) {
            try {
                script.run();
            } catch(Exception e){
                osbot.log("CLIENT: Exception: ");
                osbot.log(e.getMessage());
                shutdown();
            }
        }
    }

    public void pause() {
        script.pause();
    }

    public void runAntiban() {
        if(!antiban.isAlive()) {
            antiban.setup();
            antiban.start();
            osbot.log("CLIENT: Antiban initiated...");
        }
    }

    public void pauseAntiban() {
        antiban.pause();
    }

    public void shutdown() {
        antiban.runTime = false;
        antiban.shutdown();
        api.fighter.shutdown();
        api.myPlayer.shutdown();
        osbot.log("CLIENT: shutdown...");
    }

    public void log(String str) {
        osbot.log(str);
    }

}
