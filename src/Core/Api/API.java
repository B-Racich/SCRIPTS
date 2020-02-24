package Core.Api;

import org.osbot.rs07.Bot;
import org.osbot.rs07.script.MethodProvider;

public class API {

    public Bot bot;
    public MethodProvider mp;

    public MyPlayer myPlayer;
    public Enemy enemy;
    public Utility util;
    public Banking banking;
    public StatTracker stats;
    public LootLogger logger;
    public Miner miner;

    public API(Bot bot) {
        this.bot = bot;
        mp = bot.getMethods();
        myPlayer = new MyPlayer(this);
        enemy = new Enemy(this);
        banking = new Banking(this);
        util = new Utility(this);
        stats = new StatTracker(this);
        logger = new LootLogger(this);
        miner = new Miner(this);
    }

}
