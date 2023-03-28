package Core.Api.Modules;

import Core.API;
import Core.Api.Common.Timing;
import Core.Api.Trackers.MyPlayerTracker;
import Core.Api.Trackers.NpcTracker;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.*;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.script.MethodProvider;

import java.util.*;

public class MyPlayer {
    public MyPlayerTracker tracker;
    /**
     * #Core
     */
    private API api;
    private MethodProvider mp;

    /**
     * #Settings
     */
    private int eatAt;
    private String foodType;
    private int foodAmt;
    private boolean shouldBuryBones = false;

    private String[] foodStrings = new String[]{"Cooked meat"};
    private int max_hp;
    private int current_hp;
    public Entity targetEn;

    public MyPlayer(API api) {
        this.api = api;
        mp = api.mp;

        tracker = new MyPlayerTracker(api);
        tracker.start();
    }

    public void shutdown() { if(tracker != null) tracker.shutdown();}

    /**
     * #Settings
     */

    public void setEatAt(int eatAt) {
        this.eatAt = eatAt;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public void setShouldBuryBones(boolean shouldBuryBones) {
        this.shouldBuryBones = shouldBuryBones;
    }

    /**
     *  [Movement]
     */

    public boolean inArea(Area area) {
        return area.contains(tracker.my_position);
    }

    public boolean isWithin(Position pos, int radius) {
        return pos.distance(tracker.my_position) <= radius;
    }

    public boolean moveTo(Area area) {
        return Timing.waitCondition(() -> mp.getWalking().webWalk(area), 3000);
    }

    public void moveTo(Position pos) {
        Timing.waitCondition(() -> mp.getWalking().webWalk(pos), 3000);
    }

    /**
     *  [Logic]
     */
    public boolean shouldEat() {
        return tracker.my_health <= eatAt;
    }

    public boolean shouldFlee() {
        if(api.fighter.tracker.getStatus() == NpcTracker.status.FIGHTING_PLAYER || underAttack()) {
            if(shouldEat()) return true;
            else {
                /**
                 * TODO: Implement a combat logger to track damage and estimate the kill chance
                 */
                return true;
            }
        }
        else
            return false;
    }

    public boolean hasFood() {
        return mp.getInventory().contains(foodStrings);
    }

    /**
     * Checks if the player should bank based on two conditions:
     *
     * 1. Player is out of food and requires it
     * 2. Inventory is full
     * @return
     */
    public boolean shouldBank(boolean checkFood) {
        if(checkFood) {
            if(!mp.getInventory().contains(foodStrings))
                return true;
        }
        if(mp.getInventory().getEmptySlots() <= 0) {
            if(hasBones() && shouldBuryBones) {
                bury();
                return shouldBank(checkFood);
            } else { return true; }
        } else { return false; }
    }

    public boolean shouldBank() {
        if(mp.getInventory().getEmptySlots() <= 0) {
            if(hasBones() && shouldBuryBones) {
                bury();
                shouldBank();
                return false;
            } else { return true; }
        } else { return false; }
    }

    /**
     * Checks for:
     * 1. hp > eatAT
     * 2. underAttack
     * @return
     */
    public boolean ableToFight() {
        return !shouldEat() && !underAttack();
    }

    public boolean underAttack() {
        boolean check_1 =  mp.players.getAll().stream().filter(x -> mp.myPlayer().equals(x.getInteracting())).findFirst().isPresent();
        // there is a player ATTEMPTING to attack, not necessarily already attacked
        boolean check_2 =  mp.myPlayer().isHitBarVisible();
        // only works if the attack triggered health bar display (not a splashed spell)
        boolean check_3 =  mp.projectiles.getAll().stream().filter(x -> mp.myPlayer().equals(x.getTargetEntity())).findFirst().isPresent();
        // a ranged/magic attack was launched against the bot

        if(check_1 || check_2 || check_3) return true;
        return tracker.player_status == MyPlayerTracker.status.FIGHTING;
    }

    public boolean canLoot() {
        return mp.getInventory().getEmptySlots() > 0;
    }


    public boolean hasBones() {
        List<Item> bones = mp.getInventory().filter(item -> item.getName().contains("bones") || item.getName().contains("Bones"));
        return bones != null && !bones.isEmpty();
    }

    public boolean hasEquipped(EquipmentSlot slot, String item) {
        return mp.getEquipment().isWearingItem(slot, item);
    }

    public boolean hasItem(String item) {
        return mp.getInventory().contains(item);
    }

    public boolean hasItem(String item, int amt) {
        return mp.getInventory().contains(item) && mp.getInventory().getAmount(item) >= amt;
    }

    public boolean isBusy(boolean combatCheck) {
        if(mp.myPlayer().isAnimating() || mp.myPlayer().isMoving()) {
            if(combatCheck && mp.myPlayer().isUnderAttack()) {
                return true;
            }
            else return true;
        } else return false;
    }

    public boolean isIdle(boolean combatCheck) {
        return !isBusy(combatCheck);
    }

    public boolean equip(String item) {
        if(mp.getInventory().contains(item)) {
            mp.getInventory().interact("Wear", item);
            Timing.waitCondition(() -> !mp.getInventory().contains(item), 250, 2000);
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if(mp.getEquipment().isWearingItem(slot, item)) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    public boolean eat() {
        List<Item> foods = mp.getInventory().filter(item -> Arrays.asList(foodStrings).contains(item.getName()));
        if(foods != null && !foods.isEmpty()) {
            Iterator<Item> foodItr = foods.iterator();
            while(foodItr.hasNext()) {
                final long food_count = mp.getInventory().getAmount(foodType);
                if(shouldEat()) {
                    final Item food = foodItr.next();
                    food.interact("Eat");
                    Timing.waitCondition(() -> mp.getInventory().getAmount(foodType) < food_count, 1000);
                } else if(!shouldEat()) { break; }
            }
            return true;
        } else { return false; }
    }

    public boolean bury() {
        if(hasBones()) {
            List<Item> bones = mp.getInventory().filter(item -> item.getName().contains("bones") || item.getName().contains("Bones"));
            if(bones != null  && !bones.isEmpty()) {
                Iterator<Item> boneItr = bones.iterator();
                while(boneItr.hasNext()) {
                    final Item bone = boneItr.next();
                    final long bone_count = mp.getInventory().getAmount(bone.getName());
                    bone.interact("Bury");
                    Timing.waitCondition(() -> mp.getInventory().getAmount(bone.getName()) < bone_count, 1000);
                }
                return true;
            } else { return false; }
        } else { return false; }
    }

}
