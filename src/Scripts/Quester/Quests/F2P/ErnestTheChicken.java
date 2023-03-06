package Scripts.Quester.Quests.F2P;

import Core.API;
import Core.Api.Common.Interfaces.ApiScript;
import Core.Api.Common.Interfaces.Quest;
import Core.Api.Common.Timing;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ErnestTheChicken implements ApiScript, Quest {

    public final static String scriptName = "Ernest The Chicken";

    private API api;

    private long timeBegan;
    private long timeRan;

    public ErnestTheChicken(API api) {
        this.api = api;
    }

    public boolean isCompleted() {
        if(api.mp.getQuests().isComplete(Quests.Quest.ERNEST_THE_CHICKEN)) return true;
        else return false;
    }

    public final int quest_id = 32;
    private int quest_state;

    @Override
    public <state> state getState() {
        return null;
    }

    private Area Veronica_Area = new Area(3109,3329,3110,3330);
    private Area Fish_Food_Area = new Area(new Position(3107,3356,1), new Position(3110,3359,1));
    private Area Poison_Area = new Area(3097,3365,3100,3365);
    private Position Bookcase_Pos = new Position(3098,3359,0);
    private Area Downstairs_Area = new Area(new Position(3090,9745,0), new Position(3118,9767,0));

    private Position door1Entry = new Position(3108,9757,0);
    private Position door1Exit = new Position(3108,9759,0);
    private Position door2Entry = new Position(3106,9760,0);
    private Position door2Exit = new Position(3104,9760,0);
    private Position door3Entry = new Position(3102,9759,0);
    private Position door3Exit = new Position(3102,9757,0);
    private Position door4Entry = new Position(3101,9760,0);
    private Position door4Exit = new Position(3099,9757,0);
    private Position door5Entry = new Position(3097,9762,0);
    private Position door5Exit = new Position(3097,9764,0);
    private Position door6Entry = new Position(3099,9765,0);
    private Position door6Exit = new Position(3101,9765,0);
    private Position door7Entry = new Position(3104,9765,0);
    private Position door7Exit = new Position(3106,9765,0);
    private Position door8Entry = new Position(3102,9764,0);
    private Position door8Exit = new Position(3102,9762,0);
    private Position door9Entry = new Position(3106,9760,0);
    private Position door9Exit = new Position(3102,9757,0);

    private Area roomPos1 = new Area(3105,9758,3112,9767);
    private Area roomPos2 = new Area(3096,9763,3099,9767);
    private Area roomPos3 = new Area(3100,9763,3104,9767);
    private Area roomPos4 = new Area(3096,9758,3099,9762);
    private Area roomPos5 = new Area(3100,9758,3104,9762);
    private Area bigRoom = new Area(3100,9750,3118,9757);
    private Area oilRoom = new Area(0,0,0,0);

    private boolean stage1 = false;
    private boolean stage2 = false;
    private boolean stage3 = false;
    private boolean stage4 = false;
    private boolean stage5 = false;
    private boolean stage6 = false;
    private boolean stage7 = false;
    private boolean stage8 = false;
    private boolean stage9 = false;
    private boolean stage10 = false;
    private boolean stage11 = false;
    private boolean stage12 = false;
    private boolean stage13 = false;

    private Position Maze_Door_1 = new Position(3108, 9758, 0);
    private Position Maze_Door_2 = new Position(3105, 9760, 0);
    private Position Maze_Door_3 = new Position(3102, 9758, 0);
    private Position Maze_Door_4 = new Position(3097, 9763, 0);
    private Position Maze_Door_6 = new Position(3100, 9765, 0);
    private Position Maze_Door_7 = new Position(3105, 9765, 0);
    private Position Maze_Door_8 = new Position(3102, 9763, 0);
    private Position Maze_Door_9 = new Position(3100, 9755, 0);

    private enum LeverState {
        UP(2108),
        DOWN(2077);

        int model;
        int String;

        static public String getState(int model) {
            if(model == 2108) return "Up";
            else if(model == 2077) return "Down";
            else return "WTF is this";
        }

        LeverState(int model) {
            this.model = model;
        }
    }

    boolean pullLever(String lever, LeverState state) {
        RS2Object ob = api.mp.getObjects().closest(lever);
        int[] modelIds;
        if(ob != null) {
            modelIds = ob.getModelIds();
            if(modelIds != null && modelIds[0] != state.model) {
                ob.interact("Pull");
                Timing.waitCondition(() -> api.mp.getObjects().closest(lever).getModelIds()[0] == state.model, 5000);
                return true;
            }
            else
                return true;
        }
        return false;
    }

    private void goThroughDoor(Position entry, Position exit) {
        api.mp.getWalking().webWalk(entry);
        Timing.waitCondition(() -> api.myPlayer.isIdle(false) && api.myPlayer.isWithin(entry, 0), 3000);
        if(api.myPlayer.isWithin(entry, 0)) {
            RS2Object ob = api.mp.getObjects().closest("Door");
            if(ob != null) {
                ob.interact("Open");
                Timing.waitCondition(() -> api.myPlayer.isWithin(exit, 0), 3000);
            }
        }
    }

    enum mazeState {
        STAGE_1(new HashMap<String, LeverState>(){{
            put("Lever A", LeverState.DOWN);
            put("Lever B", LeverState.DOWN);
        }}),
        STAGE_2(new HashMap<String, LeverState>(){{
            put("Lever D", LeverState.DOWN);
        }}),
        STAGE_3(new HashMap<String, LeverState>(){{
            put("Lever A", LeverState.UP);
            put("Lever B", LeverState.UP);
        }}),
        STAGE_4(new HashMap<String, LeverState>(){{
            put("Lever E", LeverState.DOWN);
            put("Lever F", LeverState.DOWN);
        }}),
        STAGE_5(new HashMap<String, LeverState>(){{
            put("Lever C", LeverState.DOWN);
        }}),
        STAGE_6(new HashMap<String, LeverState>(){{
            put("Lever E", LeverState.UP);
        }});

        HashMap<String, LeverState> leverStates;

        mazeState(HashMap<String, LeverState> state) {
            this.leverStates = state;
        }
    }

    private void getLeverStates() {
        List<RS2Object> objects = api.mp.getObjects().getAll();
        List<RS2Object> levers = new ArrayList<>();

        Iterator<RS2Object> itr = objects.iterator();

        while(itr.hasNext()) {
            RS2Object ob = itr.next();
            if(ob.getName().contains("Lever"))  {
                levers.add(ob);
                api.log(ob.getName()+"\t:"+LeverState.getState(ob.getModelIds()[0]));
            }
        }
    }

    private void getDoorStates() {
        List<RS2Object> objects = api.mp.getObjects().getAll();
        List<RS2Object> doors = new ArrayList<>();

        Iterator<RS2Object> itr = objects.iterator();

        while(itr.hasNext()) {
            RS2Object ob = itr.next();
            if(ob.getName().contains("Door"))  {
                doors.add(ob);
                api.log(ob.getName()+"\t:"+ob.getPosition()+"\t:"+ob.getModelIds()[0]);
            }
        }
    }

    @Override
    public void run() throws NullPointerException {
        quest_state = api.mp.getConfigs().get(quest_id);

        api.log("Ernest The Chicken: " + quest_id + " - " + quest_state);
        switch (quest_state) {
            case 0:
                api.interact.moveToAreaAnd(Veronica_Area, ()-> api.interact.talkNPC("Veronica", new int[]{1}));
                break;
            case 1:
                if(!api.myPlayer.hasItem("Poisoned fish food")) {
                    if(api.myPlayer.hasItem("Fish food") && api.myPlayer.hasItem("Poison")) {
                        api.interact.combineItems("Fish food", "Poison");
                    }
                    else if(!api.myPlayer.hasItem("Fish food")) {
                        api.interact.moveToAreaAnd(Fish_Food_Area, ()-> api.interact.pickUpItem("Fish food"));
                    }
                    else if(!api.myPlayer.hasItem("Poison")) {
                        api.interact.moveToAreaAnd(Poison_Area, ()-> api.interact.pickUpItem("Poison"));
                    }
                }
                else if(!api.myPlayer.hasItem("Oil can")) {
                    if(!Downstairs_Area.contains(api.mp.myPlayer())) {
                        api.interact.moveToPosAnd(Bookcase_Pos, 1, ()-> {
                            api.interact.interactOb("Bookcase", "Search");
                            Timing.wait(1500);
                            api.interact.interactOb("Ladder", "Climb-down");
                            return true;
                        });
                    } else {
                        getLeverStates();
                        getDoorStates();
                        if(pullLever("Lever A", LeverState.DOWN) && pullLever("Lever B", LeverState.DOWN) && !stage1) {
                            api.log("1");
                            goThroughDoor(door1Entry, door1Exit);
                            stage1 = true;
                        }
                        else if(pullLever("Lever D", LeverState.DOWN) && stage1) {
                            api.log("2");
                            goThroughDoor(door2Entry, door2Exit);
                            stage2 = true;
                        }
                        else if(stage2 && api.myPlayer.isIdle(false) && roomPos5.contains(api.mp.myPlayer())) {
                            api.log("3");
                            goThroughDoor(door3Entry, door3Exit);
                            stage2 = false;
                            stage3 = true;
                        }
                        else if(bigRoom.contains(api.mp.myPlayer()) && pullLever("Lever A", LeverState.UP) && pullLever("Lever B", LeverState.UP) && stage3) {
                            api.log("4");
                            goThroughDoor(door4Entry, door4Exit);
                            stage3 = false;
                            stage4 = true;
                        }
                        else if(stage4) {
                            //goThroughDoor(doorPos4, roomPos4);
                            stage4 = false;
                            stage5 = true;
                        }
                        else if(stage5) {
                            //goThroughDoor(doorPos5, roomPos2);
                            stage5 = false;
                            stage6 = true;
                        }
                        else if(pullLever("Lever E", LeverState.DOWN) && pullLever("Lever F", LeverState.DOWN) && stage6) {
                            //goThroughDoor(doorPos6, roomPos3);
                            stage6 = false;
                            stage7 = true;
                        }
                        else if(stage7) {
                            //goThroughDoor(doorPos7, roomPos1);
                            stage7 = false;
                            stage8 = true;
                        }
                        else if(pullLever("Lever C", LeverState.DOWN) && stage8) {
                            //	goThroughDoor(doorPos7, roomPos3);
                            stage8 = false;
                            stage9 = true;
                        }
                        else if(stage9) {
                            //	goThroughDoor(doorPos6, roomPos2);
                            stage9 = false;
                            stage10 = true;
                        }
                        else if(pullLever("Lever E", LeverState.UP) && stage10) {
                            //goThroughDoor(doorPos6, roomPos3);
                            stage10 = false;
                            stage11 = true;
                        }
                        else if(stage11) {
                            //goThroughDoor(doorPos8, roomPos5);
                            stage11 = false;
                            stage12 = true;
                        }
                        else if(stage12) {
                            //goThroughDoor(doorPos3, bigRoom);
                            stage12 = false;
                            stage13 = true;
                        }
                        else if(stage13) {
                            stage13 = false;
                            api.log("Did it");
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void paint(Graphics2D g) {

    }

}
