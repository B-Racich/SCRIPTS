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

import static Core.API.ScriptState.IDLE;

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
    public int quest_state;
    private API.ScriptState scriptState;

    public API.ScriptState getState() {
        return scriptState;
    }

    private Area Veronica_Area = new Area(3109,3329,3110,3330);
    private Area Fish_Food_Area = new Area(new Position(3107,3356,1), new Position(3110,3359,1));
    private Area Poison_Area = new Area(3097,3365,3100,3365);
    private Position Bookcase_Pos = new Position(3098,3359,0);
    private Area Downstairs_Area = new Area(new Position(3090,9745,0), new Position(3118,9767,0));

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

    private static Position Maze_Door_1 = new Position(3108, 9758, 0);
    private static Position Maze_Door_2 = new Position(3105, 9760, 0);
    private static Position Maze_Door_3 = new Position(3102, 9758, 0);
    private static Position Maze_Door_4 = new Position(3100, 9760, 0);
    private static Position Maze_Door_5 = new Position(3097, 9763, 0);
    private static Position Maze_Door_6 = new Position(3100, 9765, 0);
    private static Position Maze_Door_7 = new Position(3105, 9765, 0);
    private static Position Maze_Door_8 = new Position(3102, 9763, 0);
    private static Position Maze_Door_9 = new Position(3100, 9755, 0);

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
        STAGE_1,
        STAGE_2,
        STAGE_3,
        STAGE_4,
        STAGE_5,
        STAGE_6;
    }

    HashMap<String, Boolean> doorStates = new HashMap<String, Boolean>();
    HashMap<String, String> leverStates = new HashMap<String, String>();

    private void getLeverStates() {
        List<RS2Object> objects = api.mp.getObjects().getAll();
        List<RS2Object> levers = new ArrayList<>();
        Iterator<RS2Object> itr = objects.iterator();

        while(itr.hasNext()) {
            RS2Object ob = itr.next();
            if(ob.getName().contains("Lever"))  {
                levers.add(ob);
                leverStates.put(ob.getName(), LeverState.getState(ob.getModelIds()[0]));
//                api.log(ob.getName()+"\t:"+LeverState.getState(ob.getModelIds()[0]));
            }
        }
    }

    private void getDoorStates() {
        List<RS2Object> objects = api.mp.getObjects().getAll();
        List<RS2Object> doors = new ArrayList<>();

        Position[] doorPosArr = new Position[]{
            Maze_Door_1,
            Maze_Door_2,
            Maze_Door_3,
            Maze_Door_4,
            Maze_Door_5,
            Maze_Door_6,
            Maze_Door_7,
            Maze_Door_8,
            Maze_Door_9,
        };

        Iterator<RS2Object> itr = objects.iterator();

        while(itr.hasNext()) {
            RS2Object ob = itr.next();
            if(ob != null && ob.getName().contains("Door"))  {
                doors.add(ob);
                for(int i = 0; i < doorPosArr.length; i++) {
                    if(ob.getPosition().distance(doorPosArr[i]) <= 0) {
                        boolean isOpen = ob.getModelIds()[0] == 11813 ? true : false;
                        doorStates.put("Door "+(i+1), isOpen);
                        //  api.log(ob.getName()+"\t:"+ob.getPosition()+"\t:"+isOpen);
                        break;
                    }
                }
            }
        }
    }

    private void getMazeState() {
        getLeverStates();
        getDoorStates();


    }

    @Override
    public void run() {

        try {
            quest_state = api.mp.getConfigs().get(quest_id);

            api.log("Ernest The Chicken: " + quest_id + " - " + quest_state);
            switch (quest_state) {
                default:
                    scriptState = IDLE;
                    break;
                case 0:
                    api.interact.moveToAreaAnd(Veronica_Area, () -> api.interact.talkNPC("Veronica", new int[]{1}));
                    break;
                case 1:
                    if (!api.myPlayer.hasItem("Poisoned fish food")) {
                        if (api.myPlayer.hasItem("Fish food") && api.myPlayer.hasItem("Poison")) {
                            api.interact.combineItems("Fish food", "Poison");
                        } else if (!api.myPlayer.hasItem("Fish food")) {
                            api.interact.moveToAreaAnd(Fish_Food_Area, () -> api.interact.pickUpItem("Fish food"));
                        } else if (!api.myPlayer.hasItem("Poison")) {
                            api.interact.moveToAreaAnd(Poison_Area, () -> api.interact.pickUpItem("Poison"));
                        }
                    } else if (!api.myPlayer.hasItem("Oil can")) {
                        if (!Downstairs_Area.contains(api.mp.myPlayer())) {
                            api.interact.moveToPosAnd(Bookcase_Pos, 1, () -> {
                                api.interact.interactOb("Bookcase", "Search");
                                Timing.wait(1500);
                                api.interact.interactOb("Ladder", "Climb-down");
                                return true;
                            });
                        } else {
                            getMazeState();

                        }
                    }
                    break;
            }

        } catch(Exception e) {
            api.log("INNER SCRIPT: STARTS:========");
            api.log(e.getMessage());
            api.osbot.log(e.getStackTrace());
            api.log("INNER SCRIPT: STARTS:========");
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void paint(Graphics2D g) {

    }

}
