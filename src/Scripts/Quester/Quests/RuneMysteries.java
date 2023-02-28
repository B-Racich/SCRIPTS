package Scripts.Quester.Quests;

import Core.API;
import Core.Api.Common.ApiScript;
import Core.Client;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;

import java.awt.*;
import java.util.HashMap;

public class RuneMysteries implements ApiScript {

    public final static String scriptName = "Romeo And Juliet";

    private Client client;
    private API api;

    private long timeBegan;
    private long timeRan;

    public RuneMysteries(Client client) {
        this.client = client;
        api = client.api;
    }

    public final int quest_id = 63;

    public boolean isCompleted() {
        if(api.mp.getQuests().isComplete(Quests.Quest.RUNE_MYSTERIES)) return true;
        else return false;
    }

    enum state {}

    @Override
    public void run() throws NullPointerException {
        try {
            quest();
        } catch (Exception e) {
            client.log("SCRIPT: Exception: ");
            client.log(e.getMessage());
            e.printStackTrace();
        }
    }

    private Area Dukes_Room = new Area(new Position(3208,3220,1), new Position(3210,3223,1));
    private Area Wizard_Basment = new Area(new Position(3097,9570,0), new Position(3107,9571,0));
    private Area Aubury_Hut = new Area(new Position(3252,3399,0), new Position(3254,3402,0));

    private void quest() {
        int quest_state = api.mp.getConfigs().get(quest_id);

        switch(quest_state) {
            case 0:
                api.interact.moveToAreaAnd(Dukes_Room,()->api.interact.talkNPC("Duke Horacio", new int[]{1,1,1}));
                break;
            case 1:
            case 2:
                api.interact.moveToAreaAnd(Wizard_Basment,()->api.interact.talkNPC("Archmage Sedridor", new int[]{3,1,1}));
                break;
            case 3:
                api.interact.moveToAreaAnd(Aubury_Hut,()->api.interact.talkNPC("Aubury", new int[]{2}));
                api.interact.moveToAreaAnd(Aubury_Hut,()->api.interact.talkNPC("Aubury"));
                break;
            case 5:
                api.interact.moveToAreaAnd(Wizard_Basment,()->api.interact.talkNPC("Archmage Sedridor", new int[]{2}));
                break;
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
    public <state> state getState() {
        return null;
    }
}
