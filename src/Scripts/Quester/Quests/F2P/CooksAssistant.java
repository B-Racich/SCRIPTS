package Scripts.Quester.Quests.F2P;

import Core.API;
import Core.Api.Common.Interfaces.ApiScript;
import Core.Api.Common.Interfaces.Quest;
import Core.Api.Common.Timing;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;

import java.awt.*;

public class CooksAssistant implements ApiScript, Quest {

    public final static String scriptName = "Cooks Assistant";

    private API api;

    private long timeBegan;
    private long timeRan;

    public CooksAssistant(API api) {
        this.api = api.api;
    }

    enum state {}

    @Override
    public void run() {
        try {
           quest();
        } catch (Exception e) {
            api.log("SCRIPT: Exception: ");
            api.log(e.getMessage());
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
    public API.ScriptState getState() {
        return API.ScriptState.IDLE;
    }

    private Area Lumbridge_Cook = new Area(new Position(3205,3212,0), new Position(3212,3217,0));
    private Area Lumbridge_Cellar = new Area(new Position(3208,9615,0), new Position(3219,9626,0));
    // Milk -Dairy cow
    private Position Dairy_Cow = new Position(3255,3272,0);
    // Take - Egg
    private Position Chicken_Coup = new Position(3230,3298,0);
    // Pick - Wheat
    private Position Wheat_Field = new Position(3161,3295,0);
    // Fill - Hopper, Operate - Hopper controls
    private Position Hopper_Controls = new Position(3165,3306,2);
    // Empty - Flour bin
    private Position Flour_Bin = new Position(3166,3305,0);

    private boolean ground_flour = false;
    private boolean filled_hopper = false;
    private boolean has_all_items = false;

    public final int quest_id = 29;
    private int quest_state;

    public boolean isCompleted() {
        if(api.mp.getQuests().isComplete(Quests.Quest.COOKS_ASSISTANT)) return true;
        else return false;
    }

    private void quest() {
        quest_state = api.mp.getConfigs().get(quest_id);

        api.log("Cook's Assistant: " + quest_id + " - " + quest_state);
        switch (quest_state) {
            case 0:
                api.interact.moveToAreaAnd(Lumbridge_Cook,()->api.interact.talkNPC("Cook", new int[]{1,1}));
                break;
            case 1:
                if ((!api.myPlayer.hasItem("Pot of flour") || !api.myPlayer.hasItem("Bucket of milk") || !api.myPlayer.hasItem("Egg")) && !has_all_items) {
                    // Get Pot
                    if (!api.myPlayer.hasItem("Pot") && !api.myPlayer.hasItem("Pot of flour")) {
                        api.log("Getting Pot");
                        api.interact.moveToAreaAnd(Lumbridge_Cook,()->api.interact.pickUpItem("Pot"));
                    }
                    // Get Bucket
                    else if (!api.myPlayer.hasItem("Bucket") && !api.myPlayer.hasItem("Bucket of milk")) {
                        api.log("Getting Bucket");
                        api.interact.moveToAreaAnd(Lumbridge_Cellar,()->api.interact.pickUpItem("Bucket"));
                    }
                    // Get Flour
                    else if (api.myPlayer.hasItem("Pot") && !api.myPlayer.hasItem("Pot of flour")) {
                        api.log("Getting Flour");
                        if (ground_flour) {
                            api.log("here: distance: " + Flour_Bin.distance(api.mp.myPosition()));
                            api.interact.moveToPosAnd(Flour_Bin,3,()-> api.interact.interactOb("Flour bin", "Empty"));
                        } else if (filled_hopper) {
                            api.interact.moveToPosAnd(Hopper_Controls,5,()-> {
                                api.interact.interactOb("Hopper controls", "Operate");
                                Timing.waitCondition(() -> !api.mp.myPlayer().isAnimating(), 2000);
                                ground_flour = true;
                                return true;
                            });
                        } else if (api.myPlayer.hasItem("Grain") && !filled_hopper) {
                            api.interact.moveToPosAnd(Hopper_Controls,5,()-> {
                                api.interact.interactOb("Hopper", "Fill");
                                Timing.waitCondition(() -> !api.myPlayer.hasItem("Grain"), 2000);
                                filled_hopper = true;
                                return true;
                            });
                        } else {
                            api.interact.moveToPosAnd(Wheat_Field,3,()->api.interact.interactOb("Wheat", "Pick"));
                        }
                    }
                    // Get Milk
                    else if (api.myPlayer.hasItem("Bucket") && !api.myPlayer.hasItem("Bucket of milk")) {
                        api.log("Getting Milk");
                        api.interact.moveToPosAnd(Dairy_Cow,5,()->api.interact.interactOb("Dairy cow", "Milk", "Bucket of milk"));
                    } else if (!api.myPlayer.hasItem("Egg")) {
                        api.log("Getting Egg");
                        api.interact.moveToPosAnd(Chicken_Coup,10,()->api.interact.pickUpItem("Egg"));
                    }
                } else if ((api.myPlayer.hasItem("Pot of flour") && api.myPlayer.hasItem("Bucket of milk") && api.myPlayer.hasItem("Egg")) || has_all_items) {
                    api.log("Turn in");
                    has_all_items = true;
                    api.interact.moveToAreaAnd(Lumbridge_Cook,()-> api.interact.talkNPC("Cook"));
                }
                break;
            case 2:
                api.log("Cook's Assistant Finished!");
        }
    }

}
