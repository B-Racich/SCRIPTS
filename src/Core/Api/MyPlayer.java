package Core.Api;

import Core.Api.Common.Timing;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.script.MethodProvider;

import java.util.Iterator;
import java.util.List;

public class MyPlayer {
    /**
     * #Core
     */
    private API api;
    private MethodProvider mp;

    /**
     * #Player Info
     */
    private int my_health;
    private Position my_position;
    private boolean under_attack;
    private boolean is_attackable;
    private boolean is_moving;
    private String is_interacting;
    private Player my_player;

    /**
     * #Settings
     */
    private int eatAt = 50;
    private String foodType;
    private int foodAmt;
    private boolean shouldBuryBones = false;

    MyPlayer(API api) {
        this.api = api;
        mp = api.mp;
    }

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

    public void update() {
        my_player = mp.myPlayer();
        my_health = my_player.getHealthPercent();
        my_position = mp.myPosition();
        under_attack = my_player.isUnderAttack();
        is_attackable = my_player.isAttackable();
        is_moving = my_player.isMoving();
        is_interacting = (my_player.getInteracting() != null) ? my_player.getInteracting().getName() : "";
    }

    /**
     *  [Movement]
     */

    public boolean inArea(Area area) {
        update();
        if(area.contains(my_player)) { return true; }
        else { return false; }
    }

    public void moveTo(Area area) {
        Timing.waitCondition(() -> mp.getWalking().webWalk(area), 3000);
    }

    /**
     *  [Logic]
     */
    public boolean shouldEat() {
        update();
        if(my_health <= eatAt && hasFood(false)) { return true; }
        else if(my_health <= eatAt && hasFood(true)) { return true; }
        else { return false; }
    }

    public boolean hasFood(boolean anyFood) {
        update();
        if(anyFood) {
            if(foodType != null && !foodType.equals("")) {
                if(mp.getInventory().contains(foodType)) { return true; }
                else { return false; }
            } else { return false;}
        } else {
            List<Item> items = mp.getInventory().filter(item -> item.hasAction("Eat"));
            if(items != null && !items.isEmpty()) { return true; }
            else { return false; }
        }
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

    public boolean ableToFight() {
        update();
        if(my_health > eatAt && !underAttack()) { return true; }
        else { return false; }
    }

    public boolean underAttack() {
        update();
        List<NPC> npcs = mp.getNpcs().filter(npc -> npc.getInteracting() != null && npc.getInteracting().getName().equals(my_player.getName()));
        if(under_attack) { return true; }
        else if(npcs != null && !npcs.isEmpty()) { return true; }
        else if(is_interacting.equals("") && !under_attack) { return false; }
        else { return false; }
    }

    public boolean canLoot() {
        if(mp.getInventory().getEmptySlots() > 0) { return true; }
        else { return false; }
    }


    public boolean hasBones() {
        List<Item> bones = mp.getInventory().filter(item -> item.getName().contains("bones") || item.getName().contains("Bones"));
        if(bones != null && !bones.isEmpty()) { return true; }
        else { return false; }
    }

    public boolean hasEquiped(EquipmentSlot slot, String item) {
        if(mp.getEquipment().isWearingItem(slot, item)) { return true; }
        else return false;
    }

    /**
     *  [Actions]
     */

    public void interactOb(String name, String act) {
        RS2Object ob = mp.getObjects().closest(name);
        if(ob != null) {
            ob.interact(act);
        }
    }

    public void interactNpc(String name, String act) {
        NPC ob = mp.getNpcs().closest(name);
        if(ob != null) {
            ob.interact(act);
        }
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
        List<Item> foods = mp.getInventory().filter(item -> item.getName().equals(foodType));
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
