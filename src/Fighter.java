import Core.Api.*;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@ScriptManifest(name = "Hill Giants V2", author = "Bones", version = 1.0, info = "", logo = "")
public class Fighter extends Script  {

    private API api;
    private Enemy enemy;
    private Enemy.state enemyState;
    private MyPlayer myPlayer;
    private Utility util;

    private String foodType = "Salmon";
    private int foodAmount = 3;

    private HashMap<String, Integer> keep = new HashMap<String, Integer>() {{put("Brass Key", 1);}};
    private HashMap<String, Integer> withdraw = new HashMap<String, Integer>() {{put("Salmon", 3);}};

    private Area hillGiants = new Area(new Position(3095,9824,0), new Position(3125,9855,0));

    private long timeBegan;
    private long timeRan;

    @Override
    public void onStart() {
        //Code here will execute before the loop is started
        api = new API(this.bot);
        timeBegan = System.currentTimeMillis();
    }

    @Override
    public void onExit() {
        //Code here will execute after the script ends

    }

    @Override
    public int onLoop() throws InterruptedException {
        if(myPlayer.shouldBank()) {
            if(Banks.EDGEVILLE.contains(myPlayer())) {
                api.banking.bank(keep, withdraw, Banking.methods.DEPOSIT_ALL_EXCEPT_WITHDRAW);
            } else {
                api.myPlayer.moveTo(Banks.EDGEVILLE);
            }
        } else if(myPlayer.ableToFight() && myPlayer.inArea(hillGiants)) {
            enemyState = enemy.action();
        } else if(!myPlayer.inArea(hillGiants)){
            myPlayer.moveTo(hillGiants);
        }

        return random(250,500); //The amount of time in milliseconds before the loop starts over

    }

    @Override
    public void onPaint(Graphics2D g) {
        //This is where you will put your code for paint(s)
        timeRan = System.currentTimeMillis() - this.timeBegan;
        g.drawString(ft(timeRan), 1, 1);
    }

    private String ft(long duration) {
        String res = "";
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                .toHours(duration));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                .toMinutes(duration));
        if (days == 0) {
            res = (hours + ":" + minutes + ":" + seconds);
        } else {
            res = (days + ":" + hours + ":" + minutes + ":" + seconds);
        }
        return res;
    }

}