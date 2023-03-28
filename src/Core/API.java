package Core;

import Core.Api.*;
import Core.Api.Antiban;
import Core.Api.Common.Interfaces.ApiScript;
import Core.Api.Common.LootLogger;
import Core.Api.Common.StatTracker;
import Core.Api.Common.Timing;
import Core.Api.Common.Utility;
import Core.Api.Modules.*;
import org.osbot.rs07.Bot;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;

import java.io.PrintWriter;
import java.io.StringWriter;

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
    public Miner miner;

    public Thread antibanThread;

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
        osbot.log("API: initiating...");

        this.osbot = osbot;
        bot = osbot.bot;
        mp = bot.getMethods();
        antiban = new Antiban(this);
        camera = new Camera(this);

        myPlayer = new MyPlayer(this);
        interact = new Interact(this);
        fighter = new Fighter(this);
        banking = new Banking(this);
        util = new Utility(this);
        stats = new StatTracker(this);
        logger = new LootLogger(this);
        miner = new Miner(this);

        osbot.log("API: initiated...");
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
                osbot.log("API: STARTS==========:");
                osbot.log(e.getMessage());
                osbot.log(e.getStackTrace());
                osbot.log("API: ENDS============:");
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
            osbot.log("API: Antiban initiated...");
        }
    }

    public void pauseAntiban() {
        antiban.pause();
    }

    public void shutdown() {
        try {
            antiban.runTime = false;
            script.shutdown();
            antiban.interrupt();
            antiban.shutdown();
            fighter.tracker.shutdown();
            myPlayer.tracker.shutdown();
            fighter.shutdown();
            myPlayer.shutdown();
            antiban = null;
            fighter = null;
            myPlayer = null;
            osbot.log("API: shutdown...");
        } catch(Exception e) {
            api.osbot.log(e.getStackTrace());
        }
    }

    public void log(String str) {
        osbot.log(str);
    }

}
