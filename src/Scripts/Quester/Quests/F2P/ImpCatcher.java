package Scripts.Quester.Quests.F2P;

import Core.API;
import Core.Api.Common.Interfaces.ApiScript;
import Core.Api.Common.Interfaces.Quest;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.map.Position;

import java.awt.*;

public class ImpCatcher implements ApiScript, Quest {

    public final static String scriptName = "Imp Catcher";

    private API api;

    private long timeBegan;
    private long timeRan;

    public ImpCatcher(API api) {
        this.api = api;
    }

    public final int quest_id = 160;
    public int quest_state;

    public boolean isCompleted() {
        return api.mp.getQuests().isComplete(Quests.Quest.IMP_CATCHER);
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

    private Position Wizard_Tower = new Position(3104,3163,2);
    private Position Imp_Area = new Position(3006,3319,0);

    public String[] neededBeads() {
        String[] beads;
        int beadCount = 0;
        boolean red, yellow, white, black = white = yellow = red = false;

        if(!api.myPlayer.hasItem("Red bead")) {red = true; beadCount++;}
        if(!api.myPlayer.hasItem("Yellow bead")) {yellow = true; beadCount++;}
        if(!api.myPlayer.hasItem("White bead")) {white = true; beadCount++;}
        if(!api.myPlayer.hasItem("Black bead")) {black = true; beadCount++;}

        beads = new String[beadCount];
        int beadIndex = 0;
        if(red) beads[beadIndex++] = "Red bead";
        if(yellow) beads[beadIndex++] = "Yellow bead";
        if(white) beads[beadIndex++] = "White bead";
        if(black) beads[beadIndex++] = "Black bead";

        return beads;
    }

    public void quest() {
        quest_state = api.mp.getConfigs().get(quest_id);
        api.log("Here: "+neededBeads().length);
        switch(quest_state) {
            case 0:
                if(neededBeads().length > 0) {
                    api.log("Move");
                    api.interact.moveToPosAnd(Imp_Area,20,()->{
                        api.log("Fight");
                           api.fighter.setEnemy("Imp");
                           api.fighter.setLootables(neededBeads());
                           api.fighter.fight();
                       return true;
                    });
                }
                else {
                    api.log("Move to wizard");
                    api.interact.moveToPosAnd(Wizard_Tower,5,()->api.interact.talkNPC("Wizard Mizgog",new int[]{1,1}));
                }
                break;
            case 1:
                api.interact.moveToPosAnd(Wizard_Tower,5,()->api.interact.talkNPC("Wizard Mizgog"));
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
