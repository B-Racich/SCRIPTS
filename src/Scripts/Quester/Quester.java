package Scripts.Quester;

import Core.API;
import Core.Api.Common.ApiScript;
import Core.Client;
import Scripts.Quester.Quests.*;

import java.awt.*;
import java.util.HashMap;

/**
 * The basic script template
 *
 * state - can be used to setup a state based script, otherwise create a regular onLoop script within the run() method
 *
 * pause() - if setup can be used by other classes to pause the script when needed
 *
 * if tasks are supported complete the task methods
 */

public class Quester implements ApiScript {

    public final static String scriptName = "Quester";

    private Client client;
    private API api;

    private CooksAssistant cooksAssistant;
    private WitchesPotion witchesPotion;
    private SheepShearer sheepShearer;
    private RomeoAndJuliet romeoAndJuliet;
    private RuneMysteries runeMysteries;

    public Quester(Client client) {
        this.client = client;
        api = client.api;

        cooksAssistant = new CooksAssistant(client);
        witchesPotion = new WitchesPotion(client);
        sheepShearer = new SheepShearer(client);
        romeoAndJuliet = new RomeoAndJuliet(client);
        runeMysteries = new RuneMysteries(client);
    }

    enum state {}

    @Override
    public void run() {
        try {
            if(!cooksAssistant.isCompleted()) cooksAssistant.run();
            if(!witchesPotion.isCompleted()) witchesPotion.run();
            if(!sheepShearer.isCompleted()) sheepShearer.run();
            if(!romeoAndJuliet.isCompleted()) romeoAndJuliet.run();
            if(!runeMysteries.isCompleted()) runeMysteries.run();
        } catch (Exception e) {
            client.log("SCRIPT: Exception: ");
            client.log(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void paint(Graphics2D g) {

    }

    @Override
    public void setTask(HashMap<String, Integer> tasks) {

    }

    @Override
    public boolean hasTask() {
        return false;
    }

    @Override
    public boolean completedTask() {
        return false;
    }

    @Override
    public state getState() {
        return null;
    }
}
