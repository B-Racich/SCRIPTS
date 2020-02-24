package Core;

import Core.Api.Common.Timing;
import org.osbot.rs07.api.model.*;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.MethodProvider;

import java.util.List;

import static org.osbot.rs07.script.MethodProvider.random;

public class Antiban extends Thread {

    private Client client;
    private MethodProvider mp;
    private long last;
    private long time;
    private long sleepTime;
    private long checkTime =  5;
    private long lastAfk = 0;
    public boolean runTime = false;
    public boolean idle;

    public Antiban(Client client) {
        this.client = client;
        mp = client.osBot.getBot().getMethods();
    }

    public void run() {
        while(runTime) {
            if(check())
                activate();
            try {
                sleep(sleepTime);
            } catch (InterruptedException e) {
                client.api.util.log(e.toString());
            }
        }
        client.api.util.log("Antiban: Shutting down");
    }

    public void pause() {

    }

    public void setup() {
        time = System.currentTimeMillis();
        last = time;
        runTime = true;
    }

    public boolean check() {
        time = System.currentTimeMillis();
        long diff = (time - last)/1000;
        client.api.util.log("Antiban: check "+diff+" : "+checkTime);
        sleepTime = ((checkTime-diff)*1000 >= 500) ? (checkTime-diff)*1000 : 500;
        if(diff >= checkTime) return true;
        else return false;
    }

    public void activate() {
        checkTime = random(15,90);
        sleepTime = 500;
        last = System.currentTimeMillis();
        int rand = random(0,10);
        client.api.util.log("Antiban: Activate : "+rand);
        idle = true;
        if(rand <= 2)
            examineObject();
        else if(rand <= 5)
            examineNpc();
        else if(rand <= 7)
            moveMouseOutside();
        else
            openTab();
        idle = false;
    }

    public void shutdown() {
        this.interrupt();
    }

    public void moveMouseOutside() {
        client.api.util.log("Antiban: Examine : " + "Move outside screen");
        mp.getMouse().moveOutsideScreen();
        try {
            sleep(random(500,1500));
        } catch (InterruptedException e) {
            client.api.util.log(e.toString());
        }
    }

    public void examineObject() {
        List<RS2Object> obs = mp.getObjects().filter(ob -> ob.getPosition().distance(mp.myPosition()) <= 5);
        if(obs != null && !obs.isEmpty()) {
            RS2Object en = obs.get(0);
            if(en != null) {
                client.api.util.log("Antiban: Examine : " + en.getName());
                client.camera.lookAt(en);
                Timing.waitCondition(en::isVisible, 250, 1000);
                if (en.isVisible()) {
                    en.interact("Examine");
                }
            }
        }
    }

    public void examineNpc() {
        List<NPC> obs = mp.getNpcs().filter(ob -> ob.getPosition().distance(mp.myPosition()) <= 5);
        if(obs != null && !obs.isEmpty()) {
            NPC en = obs.get(0);
            if(en != null) {
                client.api.util.log("Antiban: Examine : " + en.getName());
                client.camera.lookAt(en);
                Timing.waitCondition(en::isVisible, 250, 1000);
                if (en.isVisible()) {
                    en.interact("Examine");
                }
            }
        }
    }

    public void openTab() {
        int tab_index = random(0,Tab.values().length-1);
        int cur = 0;
        for (Tab tab : Tab.values()) {
            if(tab_index == cur) {
                client.api.util.log("Antiban: Open Tab - "+tab);
                mp.getTabs().open(tab);
                Timing.waitCondition(() -> mp.getTabs().isOpen(tab),250, 1000);
            }
            cur++;
        }
    }

}
