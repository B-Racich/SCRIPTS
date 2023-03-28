package Scripts.Quester.Quests.F2P;

import Core.API;
import Core.Api.Common.Interfaces.ApiScript;
import Core.Api.Common.Interfaces.Quest;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.constants.Banks;

import java.awt.*;

public class GoblinDiplomacy implements ApiScript, Quest {

    public final static String scriptName = "Goblin Diplomacy";

    private API api;

    private long timeBegan;
    private long timeRan;

    public GoblinDiplomacy(API api) {
        this.api = api;
    }

    public final int quest_id = 62;
    public int quest_state;

    public boolean isCompleted() {
        return api.mp.getQuests().isComplete(Quests.Quest.GOBLIN_DIPLOMACY);
    }

    enum state {}

    private Area Aggies_House = new Area(3086,3257,3087,3260);
    private Area Onion_Patch = new Area(2951,3252,2953,3253);
    private Area Falador_Park = new Area(3024,3378,3027,3381);
    private Area Redberry_Bushes = new Area(3273,3369,3277,3372);
    private Area Sarim_Food_Store = new Area(3012,3203,3015,3207);
    private Area Goblin_Village = new Area(2956,3511,2959,3514);

    private Position Goblin_Mail_1 = new Position(2952,3508,0);
    private Position Goblin_Mail_1_Door = new Position(2954,3505,0);
    private Position Goblin_Mail_2 = new Position(2955,3497,0);
    private Position Goblin_Mail_2_test = new Position(2944,3497,2);
    private Position Goblin_Mail_3 = new Position(2960,3514,0);

    private int[] Red_Dye_Dialogue = new int[] {4,1,1};	//	3 redberries 5 coins
    private int[] Yellow_Dye_Dialogue = new int[] {4,2,1,};		//	2 onions 5 coins
    private int[] Blue_Dye_Dialogue = new int[] {4,3,1};		//	2 woad leaves 5 coins

    private boolean hasDyes() {
        if(api.myPlayer.hasItem("Blue dye")) madeBlueDye = true;
        if(api.myPlayer.hasItem("Yellow dye")) madeYellowDye = true;
        if(api.myPlayer.hasItem("Red dye")) madeRedDye = true;
        madeDyes = madeBlueDye && madeRedDye && madeYellowDye;
        return madeDyes;
    }

    private boolean hasDyeIngredients() {
       hasDyes();
       boolean hasBerries = api.myPlayer.hasItem("Redberries",3);
       boolean hasWoadleaves = api.myPlayer.hasItem("Woad leaf",2);
       boolean hasOnions = api.myPlayer.hasItem("Onion",2);
        return (madeRedDye || hasBerries)
                && (madeYellowDye || hasOnions)
                && (madeBlueDye || hasWoadleaves);
    }

    private boolean hasDyedMail() {
        return api.myPlayer.hasItem("Blue goblin mail") &&
                api.myPlayer.hasItem("Orange goblin mail") &&
                api.myPlayer.hasItem("Goblin mail");
    }

    private boolean madeYellowDye = false;
    private boolean madeRedDye = false;
    private boolean madeBlueDye = false;

    private boolean madeDyes;

    private boolean hasCoinsForDye = false;
    private boolean hasCoinsForBerries = false;
    private boolean hasCoinsForWoadLeaves = false;

    private boolean hasGoblinMail_1 = false;
    private boolean hasGoblinMail_2 = false;
    private boolean hasGoblinMail_3 = false;

    private boolean hasEnoughGold(String item) {
        if(item == "Redberries") {
            int coins = (int)api.mp.getInventory().getAmount("Coins");
            int berries = (int)api.mp.getInventory().getAmount("Redberries");
            int berryCost = 3;

            api.log("Cost calc: "+(coins - ((3-berries)*berryCost)));
            return coins - ((3 - berries) * berryCost) >= 0;
        }
        else if(item == "Dyes") {
            int cost = 15;
            if(madeYellowDye) cost-=5;
            if(madeBlueDye) cost-=5;
            if(madeRedDye) cost-=5;

            int coins = (int)api.mp.getInventory().getAmount("Coins");

            return coins >= cost;
        }
        return false;
    }

