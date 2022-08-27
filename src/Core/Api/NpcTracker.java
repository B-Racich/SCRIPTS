package Core.Api;

import Core.API;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.script.MethodProvider;

/**
 * Golden tidbits
 *
 * script.players.getAll().stream().filter(x -> script.myPlayer().equals(x.getInteracting())).findFirst().isPresent();
 * // there is a player ATTEMPTING to attack, not necessarily already attacked
 *
 * script.myPlayer().isHitBarVisible();
 * // only works if the attack triggered health bar display (not a splashed spell)
 *
 * script.projectiles.getAll().stream().filter(x -> script.myPlayer().equals(x.getTargetEntity())).findFirst().isPresent();
 * // a ranged/magic attack was launched against the bot
 */

public class NpcTracker extends Thread {

    private API api;
    private MethodProvider mp;

    public static status status;

    public NPC npc;
    public int npc_health;
    public String npc_target;
    public boolean npc_attackable;
    public boolean npc_fighting;
    public boolean npc_animating;
    public boolean npc_visible;
    public boolean npc_moving;
    public Position npc_position;
    public status npc_status;

    public NpcTracker(API api) {
        this.api = api;
        mp = api.mp;
    }

    public NpcTracker(API api, NPC npc) {
        this.api = api;
        mp = api.mp;

        if(npc != null) this.npc = npc;
        getStatus();
    }

    public void shutdown() {
        this.interrupt();
    }

    public void setNPC(NPC npc) {
        this.npc = npc;
        getStatus();
    }

    enum status {
        IDLE,
        DEAD,
        FIGHTING_PLAYER,
        FIGHTING_OTHER,
        NO_TARGET
    }

    public status getStatus() {
        if(updateNPC()) {
            if(npc_health > 0 && !npc_fighting && npc_target == "") npc_status = status.IDLE;
            else if(npc_health <= 0) npc_status = status.DEAD;
            else if(npc_attackable && npc_fighting && npc_target == mp.myPlayer().getName()) npc_status = status.FIGHTING_PLAYER;
            else if(npc_fighting && npc_target != mp.myPlayer().getName()) npc_status = status.FIGHTING_OTHER;
            else npc_status = status.NO_TARGET;
        }
        else if(api.myPlayer.underAttack()) {
            NPC attacker = api.fighter.getAttacker();
            if(attacker != null)
                setNPC(attacker);
        }
        else
            npc_status = status.NO_TARGET;
//        mp.log("NpcTracker: getStatus: "+player_status);
        return npc_status;
    }

    private boolean updateNPC() {
//        mp.log("NpcTracker: updateNPC");
        if(npc == null) return false;
        npc_health = npc.getHealthPercentCache();
        npc_target = npc.getInteracting() != null ? npc.getInteracting().getName() : "";
        npc_attackable = npc.isAttackable();
        npc_fighting = npc.isUnderAttack();
        npc_animating = npc.isAnimating();
        npc_moving = npc.isMoving();
        npc_visible = npc.isVisible();
        npc_position = npc.getPosition();
//        mp.log("NpcTracker: updateNPC Complete");
        return true;
    }

    public void run() {
        try {
            while(npc_status != status.NO_TARGET) {
                if (mp.getMap().realDistance(npc_position, mp.myPosition()) >= 30) shutdown();
                getStatus();
                sleep(250);
            }
            this.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
