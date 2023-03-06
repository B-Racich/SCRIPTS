package Scripts.Quester.Quests.F2P;

import Core.API;
import Core.Api.Common.Interfaces.ApiScript;
import Core.Api.Common.Timing;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.EquipmentSlot;

import java.awt.*;

public class RestlessGhost implements ApiScript {

    public final static String scriptName = "Restless Ghost";

    private API api;

    private long timeBegan;
    private long timeRan;

    public RestlessGhost(API api) {
        this.api = api;
    }

    public boolean isCompleted() {
        if(api.mp.getQuests().isComplete(Quests.Quest.THE_RESTLESS_GHOST)) return true;
        else return false;
    }

    enum state {}

    public final int quest_id = 107;
    private int quest_state;

    private Area Church_Area = new Area(3243,3208,3244,3211);
    private Area Swamp_Area = new Area(3146,3173,3149,3175);
    private Area Spooky_Area = new Area(3247,3194,3248,3191);
    private Area Wizard_Area = new Area(new Position(3097,9570,0), new Position(3107,9571,0));
    private Area Skull_Area = new Area(new Position(3117,9565,0), new Position(3119,9566,0));

    private boolean firstRun = true;
    private boolean ghostSpeak = false;

    private boolean talkToGhost(int[] dialogue) {
        if(api.mp.getObjects().closest("Coffin").hasAction("Search"))
            api.interact.interactOb("Coffin","Search");
        else
            api.interact.interactOb("Coffin","Open");
        Timing.wait(2000);
        if(dialogue != null)
            api.interact.talkNPC("Restless ghost", dialogue);
        else
            api.interact.talkNPC("Restless ghost");
        return true;
    }

    @Override
    public void run() throws NullPointerException {
        quest_state = api.mp.getConfigs().get(quest_id);

        api.log("Restless Ghost: " + quest_id + " - " + quest_state);
        switch (quest_state) {
            case 0:
                api.interact.moveToAreaAnd(Church_Area, ()-> api.interact.talkNPC("Father Aereck", new int[]{3,1}));
                break;
            case 1:
                api.interact.moveToAreaAnd(Swamp_Area, ()-> api.interact.talkNPC("Father Urhney", new int[]{2,1}));
                break;
            case 2:
                if(!api.myPlayer.hasEquipped(EquipmentSlot.AMULET, "Ghostspeak amulet"))
                    api.myPlayer.equip("Ghostspeak amulet");
                api.interact.moveToAreaAnd(Spooky_Area, ()-> talkToGhost(new int[]{1}));
                break;
            case 3:
                api.interact.moveToAreaAnd(Skull_Area,()->  api.interact.interactOb("Altar", "Search"));
                break;
            case 4:
                api.interact.moveToAreaAnd(Spooky_Area, () -> talkToGhost(null));
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
    public <state> state getState() {
        return null;
    }
}
