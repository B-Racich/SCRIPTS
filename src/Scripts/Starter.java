package Scripts;

import Core.API;
import Core.Api.Common.Utility;
import Core.Api.Enemy;
import Core.Api.MyPlayer;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;
import java.util.concurrent.TimeUnit;


@ScriptManifest(name = "Scripts.Starter", author = "Bones", version = 1.0, info = "", logo = "")
public class Starter extends Script {

    private API api;
    private Enemy enemy;
    private Enemy.state enemyState;
    private MyPlayer myPlayer;
    private Utility util;

    private long timeBegan;
    private long timeRan;

    private boolean firstRun = true;

    @Override
    public void onStart() {
        //Code here will execute before the loop is started
//        api = new API(this);
        timeBegan = System.currentTimeMillis();
    }

    @Override
    public void onExit() {
        //Code here will execute after the script ends

    }

    @Override
    public int onLoop() throws InterruptedException {
        if(firstRun)
            setup();
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

    private void setup() {

    }

}
