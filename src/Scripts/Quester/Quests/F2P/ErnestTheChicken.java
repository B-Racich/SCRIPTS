package Scripts.Quester.Quests.F2P;

import Core.API;
import Core.Api.Common.Interfaces.ApiScript;
import Core.Api.Common.Interfaces.Quest;
import Core.Api.Common.Timing;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.randoms.Maze;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static Core.API.ScriptState.IDLE;
import static org.osbot.rs07.script.MethodProvider.random;

public class ErnestTheChicken implements ApiScript, Quest {

    public final static String scriptName = "Ernest The Chicken";

    private API api;

    private long timeBegan;
    private long timeRan;

    public ErnestTheChicken(API api) {
        this.api = api;

//        api.antiban.idleFight = true;
    }

    public boolean isCompleted() {
        return api.mp.getQuests().isComplete(Quests.Quest.ERNEST_THE_CHICKEN);
    }

    public void shutdown() {
        stageInProgress = false;
    }

    public final int quest_id = 32;
    public int quest_state;

    public API.ScriptState getState() {
        return IDLE;
    }

    private Area Veronica_Area = new Area(3109,3329,3110,3330);
    private Area Fish_Food_Area = new Area(new Position(3107,3356,1), new Position(3110,3359,1));
    private Area Poison_Area = new Area(3097,3365,3100,3365);
    private Position Bookcase_Pos = new Position(3098,3359,0);
    private Area Downstairs_Area = new Area(new Position(3090,9745,0), new Position(3118,9767,0));


    /**
     *      /-------/
     *      /2/3/ 1 /
     *      /5/4/   /
     * /----/-------/
     * / oil/  Big  /
     * /----/-------/
     */

    private Area Maze_Room_1 = new Area(3105,9758,3112,9767);
    private Area Maze_Room_2 = new Area(3096,9763,3099,9767);
    private Area Maze_Room_3 = new Area(3100,9763,3104,9767);
    private Area Maze_Room_4 = new Area(3100,9758,3104,9762);
    private Area Maze_Room_5 = new Area(3096,9758,3099,9762);
    private Area Maze_Big_Room = new Area(3100,9750,3118,9757);
    private Area Maze_Oil_Room = new Area(0,0,0,0);

    private static Position Maze_Door_1 = new Position(3108, 9758, 0);
    private static Position Maze_Door_2 = new Position(3105, 9760, 0);
    private static Position Maze_Door_3 = new Position(3102, 9758, 0);
    private static Position Maze_Door_4 = new Position(3100, 9760, 0);
    private static Position Maze_Door_5 = new Position(3097, 9763, 0);
    private static Position Maze_Door_6 = new Position(3100, 9765, 0);
    private static Position Maze_Door_7 = new Position(3105, 9765, 0);
    private static Position Maze_Door_8 = new Position(3102, 9763, 0);
    private static Position Maze_Door_9 = new Position(3100, 9755, 0);

    private Position Sync_Spot = new Position(3105,9757,0);

    private enum LeverState {
        UP(2108),
        DOWN(2077);

        int model;

        static public String getState(int model) {
            if(model == 2108) return "Up";
            else if(model == 2077) return "Down";
            else return "WTF is this";
        }

        LeverState(int model) {
            this.model = model;
        }
    }


    enum MazeState {
        STAGE_START,
        STAGE_1,
        STAGE_2,
        STAGE_3,
        STAGE_4,
        STAGE_5,
        STAGE_6,
        STAGE_END
    }

    private HashMap<String, String> stageDoorStates = new HashMap<String, String>();
    private HashMap<String, String> stageLeverStates = new HashMap<String, String>();

    private HashMap<String, String> currentDoorStates = new HashMap<String, String>();
    private HashMap<String, String> currentLeverStates = new HashMap<String, String>();