    @Override
    public void run() throws NullPointerException {
        quest_state = api.mp.getConfigs().get(quest_id);
        madeDyes = madeBlueDye && madeRedDye && madeYellowDye;
        boolean hasGoblinMails = hasGoblinMail_1 && hasGoblinMail_2 && hasGoblinMail_3;

        if(api.interact.isTalking()) {
            api.interact.clickContinue();
            return;
        }

        switch(quest_state) {
            case 0:
                if(!hasDyes() && !madeDyes) {
                    api.log("Need Dyes");
                    if(!hasDyeIngredients()) {
                        api.log("Need ingredients");
                        if(!api.myPlayer.hasItem("Onion",2)) {
                            api.interact.moveToAreaAnd(Onion_Patch,()->api.interact.interactOb("Onion","Pick"));
                        }
                        else if(!api.myPlayer.hasItem("Woad leaf",2)) {
                            if(!api.myPlayer.hasItem("Coins",20) && !hasCoinsForWoadLeaves) {
                                if(api.interact.moveToAreaAnd(Banks.FALADOR_EAST,()->api.banking.withdraw("Coins",20))) hasCoinsForWoadLeaves = true;
                            }
                            else {
                                api.interact.moveToAreaAnd(Falador_Park,()->api.interact.talkNPC("Wyson the gardener",new int[]{1,4}));
                            }
                        }
                        else if(!api.myPlayer.hasItem("Redberries",3)) {
                            if((!api.myPlayer.hasItem("Coins",9) && !hasCoinsForBerries) && !hasEnoughGold("Redberries")) {
                                if(api.interact.moveToAreaAnd(Banks.FALADOR_EAST,()->api.banking.withdraw("Coins",9))) hasCoinsForBerries = true;
                            }
                            else {
                                api.log("Buying berries");
                                api.interact.moveToAreaAnd(Sarim_Food_Store,()->api.interact.shop("Wydin","Redberries",1));
                            }
                        }
                    }
                    else {
                        api.log("Has all ingredients");
                        if((!api.myPlayer.hasItem("Coins",15) && !hasCoinsForDye) && !hasEnoughGold("Dyes")) {
                            if(api.interact.moveToAreaAnd(Banks.DRAYNOR,()->api.banking.withdraw("Coins",15))) hasCoinsForDye = true;
                        }
                        else {
                            api.interact.moveToAreaAnd(Aggies_House,()->{
                                if(!api.myPlayer.hasItem("Red dye") && !madeRedDye) {
                                    if(api.interact.talkNPC("Aggie",Red_Dye_Dialogue)) {
                                        madeRedDye = true;
                                        return true;
                                    }
                                }
                                else if(!api.myPlayer.hasItem("Blue dye") && !madeBlueDye) {
                                    if(api.interact.talkNPC("Aggie",Blue_Dye_Dialogue)) {
                                        madeBlueDye = true;
                                        return true;
                                    }
                                }
                                else if(!api.myPlayer.hasItem("Yellow dye") && !madeYellowDye) {
                                    if(api.interact.talkNPC("Aggie",Yellow_Dye_Dialogue)) {
                                        madeYellowDye = true;
                                        return true;
                                    }
                                }
                                return false;
                            });
                        }
                    }
                } else {
                    api.log("Goblin mail time");
                    if(!hasGoblinMails) {
                        if(!hasGoblinMail_1) {
                            if(api.interact.moveToPosAnd(Goblin_Mail_1_Door,1,()->{
                                if(api.mp.getObjects().closest("Door").hasAction("Open")) {
                                    api.interact.interactOb("Door","Open");
                                }
                                api.interact.interactOb("Crate","Search");
                                hasGoblinMail_1 = true;
                                return true;
                            }));
                        }
                        else if(!hasGoblinMail_2) {
                            if(api.interact.moveToPosAnd(Goblin_Mail_2,1,()->{
                                if(api.mp.myPosition().getZ()==0) {
                                    api.interact.interactOb("Ladder","Climb-up");
                                }
                                api.interact.interactOb("Crate","Search");
                                hasGoblinMail_2 = true;
                                if(api.mp.myPosition().getZ()==2) {
                                    api.interact.interactOb("Ladder","Climb-down");
                                }
                                return true;
                            }));
                        }
                        else if(!hasGoblinMail_3) {
                            if(api.interact.moveToPosAnd(Goblin_Mail_3,1,()->api.interact.interactOb("Crate","Search"))) hasGoblinMail_3 = true;
                        }
                    }
                    else {
                        if(!api.myPlayer.hasItem("Orange goblin mail")) {
                            if(!api.myPlayer.hasItem("Orange dye")) {
                                api.interact.combineItems("Red dye", "Yellow dye");
                            } else {
                                api.interact.combineItems("Orange dye", "Goblin mail");
                            }
                        }
                        else if(!api.myPlayer.hasItem("Blue goblin mail")) {
                            api.interact.combineItems("Blue dye", "Goblin mail");
                        }
                    }
                }
                break;
            case 16832:
                api.interact.moveToAreaAnd(Goblin_Village,()->api.interact.talkNPC("General Wartface", new int[]{3,3,1}));
                break;
            case 17348:
                api.interact.moveToAreaAnd(Goblin_Village,()->api.interact.talkNPC("General Wartface", new int[]{1,1}));
                break;
            case 17861:
                api.interact.moveToAreaAnd(Goblin_Village,()->api.interact.talkNPC("General Wartface", new int[]{1,1}));
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
