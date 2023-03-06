package Core.Api.Trackers;

import Core.API;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.ui.Skill;
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

public class MyPlayerTracker extends Thread {

    private API api;
    private MethodProvider mp;

    public static status status;

    private boolean runTime = true;

    public status player_status;
    private Player my_player;
    private boolean is_moving;
    private int current_hp;
    private int max_hp;
    private boolean is_attackable;
    private String is_interacting;
    private boolean under_attack;
    public int my_health;
    public Position my_position;


    public MyPlayerTracker(API api) {
        this.api = api;
        mp = api.mp;

//        getStatus();
    }

    public void shutdown() {
        this.interrupt();
    }

    public enum status {
        IDLE,
        FIGHTING,
        IN_COMBAT,
        MOVING,
        BANKING,
        IN_DIALOGUE
    }

    public status getStatus() {
        updatePlayer();
        if(api.myPlayer.underAttack() && api.fighter.tracker.npc_status == NpcTracker.status.FIGHTING_PLAYER) player_status = MyPlayerTracker.status.FIGHTING;
        else if(api.myPlayer.underAttack() && api.fighter.tracker.npc_status == NpcTracker.status.FIGHTING_PLAYER) player_status = MyPlayerTracker.status.IN_COMBAT;
        else if(mp.getBank().isOpen()) player_status = MyPlayerTracker.status.BANKING;
        else if(mp.dialogues.isPendingOption() || mp.dialogues.isPendingContinuation()) player_status = MyPlayerTracker.status.IN_DIALOGUE;
        else if(is_moving) player_status = MyPlayerTracker.status.MOVING;
        else player_status = MyPlayerTracker.status.IDLE;
        return player_status;
    }

    private void updatePlayer() {
        my_player = mp.myPlayer();
        max_hp = mp.getSkills().getStatic(Skill.HITPOINTS);
        current_hp = mp.getSkills().getDynamic(Skill.HITPOINTS);
        my_health = (int)((double)current_hp / (double)max_hp * 100);
        my_position = mp.myPosition();
        under_attack = my_player.isUnderAttack();
        is_attackable = my_player.isAttackable();
        is_moving = my_player.isMoving();
        is_interacting = (my_player.getInteracting() == null) ? "" : my_player.getInteracting().getName();
    }

    public void run() {
        try {
            while(runTime) {
                getStatus();
                sleep(250);
            }
            this.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
