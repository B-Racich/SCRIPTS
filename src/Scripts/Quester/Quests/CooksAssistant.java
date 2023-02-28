package Scripts.Quester.Quests;

import Core.API;
import Core.Api.Common.ApiScript;
import Core.Api.Common.Timing;
import Core.Client;
import Scripts.Quester.Quester;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;

import java.awt.*;
import java.util.HashMap;

public class CooksAssistant implements ApiScript {

    public final static String scriptName = "Cooks Assistant";

    private Client client;
    private API api;

    private long timeBegan;
    private long timeRan;

    public CooksAssistant(Client client) {
        this.client = client;
        api = client.api;
    }

    enum state {}

    @Override
    public void run() {
        try {
           quest();
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
    public CooksAssistant.state getState() {
        return null;
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
    private boolean has_all_items = true;

    public final int quest_id = 29;
    private int quest_state;

    public boolean isCompleted() {
        if(api.mp.getQuests().isComplete(Quests.Quest.COOKS_ASSISTANT)) return true;
        else return false;
    }

    private void quest() {
        quest_state = api.mp.getConfigs().get(quest_id);

        client.log("Cook's Assistant: " + quest_id + " - " + quest_state);
        switch (quest_state) {
            case 0:
                if (!Lumbridge_Cook.contains(api.mp.myPlayer())) {
                    api.myPlayer.moveTo(Lumbridge_Cook);
                } else {
                    api.interact.talkNPC("Cook", new int[]{1, 1});
                }
            case 1:
                if ((!api.myPlayer.hasItem("Pot of flour") || !api.myPlayer.hasItem("Bucket of milk") || !api.myPlayer.hasItem("Egg")) && !has_all_items) {
                    // Get Pot
                    if (!api.myPlayer.hasItem("Pot") && !api.myPlayer.hasItem("Pot of flour")) {
                        client.log("Getting Pot");
                        if (!Lumbridge_Cook.contains(api.mp.myPlayer())) {
                            api.myPlayer.moveTo(Lumbridge_Cook);
                        } else {
                            api.interact.pickUpItem("Pot");
                        }
                    }
                    // Get Bucket
                    else if (!api.myPlayer.hasItem("Bucket") && !api.myPlayer.hasItem("Bucket of milk")) {
                        client.log("Getting Bucket");
                        if (!Lumbridge_Cellar.contains(api.mp.myPlayer())) {
                            api.myPlayer.moveTo(Lumbridge_Cellar);
                        } else {
                            api.interact.pickUpItem("Bucket");
                        }
                    }
                    // Get Flour
                    else if (api.myPlayer.hasItem("Pot") && !api.myPlayer.hasItem("Pot of flour")) {
                        client.log("Getting Flour");
                        if (ground_flour) {
                            client.log("here: distance: " + Flour_Bin.distance(api.mp.myPosition()));
                            if (api.myPlayer.isWithin(Flour_Bin, 3)) {
                                api.interact.interactOb("Flour bin", "Empty");
                            } else {
                                api.myPlayer.moveTo(Flour_Bin);
                            }
                        } else if (filled_hopper) {
                            if (api.myPlayer.isWithin(Hopper_Controls, 5)) {
                                api.interact.interactOb("Hopper controls", "Operate");
                                Timing.waitCondition(() -> !api.mp.myPlayer().isAnimating(), 2000);
                                ground_flour = true;
                            } else {
                                api.myPlayer.moveTo(Hopper_Controls);
                            }
                        } else if (api.myPlayer.hasItem("Grain") && !filled_hopper) {
                            if (api.myPlayer.isWithin(Hopper_Controls, 5)) {
                                api.interact.interactOb("Hopper", "Fill");
                                Timing.waitCondition(() -> !api.myPlayer.hasItem("Grain"), 2000);
                                filled_hopper = true;
                            } else {
                                api.myPlayer.moveTo(Hopper_Controls);
                            }
                        } else {
                            if (api.myPlayer.isWithin(Wheat_Field, 3)) {
                                api.interact.interactOb("Wheat", "Pick");
                            } else {
                                api.myPlayer.moveTo(Wheat_Field);
                            }
                        }
                    }
                    // Get Milk
                    else if (api.myPlayer.hasItem("Bucket") && !api.myPlayer.hasItem("Bucket of milk")) {
                        client.log("Getting Milk");
                        if (api.myPlayer.isWithin(Dairy_Cow, 1)) {
                            api.interact.interactOb("Dairy cow", "Milk", "Bucket of milk");
                        } else {
                            api.myPlayer.moveTo(Dairy_Cow);
                        }
                    } else if (!api.myPlayer.hasItem("Egg")) {
                        client.log("Getting Egg");
                        if (api.myPlayer.isWithin(Chicken_Coup, 5)) {
                            api.interact.pickUpItem("Egg");
                        } else {
                            api.myPlayer.moveTo(Chicken_Coup);
                        }
                    }
                } else if ((api.myPlayer.hasItem("Pot of flour") && api.myPlayer.hasItem("Bucket of milk") && api.myPlayer.hasItem("Egg")) || has_all_items) {
                    client.log("Turn in");
                    has_all_items = true;
                    if (Lumbridge_Cook.contains(api.mp.myPlayer())) {
                        api.interact.talkNPC("Cook");
                    } else {
                        api.myPlayer.moveTo(Lumbridge_Cook);
                    }
                }
            case 2:
                client.log("Cook's Assistant Finished!");
        }
    }

}
