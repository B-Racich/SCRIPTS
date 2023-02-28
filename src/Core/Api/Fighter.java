package Core.Api;

import Core.API;
import Core.Api.Common.Timing;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.script.MethodProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Fighter {
    /**
     * VARIABLES
     */
    private API api;
    private MethodProvider mp;
    private MyPlayer myPlayer;

    private String npc_name = "";
    private String my_name = "";
    public NPC npc;
    private int npc_index = 0;

    public state fight_state;
    private int npc_health = 0;
    private Position npc_position;
    private String npc_interacting = "";

    private String[] lootables;
    private String[] enemies;

    public NpcTracker tracker;

    private int search_radius = 5;
    public HashMap<String, String> options;

    public enum state {
        IDLE,
        READY,
        RUN,
        LOOT,
        FIGHT,
        PASSIVE,
        DEFENSIVE,
        FLEE,
    }

    public Fighter(MethodProvider mp, MyPlayer myPlayer, String npc_name) {
        this.mp = mp;
        this.myPlayer = myPlayer;
        this.npc_name = npc_name;
        my_name = mp.myPlayer().getName();

        tracker = new NpcTracker(api);
        tracker.start();
    }

    public Fighter(API api) {
        this.api = api;
        mp = api.mp;
        myPlayer = api.myPlayer;
        my_name = mp.myPlayer().getName();

        fight_state = state.PASSIVE;

        tracker = new NpcTracker(api);
        tracker.start();
    }

    public void setOptions(HashMap<String, String> options) {
        this.options = options;
    }

    private void trackNPC(NPC npc) {
        tracker = new NpcTracker(api, npc);
    }

    public String getNpcName() { return npc_name; }

    public void setEnemy(String enemyName) {
//        npc_name = enemyName;
        this.enemies = new String[]{enemyName};
    }

    public void setEnemies(String[] enemies) { this.enemies = enemies; }

    public void shutdown() { if(tracker != null) tracker.shutdown();}

    public state getState() {
        if(npc == null || tracker == null) {
            if(npc_name == "" && enemies.length <= 0) return state.IDLE;
            mp.log("Fighter: getState(): getNPC()");
            getNpc();
            if(npc != null)
                trackNPC(npc);
        }
        mp.log("Fighter: getState(): Switch");
        switch(tracker.getStatus()) {
            case IDLE:
                fight_state = state.READY;
                break;
            case DEAD:
                fight_state = state.LOOT;
                break;
            case FIGHTING_PLAYER:
                fight_state = state.FIGHT;
                break;
            case FIGHTING_OTHER:
                fight_state = state.IDLE;
                break;
            case NO_TARGET:
                fight_state = state.IDLE;
                break;
        }
        return fight_state;
    }

    public void fight() {
        getState();
        mp.log("Fighter: State: "+ fight_state);
        switch (fight_state) {
            case    IDLE:
                checkHealth();
                getNpc();
                break;
            case    LOOT:
                checkHealth();
                lootNpc();
                break;
            case    FIGHT:
                checkHealth();
                break;
            case    READY:
                checkHealth();
                fightNpc();
                break;
            case    RUN:
                break;
        }
    }

    public void setLootables(String[] lootables) { this.lootables = lootables; }

    public void checkHealth() {
        if(api.myPlayer.shouldEat()) {
            if(api.myPlayer.hasFood()) {
                api.myPlayer.eat();
            } else {
                fight_state = state.RUN;
                api.util.findNearestBank(true);
            }
        }
    }

    public NPC getAttacker() {
        Optional<NPC> attacker = mp.npcs.getAll().stream().filter(x -> mp.myPlayer().equals(x.getInteracting())).findFirst();
        if(attacker.isPresent()) return attacker.get();
        else return null;
    }

    public void getNpc() {
        mp.log("GET NPC");
        List<NPC> npcs = mp.getNpcs().filter(npc ->
                npc != null && Arrays.asList(enemies).contains(npc.getName()) && mp.getMap().canReach(npc)
                    && npc.isAttackable()
            );

        if(npcs != null && !npcs.isEmpty()) {
            int[] distances = new int[npcs.size()];
            int min = Integer.MAX_VALUE;
            int index = 0;
            boolean npc_hot = false;
            for (int i = 0; i < npcs.size(); i++) {
                NPC temp = npcs.get(i);
                distances[i] = mp.getMap().realDistance(temp.getPosition(), mp.myPosition());
                Area npc_area = temp.getPosition().getArea(search_radius);
                List<Player> players = mp.getPlayers().filter(player -> npc_area.contains(player) && !player.getName().equals(my_name));
                for (Player player : players) {
                    if (player.isInteracting(npc)) {
                        npc_hot = true;
                        break;
                    }
                }
                if (!npc_hot && distances[i] < min) {
                    min = distances[i];
                    index = i;
                }
            }
            this.npc = npcs.get(index);
//            mp.log("Found NPC ");
            tracker.setNPC(npc);
            npc_name = npc.getName();
        }
        else {
//            mp.log("Cant find NPC");
        }
    }

    /**
     * Loots the npc
     * @return
     */
    public void lootNpc() {
        //  Tracker reports DEAD
        if(tracker.getStatus() == NpcTracker.status.DEAD) {
            //  Player has free Inventory spaces
            if(api.myPlayer.canLoot()) {
                int loot_checks = 0;
                List<GroundItem> loot = mp.getGroundItems().filter(item -> item.getPosition().distance(tracker.npc_position) <= 0);
                //  Check for dropped items, loop and wait in-between checks
                while (loot != null && loot.isEmpty() && loot_checks < 5) {
                    loot = mp.getGroundItems().filter(item -> item.getPosition().distance(tracker.npc_position) == 0);
                    Timing.waitCondition(() -> false, 150, 750);
                    loot_checks++;
                }
                // Found loot
                if (loot != null && !loot.isEmpty()) {
                    for (GroundItem item : loot) {
                        api.client.log("Loot: "+item.getName());
                        if(lootables != null && lootables.length > 0) {
                            if(!Arrays.asList(lootables).contains(item.getName())) {
                                continue;
                            }
                        }
                        long item_count_before = mp.getInventory().getAmount(item.getName());
                        item.interact("Take");
                        Timing.waitCondition(() -> mp.getInventory().getAmount(item.getName()) > item_count_before, 3000);
                        if(item.getName() == "Bones" && Math.random() > .5)
                            api.myPlayer.bury();
                    }
                }
            }
            // Set the npc to null after we have looted
            tracker.setNPC(null);
        }
    }

    /**
     * Fights the npc
     * @return
     */
    public void fightNpc() {
//        mp.log("Trying to Fight: "+tracker.getStatus());
        if(tracker.getStatus() == NpcTracker.status.IDLE) {
            npc.interact("Attack");
            Timing.waitCondition(() -> tracker.getStatus() == NpcTracker.status.FIGHTING_PLAYER  ||  api.myPlayer.tracker.getStatus() == MyPlayerTracker.status.IN_COMBAT, 250, 5000);
            if (tracker.getStatus() == NpcTracker.status.FIGHTING_PLAYER || api.myPlayer.tracker.getStatus() == MyPlayerTracker.status.IN_COMBAT) {
                fight_state = state.FIGHT;
            }
        }
    }

}
