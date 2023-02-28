package Scripts.Quester.Quests;

import Core.API;
import Core.Api.Common.ApiScript;
import Core.Client;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;

import java.awt.*;
import java.util.HashMap;

public class WitchesPotion implements ApiScript {

    public final static String scriptName = "Witches Potion";

    private Client client;
    private API api;

    private long timeBegan;
    private long timeRan;

    public WitchesPotion(Client client) {
        this.client = client;
        api = client.api;
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

    Area Aggies_House = new Area(new Position(3083,3256,0), new Position(3088,3261,0));
    Area Hettys_House = new Area(new Position(2965,3203,0), new Position(2970,3208,0));
    Area Archery_Shop = new Area(new Position(2953,3202,0), new Position(2960,3205,0));
    Area Bettys_Shop = new Area(new Position(3011,3256,0), new Position(3016,3261,0));
    Position Giant_Rat = new Position(2998,3194,0);
    Position Range = new Position(3018,3238,0);
    Position Onion_Patch = new Position(2950,3252,0);

    public final int quest_id = 67;

    public boolean isCompleted() {
        if(api.mp.getQuests().isComplete(Quests.Quest.WITCHS_POTION)) return true;
        else return false;
    }

    private boolean hasItems() {
        if(api.myPlayer.hasItem("Rat's tail")
                && api.myPlayer.hasItem("Eye of newt")
                && api.myPlayer.hasItem("Burnt meat")
                && api.myPlayer.hasItem("Onion"))
        { return true;
        } else return false;
    }

    private void quest() {
        int quest_state = api.mp.getConfigs().get(quest_id);

//        client.log("Witches Potion: " + quest_id + " - " + quest_state);
        switch (quest_state) {
            case 0:
                if(Hettys_House.contains(api.mp.myPlayer())) {
                    api.interact.talkNPC("Hetty", new int[]{1,1});
                } else { api.myPlayer.moveTo(Hettys_House); }
            case 1:
                if(hasItems()) {
                    if(Hettys_House.contains(api.mp.myPlayer())) {
                        api.interact.talkNPC("Hetty");
                    } else { api.myPlayer.moveTo(Hettys_House); }
                }
                else if(!api.myPlayer.hasItem("Rat's tail")) {
                    if(Archery_Shop.contains(api.mp.myPlayer())) {
                        api.client.log("Getting tail");
                        api.fighter.setEnemy("Rat");
                        api.fighter.setLootables(new String[]{"Rat's tail"});
                        api.fighter.fight();
                    } else { api.myPlayer.moveTo(Archery_Shop); }
                }
                else if(!api.myPlayer.hasItem("Burnt meat")) {
                    if(api.myPlayer.hasItem("Raw rat meat") || api.myPlayer.hasItem("Cooked meat")) {
                        if(api.myPlayer.isWithin(Range, 1)) {
                            if(api.myPlayer.hasItem("Raw rat meat")) { api.interact.useItemWithObject("Raw rat meat", "Range"); }
                            else if(api.myPlayer.hasItem("Cooked meat")) { api.interact.useItemWithObject("Cooked meat", "Range"); }
                        } else { api.myPlayer.moveTo(Range); }
                    } else {
                        if(api.myPlayer.isWithin(Giant_Rat, 10)) {
                            api.fighter.setEnemy("Giant rat");
                            api.fighter.setLootables(new String[]{"Raw rat meat"});
                            api.fighter.fight();
                        } else { api.myPlayer.moveTo(Giant_Rat); }
                    }
                }
                else if(!api.myPlayer.hasItem("Eye of newt")) {
                    if(!api.myPlayer.hasItem("Coins", 3)) {
                        if(api.myPlayer.isWithin(Range, 20)) {
                            api.fighter.setEnemies(new String[]{"Man","Woman"});
                            api.fighter.setLootables(new String[]{"Coins"});
                            api.fighter.fight();
                        } else {api.myPlayer.moveTo(Range); }
                    } else {
                        if(Bettys_Shop.contains(api.mp.myPlayer())) {
                            api.interact.shop("Betty", "Eye of newt", 1);
                        } else {api.myPlayer.moveTo(Bettys_Shop); }
                    }
                }
                else if(!api.myPlayer.hasItem("Onion")) {
                    if(api.myPlayer.isWithin(Onion_Patch, 5)) {
                        api.interact.interactOb("Onion", "Pick");
                    } else { api.myPlayer.moveTo(Onion_Patch); }
                }
            case 2:
                if(Hettys_House.contains(api.mp.myPlayer())) {
                    api.interact.interactOb("Cauldron", "Drink From");
                } else { api.myPlayer.moveTo(Hettys_House); }
        }
    }
}
