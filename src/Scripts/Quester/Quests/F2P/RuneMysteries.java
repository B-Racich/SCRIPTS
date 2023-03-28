package Scripts.Quester.Quests.F2P;

import Core.API;
import Core.Api.Common.Interfaces.ApiScript;
import Core.Api.Common.Interfaces.Quest;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;

import java.awt.*;

public class RuneMysteries implements ApiScript, Quest {

    public final static String scriptName = "Romeo And Juliet";

    private API api;

    private long timeBegan;
    private long timeRan;

    public RuneMysteries(API api) {
        this.api = api;
    }

    public final int quest_id = 63;
    public int quest_state;

    public boolean isCompleted() {
        return api.mp.getQuests().isComplete(Quests.Quest.RUNE_MYSTERIES);
    }

    enum state {}

    @Override
    public void run() throws NullPointerException {
        try {
            quest();
        } catch (Exception e) {
            api.log("SCRIPT: Exception: ");
            api.log(e.getMessage());
            e.printStackTrace();
        }
    }

    private Area Dukes_Room = new Area(new Position(3208,3220,1), new Position(3210,3223,1));
    private Area Wizard_Basment = new Area(new Position(3097,9570,0), new Position(3107,9571,0));
    private Area Aubury_Hut = new Area(new Position(3252,3399,0), new Position(3254,3402,0));

    private void quest() {
        quest_state = api.mp.getConfigs().get(quest_id);

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
    public void shutdown() {

    }

    @Override
    public void paint(Graphics2D g) {

    }

    @Override
    public API.ScriptState getState() {
        return API.ScriptState.IDLE;
    }
}
