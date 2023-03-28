package Scripts.Quester.Quests.F2P;

import Core.API;
import Core.Api.Common.Interfaces.ApiScript;
import Core.Api.Common.Interfaces.Quest;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;

import java.awt.*;

public class WitchesPotion implements ApiScript, Quest {

    public final static String scriptName = "Witches Potion";

    private API api;

    private long timeBegan;
    private long timeRan;

    public WitchesPotion(API api) {
        this.api = api;
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

    private Area Aggies_House = new Area(new Position(3083,3256,0), new Position(3088,3261,0));
    private Area Hettys_House = new Area(new Position(2965,3203,0), new Position(2970,3208,0));
    private Area Archery_Shop = new Area(new Position(2953,3202,0), new Position(2960,3205,0));
    private Area Bettys_Shop = new Area(new Position(3011,3256,0), new Position(3016,3261,0));
    private Position Giant_Rat = new Position(2998,3194,0);
    private Position Range = new Position(3018,3238,0);
    private Position Onion_Patch = new Position(2950,3252,0);

    public final int quest_id = 67;
    public int quest_state;

    public boolean isCompleted() {
        return api.mp.getQuests().isComplete(Quests.Quest.WITCHS_POTION);
    }

    private boolean hasItems() {
        return api.myPlayer.hasItem("Rat's tail")
                && api.myPlayer.hasItem("Eye of newt")
                && api.myPlayer.hasItem("Burnt meat")
                && api.myPlayer.hasItem("Onion");
    }

    private void quest() {
        quest_state = api.mp.getConfigs().get(quest_id);

//        client.log("Witches Potion: " + quest_id + " - " + quest_state);
        switch (quest_state) {
            case 0:
                api.interact.moveToAreaAnd(Hettys_House,()->api.interact.talkNPC("Hetty", new int[]{1,1}));
                break;
            case 1:
                if(hasItems()) {
                    api.interact.moveToAreaAnd(Hettys_House,()->api.interact.talkNPC("Hetty"));
                }
                else if(!api.myPlayer.hasItem("Rat's tail")) {
                    api.interact.moveToAreaAnd(Archery_Shop,()->{
                        api.log("Getting tail");
                        api.fighter.setEnemy("Rat");
                        api.fighter.setLootables(new String[]{"Rat's tail"});
                        api.fighter.fight();
                        return true;
                    });
                }
                else if(!api.myPlayer.hasItem("Burnt meat")) {
                    if(api.myPlayer.hasItem("Raw rat meat") || api.myPlayer.hasItem("Cooked meat")) {
                        api.interact.moveToPosAnd(Range,1,()->{
                            if(api.myPlayer.hasItem("Raw rat meat")) { api.interact.useItemWithObject("Raw rat meat", "Range"); }
                            else if(api.myPlayer.hasItem("Cooked meat")) { api.interact.useItemWithObject("Cooked meat", "Range"); }
                            return true;
                        });
                    } else {
                        api.interact.moveToPosAnd(Giant_Rat,10,()->{
                            api.fighter.setEnemy("Giant rat");
                            api.fighter.setLootables(new String[]{"Raw rat meat"});
                            api.fighter.fight();
                            return true;
                        });
                    }
                }
                else if(!api.myPlayer.hasItem("Eye of newt")) {
                    if(!api.myPlayer.hasItem("Coins", 3)) {
                        api.interact.moveToPosAnd(Range,20,()->{
                            api.fighter.setEnemies(new String[]{"Man","Woman"});
                            api.fighter.setLootables(new String[]{"Coins"});
                            api.fighter.fight();
                            return true;
                        });
                    } else {
                        api.interact.moveToAreaAnd(Bettys_Shop,()-> api.interact.shop("Betty", "Eye of newt", 1));
                    }
                }
                else if(!api.myPlayer.hasItem("Onion")) {
                    api.interact.moveToPosAnd(Onion_Patch,5,()->api.interact.interactOb("Onion", "Pick"));
                }
                break;
            case 2:
                api.interact.moveToAreaAnd(Hettys_House,()->api.interact.interactOb("Cauldron", "Drink From"));
                break;
        }
    }
}
