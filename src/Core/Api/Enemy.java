package Core.Api;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.script.MethodProvider;

import java.util.List;

public class Enemy {

    /**
     * VARIABLES
     */
    private API api;
    private MethodProvider mp;
    private MyPlayer myPlayer;

    private String npc_name = "";
    private String my_name = "";
    private NPC npc;
    private int npc_index = 0;
    private state npc_state;
    private int npc_health = 0;
    private Position npc_position;
    private String npc_interacting = "";

    private int search_radius = 5;

    public enum state {
        NULL,
        IDLE,
        DEAD,
        FIGHT
    }

    public Enemy(MethodProvider mp, MyPlayer myPlayer, String npc_name) {
        this.mp = mp;
        this.myPlayer = myPlayer;
        this.npc_name = npc_name;
        my_name = mp.myPlayer().getName();
    }

    public Enemy(API api) {
        this.api = api;
        mp = api.mp;
        myPlayer = api.myPlayer;
        my_name = mp.myPlayer().getName();
    }

    public void setEnemy(String enemyName) {
        this.npc_name = enemyName;
    }

    public state action() {
        updateNpc();
        mp.log(npc_state);
        switch (npc_state) {
            case    NULL:
                getNpc();
                break;
            case    IDLE:
                fightNpc();
                break;
            case    DEAD:
                lootNpc();
                break;
            case    FIGHT:
                updateNpc();
                break;
        }
        return npc_state;
    }

    /**
     * Finds an npc
     * @return
     */
    public boolean getNpc() {
        List<NPC> npcs = mp.getNpcs().filter(npc -> npc != null && npc.getName().equals(npc_name) && (npc.getInteracting() == null || (npc.getInteracting() != null && npc.getInteracting().getName().equals(my_name))) && npc.getHealthPercent() > 0);
        mp.log(npcs.size());
        mp.log(npc_name);
        if(npcs != null && !npcs.isEmpty()) {
            int[] distances = new int[npcs.size()];
            int min = Integer.MAX_VALUE;
            int index = 0;
            boolean npc_hot = false;
            for (int i = 0; i < npcs.size(); i++) {
                NPC temp = npcs.get(i);
                distances[i] = temp.getPosition().distance(mp.myPosition());
                Area npc_area = temp.getPosition().getArea(search_radius);
                List<Player> players = mp.getPlayers().filter(player-> npc_area.contains(player) && !player.getName().equals(my_name));
                for (Player player : players) {
                    if(player.isInteracting(npc)) {
                        npc_hot = true;
                        break;
                    }
                }
                if(!npc_hot && distances[i] < min) {
                    min = distances[i];
                    index = i;
                }
            }
            mp.log("Found NPC");
            npc = npcs.get(index);
            npc_state = state.IDLE;
            return true;
        }
        mp.log("Cant find NPC");
        return false;
    }

    /**
     * Updates the NPC information
     */
    private void updateNpc() {
        if(npc != null && npc_state != state.NULL) {
            npc_health = npc.getHealthPercent();
            npc_interacting = (npc.getInteracting() == null) ? "" : npc.getInteracting().getName();
            npc_position = npc.getPosition();
            npc_index = npc.getIndex();
            if(npc_health > 0) {
                if(isFightingNpc()) { npc_state = state.FIGHT; }
                else if(validNpc()) { npc_state = state.IDLE; }
                else { npc_state = state.IDLE; }
            } else { npc_state = state.DEAD; }
        } else { npc_state = state.NULL; }
    }

    /**
     * Checks if the NPC is valid:
     * True:
     *  Is available to attack
     *  We are attacking
     * False:
     *  Otherwise
     * @return
     */
    public boolean validNpc() {
        if(npc != null) {
            //  If npc is interacting with another player
            if(!npc_interacting.equals("") && !npc_interacting.equals(my_name)) { mp.log(1); return false; }
            //  If we are fighting npc
            else if(isFightingNpc()) { mp.log(2); return true; }
            //  If npc is interacting with us
            else if(!npc_interacting.equals("") && npc_interacting.equals(my_name)) { mp.log(3); return true; }
            else { return true; }
        } else { return false; }
    }

    /**
     * Checks if we are fighting the NPC
     * @return boolean
     */
    public boolean isFightingNpc() {
        if(npc_interacting.equals(my_name) && mp.myPlayer().getInteracting().getName().equals(npc_name)) { return true; }
        else { return false; }
    }

    /**
     * Loots the npc
     * @return
     */
    public boolean lootNpc() {
        if(npc_state == state.DEAD) {
            if(api.myPlayer.canLoot()) {
                mp.log(npc_position);
                int loot_checks = 0;
                List<GroundItem> loot = mp.getGroundItems().filter(item -> item.getPosition().distance(npc_position) <= 1);
                while (loot != null && loot.isEmpty() && loot_checks < 5) {
                    loot = mp.getGroundItems().filter(item -> item.getPosition().distance(npc_position) == 0);
                    Timing.waitCondition(() -> false, 150, 750);
                    loot_checks++;
                }
                mp.log(loot.size());
                if (loot != null && !loot.isEmpty()) {
                    mp.log("Try to get loot");
                    for (GroundItem item : loot) {
                        long item_count_before = mp.getInventory().getAmount(item.getName());
                        item.interact("Take");
                        Timing.waitCondition(() -> mp.getInventory().getAmount(item.getName()) > item_count_before, 3000);
                    }
                    npc_state = state.NULL;
                    return true;
                } else {
                    npc_state = state.NULL;
                    return false;
                }
            } else { npc_state = state.NULL; return false; }
        } else { return false; }
    }

    /**
     * Checks if the NPC is eligble for fighting:
     * Checks if NPC is valid
     * @return
     */
    public boolean shouldFight() {
        if(validNpc()) {
            if(npc_state == state.IDLE) { return true; }
            else { return false; }
        }
        mp.log("Dont fight");
        npc_state = state.NULL;
        return false;
    }

    /**
     * Fights the npc
     * @return
     */
    public boolean fightNpc() {
        if(shouldFight()) {
            mp.log("Trying to fight");
            npc.interact("Attack");
            Timing.waitCondition(() -> isFightingNpc() || !validNpc(),250, 5000);
            if(isFightingNpc()) {
                npc_state = state.FIGHT;
                return true;
            } else if(!validNpc()) { npc_state = state.NULL; return false; }
            else { return false; }
        } else { return false; }
    }

}
