package Core;

import Core.Api.*;
import Core.Api.Common.Interfaces.ApiScript;
import Core.Api.Common.LootLogger;
import Core.Api.Common.StatTracker;
import Core.Api.Common.Utility;
import org.osbot.rs07.Bot;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;

/**
 * This class is the parent to:
 *  - osBot
 *  - API
 * As such it connects and wraps all the classes together so they can communicate
 *
 *  The Antiban is ran as a thread as is the Camera so that they run concurrent/parallel to the script execution
 */

public class API {

    public Script osbot;
    public API api;

    public ApiScript script;
    public Antiban antiban;
    public Camera camera;

    public Bot bot;
    public MethodProvider mp;

    public MyPlayer myPlayer;
    public Interact interact;
    public Fighter fighter;
    public Utility util;
    public Banking banking;
    public StatTracker stats;
    public LootLogger logger;
    public static Miner miner;

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

    public API(Script osbot) {
        osbot.log("CLIENT: initiating...");
        this.osbot = osbot;
        antiban = new Antiban(this);
        camera = new Camera(this);
        osbot.log("CLIENT: initiated...");

        bot = osbot.bot;
        antiban = antiban;
        camera = camera;

        mp = bot.getMethods();
        myPlayer = new MyPlayer(this);
        interact = new Interact(this);
        fighter = new Fighter(this);
        banking = new Banking(this);
        util = new Utility(this);
        stats = new StatTracker(this);
        logger = new LootLogger(this);
        miner = new Miner(this);
    }

    public enum ScriptState{
        IDLE,
        ACTIVE
    }

    public void setScript(ApiScript script) {
        this.script = script;
    }

    public void run() {
        if(!antiban.idle) {
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
