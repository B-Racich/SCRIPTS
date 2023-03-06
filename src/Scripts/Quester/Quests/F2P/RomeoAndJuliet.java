package Scripts.Quester.Quests.F2P;

import Core.API;
import Core.Api.Common.Interfaces.ApiScript;
import Core.Api.Common.Interfaces.Quest;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;

import java.awt.*;

public class RomeoAndJuliet implements ApiScript, Quest {

    public final static String scriptName = "Romeo And Juliet";

    private API api;

    private long timeBegan;
    private long timeRan;

    public RomeoAndJuliet(API api) {
        this.api = api;
    }

    public final int quest_id = 144;
    public int quest_state;

    public boolean isCompleted() {
        if(api.mp.getQuests().isComplete(Quests.Quest.ROMEO_JULIET)) return true;
        else return false;
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

    private Area Romeo_Area = new Area(3211,3423,3214,3425);
    private Area Juliet_Area = new Area(new Position(3157,3425,1), new Position(3159,3426,1));
    private Area Chapel_Area = new Area(3253,3479,3255,3481);
    private Area Apoth_Area = new Area(3195,3402,3197,3404);
    private Area Berry_Area = new Area(3273,3369,3277,3372);

    private boolean gave_berries = false;
    private boolean gave_potion = false;

    private void quest() {
        quest_state = api.mp.getConfigs().get(quest_id);

        switch(quest_state) {
            case 0:
                api.interact.moveToPosAnd(Romeo_Area.getCentralPosition(), 15,()->api.interact.talkNPC("Romeo", new int[]{1,1}));
                break;
            case 10:
                api.interact.moveToPosAnd(Juliet_Area.getCentralPosition(), 15,()->api.interact.talkNPC("Juliet", new int[]{1,1}));
                break;
            case 20:
                api.interact.moveToPosAnd(Romeo_Area.getCentralPosition(), 15,()->api.interact.talkNPC("Romeo", new int[]{4}));
                break;
            case 30:
                api.interact.moveToPosAnd(Chapel_Area.getCentralPosition(),20,()->api.interact.talkNPC("Father Lawrence"));
                break;
            case 40:
            case 50:
                if(!api.myPlayer.hasItem("Cadava berries") && !api.myPlayer.hasItem("Cadava potion") && !gave_berries && !gave_potion) {
                    api.interact.moveToPosAnd(Berry_Area.getCentralPosition(),10,()->api.interact.interactOb("Cadava bush", "Pick-from"));
                }
                else if(api.myPlayer.hasItem("Cadava berries") && !api.myPlayer.hasItem("Cadava potion") && !gave_berries && !gave_potion){
                    api.interact.moveToPosAnd(Apoth_Area.getCentralPosition(),10,()->api.interact.talkNPC("Apothecary",new int[]{2,1}));
                    gave_berries = true;
                    api.interact.moveToPosAnd(Apoth_Area.getCentralPosition(),10,()->api.interact.talkNPC("Apothecary",new int[]{2,2}));
                }
                else if(!api.myPlayer.hasItem("Cadava berries") && !api.myPlayer.hasItem("Cadava potion") && gave_berries && !gave_potion) {
                    api.interact.moveToPosAnd(Apoth_Area.getCentralPosition(),10,()->api.interact.talkNPC("Apothecary",new int[]{2,1}));
                }
                else if(api.myPlayer.hasItem("cadava potion") && !gave_potion) {
                    api.interact.moveToPosAnd(Juliet_Area.getCentralPosition(), 15,()->api.interact.talkNPC("Juliet"));
                    gave_potion = true;
                }
                else if(gave_potion) {
                    if(api.interact.isTalking()) {
                        api.interact.clickContinue();
                    }
                }
                break;
            case 60:
                if(api.interact.isTalking()) {
                    api.interact.clickContinue();
                }
                api.interact.moveToPosAnd(Romeo_Area.getCentralPosition(), 15,()->api.interact.talkNPC("Romeo"));
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
    public API.ScriptState getState() {
        return API.ScriptState.IDLE;
    }
}
