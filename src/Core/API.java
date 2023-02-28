package Core;

import Core.Antiban;
import Core.Api.*;
import Core.Api.Common.LootLogger;
import Core.Api.Common.StatTracker;
import Core.Api.Common.Utility;
import Core.Camera;
import Core.Client;
import org.osbot.rs07.Bot;
import org.osbot.rs07.script.MethodProvider;

/**
 * The API class houses each class of functionality so that any component may utilize its siblings functionality
 *
 * It also provides a nice way to use the different components through one entry point
 */

public class API {

    public Client client;
    public Antiban antiban;
    public Camera camera;

    public Bot bot;
    public MethodProvider mp;

    public MyPlayer myPlayer;
    public Interact interact;
    public Enemy enemy;
    public Fighter fighter;
    public Utility util;
    public Banking banking;
    public StatTracker stats;
    public LootLogger logger;
    public Miner miner;

    public API(Client client) {
        this.client = client;

        bot = client.osbot.bot;
        antiban = client.antiban;
        camera = client.camera;

        mp = bot.getMethods();
        myPlayer = new MyPlayer(this);
        interact = new Interact(this);
        enemy = new Enemy(this);
        fighter = new Fighter(this);
        banking = new Banking(this);
        util = new Utility(this);
        stats = new StatTracker(this);
        logger = new LootLogger(this);
        miner = new Miner(this);
    }

}