    private void getLeverStates() {
        List<RS2Object> objects = api.mp.getObjects().getAll();
        List<RS2Object> levers = new ArrayList<>();
        Iterator<RS2Object> itr = objects.iterator();

        while(itr.hasNext()) {
            RS2Object ob = itr.next();
            if(ob!= null && ob.getName().contains("Lever"))  {
                levers.add(ob);
                String isUp = ob.getModelIds()[0] == LeverState.UP.model ? "true" : "false";
                currentLeverStates.put(ob.getName(), isUp);
                api.log(ob.getName()+"\t:"+LeverState.getState(ob.getModelIds()[0]));
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
                        String isOpen = ob.getModelIds()[0] == 11813 ? "true" : "false";
                        currentDoorStates.put("Door "+(i+1), isOpen);
                        api.log(ob.getName()+" "+(i+1)+"\t:"+ob.getPosition()+"\t:"+isOpen);
                        break;
                    }
                }
            }
        }
    }

    private void getMazeState() {
        api.log("**getMazeState**");

        if(!api.myPlayer.isWithin(Sync_Spot, 7))
            api.interact.moveTo(Sync_Spot,7);

        getLeverStates();
        getDoorStates();

        if(!stageInProgress) {
            stageDoorStates = currentDoorStates;
            stageLeverStates = currentLeverStates;
        }

        String[][] Current_Doors = new String[2][9];
        String[][] Current_Levers = new String[2][6];

        for(int i=0; i<Current_Doors[0].length; i++) {
            int index=i+1;

            String door = "Door "+index;
            Current_Doors[0][i] = door;
            Current_Doors[1][i] = stageDoorStates.get(door);
        }

        String[] leverNames = {"Lever A", "Lever B",  "Lever C",  "Lever D",  "Lever E",  "Lever F"};
        for(int i=0; i<leverNames.length; i++) {
            String lever = leverNames[i];

            Current_Levers[0][i] = lever;
            Current_Levers[1][i] = stageLeverStates.get(lever);
        }

        api.log(Arrays.deepToString(Current_Doors));
        api.log("==============================");
        api.log(Arrays.deepToString(Current_Levers));

        /**
         * Start
         */
        String[][] Maze_Start_Doors = new String[][]{
                {"Door 1",  "Door 2",   "Door 3",   "Door 4",   "Door 5",   "Door 6",   "Door 7",   "Door 8",   "Door 9"},
                {"false",   "false",    "false",    "false",    "false",    "false",    "false",    "false",    "false",}};

        String[][] Maze_Start_Levers = new String[][]{
                {"Lever A", "Lever B",  "Lever C",  "Lever D",  "Lever E",  "Lever F"},
                {"true",    "true",     "true",     "true",     "true",     "true"}};
        /**
         * Stage 1
         */
        String[][] Maze_Doors_1 = new String[][]{
                {"Door 1",  "Door 2",   "Door 3",   "Door 4",   "Door 5",   "Door 6",   "Door 7",   "Door 8",   "Door 9"},
                {"true",   "true",    "false",    "false",    "false",    "false",    "false",    "false",    "false",}};

        String[][] Maze_Levers_1 = new String[][]{
                {"Lever A", "Lever B",  "Lever C",  "Lever D",  "Lever E",  "Lever F"},
                {"false",    "false",     "true",     "true",     "true",     "true"}};
        /**
         * Stage 2
         */
        String[][] Maze_Doors_2 = new String[][]{
                {"Door 1",  "Door 2",   "Door 3",   "Door 4",   "Door 5",   "Door 6",   "Door 7",   "Door 8",   "Door 9"},
                {"true",   "true",    "true",    "true",    "false",    "false",    "false",    "false",    "false",}};

        String[][] Maze_Levers_2 = new String[][]{
                {"Lever A", "Lever B",  "Lever C",  "Lever D",  "Lever E",  "Lever F"},
                {"false",    "false",     "true",     "false",     "true",     "true"}};
        /**
         * Stage 3
         */
            String[][] Maze_Doors_3 = new String[][]{
                    {"Door 1",  "Door 2",   "Door 3",   "Door 4",   "Door 5",   "Door 6",   "Door 7",   "Door 8",   "Door 9"},
                    {"false",   "false",    "false",    "false",    "false",    "false",    "false",    "false",    "false",}};

            String[][] Maze_Levers_3 = new String[][]{
                    {"Lever A", "Lever B",  "Lever C",  "Lever D",  "Lever E",  "Lever F"},
                    {"true",    "true",     "true",     "true",     "true",     "true"}};
        /**
         * Stage 4
         */
        String[][] Maze_Doors_4 = new String[][]{
                {"Door 1",  "Door 2",   "Door 3",   "Door 4",   "Door 5",   "Door 6",   "Door 7",   "Door 8",   "Door 9"},
                {"false",   "false",    "false",    "false",    "false",    "false",    "false",    "false",    "false",}};

        String[][] Maze_Levers_4 = new String[][]{
                {"Lever A", "Lever B",  "Lever C",  "Lever D",  "Lever E",  "Lever F"},
                {"true",    "true",     "true",     "true",     "true",     "true"}};
        /**
         * Stage 5
         */
        String[][] Maze_Doors_5 = new String[][]{
                {"Door 1",  "Door 2",   "Door 3",   "Door 4",   "Door 5",   "Door 6",   "Door 7",   "Door 8",   "Door 9"},
                {"false",   "false",    "false",    "false",    "false",    "false",    "false",    "false",    "false",}};

        String[][] Maze_Levers_5 = new String[][]{
                {"Lever A", "Lever B",  "Lever C",  "Lever D",  "Lever E",  "Lever F"},
                {"true",    "true",     "true",     "true",     "true",     "true"}};
        /**
         * Stage 6
         */
        String[][] Maze_Doors_6 = new String[][]{
                {"Door 1",  "Door 2",   "Door 3",   "Door 4",   "Door 5",   "Door 6",   "Door 7",   "Door 8",   "Door 9"},
                {"false",   "false",    "false",    "false",    "false",    "false",    "false",    "false",    "false",}};

        String[][] Maze_Levers_6 = new String[][]{
                {"Lever A", "Lever B",  "Lever C",  "Lever D",  "Lever E",  "Lever F"},
                {"true",    "true",     "true",     "true",     "true",     "true"}};

        if(Arrays.deepEquals(Current_Doors,Maze_Doors_1) && Arrays.deepEquals(Current_Levers,Maze_Levers_1)) {
            api.log("Matched 1");
            mazeState = MazeState.STAGE_1;
        }
        else if(Arrays.deepEquals(Current_Doors,Maze_Doors_2) && Arrays.deepEquals(Current_Levers,Maze_Levers_2)) {
            api.log("Matched 2");
            mazeState = MazeState.STAGE_2;
        }
        else if(Arrays.deepEquals(Current_Doors,Maze_Doors_3) && Arrays.deepEquals(Current_Levers,Maze_Levers_3)) {
            api.log("Matched 3");
            mazeState = MazeState.STAGE_3;
        }
        else if(Arrays.deepEquals(Current_Doors,Maze_Doors_4) && Arrays.deepEquals(Current_Levers,Maze_Levers_4)) {
            api.log("Matched 4");
            mazeState = MazeState.STAGE_4;
        }
        else if(Arrays.deepEquals(Current_Doors,Maze_Doors_5) && Arrays.deepEquals(Current_Levers,Maze_Levers_5)) {
            api.log("Matched 5");
            mazeState = MazeState.STAGE_5;
        }
        else if(Arrays.deepEquals(Current_Doors,Maze_Doors_6) && Arrays.deepEquals(Current_Levers,Maze_Levers_6)) {
            api.log("Matched 6");
            mazeState = MazeState.STAGE_6;
        }
        else {
            api.log("Matched Nothing");
            mazeState = MazeState.STAGE_START;
        }
    }

    public boolean stageInProgress = false;
    public MazeState mazeState = MazeState.STAGE_START;

    private void walkThroughDoor(Position doorPos) {
        api.mp.getWalking().walk(doorPos);
        Timing.wait(random(4000, 5000));
        api.interact.interactOb("Door", "Open");
        Timing.wait(random(4000, 5000));
    }

    @Override
    public void run() {

        try {
            quest_state = api.mp.getConfigs().get(quest_id);

            api.log("Ernest The Chicken: " + quest_id + " - " + quest_state);
            switch (quest_state) {
                default:
                    API.ScriptState scriptState = IDLE;
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
                            if(!stageInProgress)
                                getMazeState();
                            api.log(mazeState+" "+stageInProgress);
                            switch(mazeState) {
                                case STAGE_START:
                                    api.log("**IN STAGE_START**");
                                    stageInProgress = true;
                                    while(stageInProgress) {
                                        if (Boolean.parseBoolean(currentLeverStates.get("Lever A"))) {
                                            api.interact.interactOb("Lever A", "Pull");
                                            Timing.waitCondition(() -> {
                                                RS2Object temp = api.mp.getObjects().filter(ob -> ob.getName().equals("Lever A") && ob.getModelIds()[0] == LeverState.DOWN.model).get(0);
                                                return temp != null;
                                            }, 4000);
                                        } else if (Boolean.parseBoolean(currentLeverStates.get("Lever B"))) {
                                            api.interact.interactOb("Lever B", "Pull");
                                            Timing.waitCondition(() -> {
                                                RS2Object temp = api.mp.getObjects().filter(ob -> ob.getName().equals("Lever B") && ob.getModelIds()[0] == LeverState.DOWN.model).get(0);
                                                return temp != null;
                                            }, 4000);
                                        } else if (!Maze_Room_1.contains(api.mp.myPlayer())) {
                                            walkThroughDoor(Maze_Door_1);
                                        } else stageInProgress = false;
                                    }
                                    break;
                                case STAGE_1:
                                    api.log("**IN STAGE_1**");
                                    stageInProgress = true;
                                    while(stageInProgress) {
                                        if(Boolean.parseBoolean(currentLeverStates.get("Lever D")))
                                            if(!Maze_Room_1.contains(api.mp.myPlayer())) {
                                                walkThroughDoor(Maze_Door_1);

                                            api.interact.interactOb("Lever D","Pull");
                                            Timing.waitCondition(()->{
                                                RS2Object temp = api.mp.getObjects().filter(ob->ob.getName().equals("Lever D") && ob.getModelIds()[0] == LeverState.DOWN.model).get(0);
                                                return temp != null;
                                            },4000);

                                        } else if(!Maze_Big_Room.contains(api.mp.myPlayer()))
                                            walkThroughDoor(Maze_Door_1);
                                        else stageInProgress = false;
                                    }
                                    break;
                                case STAGE_2:
                                    stageInProgress = true;
                                    api.log("**IN STAGE_2**");
                                    while(stageInProgress) {
                                        getMazeState();
                                        if (!Boolean.parseBoolean(currentLeverStates.get("Lever A")) || !Boolean.parseBoolean(currentLeverStates.get("Lever B"))) {
                                            if(!Maze_Big_Room.contains(api.mp.myPlayer())) {
                                                walkThroughDoor(Maze_Door_1);
                                            } else if(!Boolean.parseBoolean(currentLeverStates.get("Lever A"))) {
                                                api.interact.interactOb("Lever A", "Pull");
                                                Timing.waitCondition(() -> {
                                                    RS2Object temp = api.mp.getObjects().filter(ob -> ob.getName().equals("Lever A") && ob.getModelIds()[0] == LeverState.UP.model).get(0);
                                                    return temp != null;
                                                }, 4000);
                                            } else if (!Boolean.parseBoolean(currentLeverStates.get("Lever B"))) {
                                                api.interact.interactOb("Lever B", "Pull");
                                                Timing.waitCondition(() -> {
                                                    RS2Object temp = api.mp.getObjects().filter(ob -> ob.getName().equals("Lever B") && ob.getModelIds()[0] == LeverState.UP.model).get(0);
                                                    return temp != null;
                                                }, 4000);
                                            }
                                        } else if (Boolean.parseBoolean(currentLeverStates.get("Lever A")) && Boolean.parseBoolean(currentLeverStates.get("Lever B")) && !Maze_Room_2.contains(api.mp.myPlayer())) {
                                            if(!Maze_Room_5.contains(api.mp.myPlayer())) {
                                                if(!Maze_Room_4.contains(api.mp.myPlayer()))
                                                    walkThroughDoor(Maze_Door_3);
                                                else
                                                    walkThroughDoor(Maze_Door_4);
                                            } else
                                                walkThroughDoor(Maze_Door_5);
                                        } else stageInProgress = false;
                                    }
                                    break;
                                case STAGE_3:
                                    stageInProgress = true;
                                    api.log("**IN STAGE_3**");
                                    while(stageInProgress) {
                                        if (Boolean.parseBoolean(currentLeverStates.get("Lever E")) || Boolean.parseBoolean(currentLeverStates.get("Lever F"))) {
                                            if(!Maze_Room_5.contains(api.mp.myPlayer())) {
                                                if(!Maze_Room_5.contains(api.mp.myPlayer())) {
                                                    if(!Maze_Room_4.contains(api.mp.myPlayer()))
                                                        walkThroughDoor(Maze_Door_3);
                                                    else
                                                        walkThroughDoor(Maze_Door_4);
                                                } else
                                                    walkThroughDoor(Maze_Door_5);
                                            } else if(Boolean.parseBoolean(currentLeverStates.get("Lever E"))) {
                                                api.interact.interactOb("Lever E", "Pull");
                                                Timing.waitCondition(() -> {
                                                    RS2Object temp = api.mp.getObjects().filter(ob -> ob.getName().equals("Lever E") && ob.getModelIds()[0] == LeverState.DOWN.model).get(0);
                                                    return temp != null;
                                                }, 10000);
                                            } else if (Boolean.parseBoolean(currentLeverStates.get("Lever F"))) {
                                                api.interact.interactOb("Lever F", "Pull");
                                                Timing.waitCondition(() -> {
                                                    RS2Object temp = api.mp.getObjects().filter(ob -> ob.getName().equals("Lever F") && ob.getModelIds()[0] == LeverState.DOWN.model).get(0);
                                                    return temp != null;
                                                }, 10000);
                                            }
                                        } else if(!Maze_Room_1.contains(api.mp.myPlayer())){
                                            if(!Maze_Room_3.contains(api.mp.myPlayer()))
                                                walkThroughDoor(Maze_Door_6);
                                            else
                                                walkThroughDoor(Maze_Door_7);
                                        } else stageInProgress = false;
                                    }
                                    break;
                                case STAGE_4:
                                    break;
                                case STAGE_5:
                                    break;
                                case STAGE_6:
                                    break;
                                case STAGE_END:
                                    break;
                            }
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
