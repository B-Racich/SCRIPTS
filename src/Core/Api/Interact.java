package Core.Api;

import Core.API;
import Core.Api.Common.Timing;
import org.osbot.rs07.Bot;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.*;
import org.osbot.rs07.script.MethodProvider;

import java.util.function.BooleanSupplier;

public class Interact {

    private API api;
    private Bot bot;
    private MethodProvider mp;

    public Interact(API api) {
        this.api = api;
        bot = api.bot;
        mp = api.mp;
    }

    /**
     * NPC Interactions
     */

    public boolean moveToPosAnd(Position pos, int dist, BooleanSupplier condition) {
        if(pos.distance(api.mp.myPosition()) <= dist) {
            return condition.getAsBoolean();
        }
        else if(api.myPlayer.isIdle(false))
            api.myPlayer.moveTo(pos);
        return false;
    }

    public boolean moveToAreaAnd(Area area, BooleanSupplier condition) {
        if(area.contains(api.mp.myPlayer())) {
            return condition.getAsBoolean();
        }
        else if(api.myPlayer.isIdle(false))
            api.myPlayer.moveTo(area);
        return false;
    }

    public boolean interactNpc(String name, String act) {
        NPC ob = mp.getNpcs().closest(name);
        if(ob != null) {
            return Timing.waitCondition(()->ob.interact(act),2500);
        }
        return false;
    }

    public boolean isTalking() {
        if(!mp.getWidgets().containingText("Click here to continue").isEmpty()) {
            if(mp.getWidgets().containingText("Click here to continue").get(0).isVisible()) {
                return true;
            }
        }
        else if(!mp.getWidgets().containingText("Please wait...").isEmpty()) {
            if(mp.getWidgets().containingText("Please wait...").get(0).isVisible()) {
                return true;
            }
        }
        else if(mp.getDialogues().isPendingContinuation())
            return true;
        else if(mp.getDialogues().inDialogue())
            return true;
        return false;
    }

    public boolean clickContinue() {
        if(mp.getWidgets().containingText("Click here to continue") != null) {
            if(!mp.getWidgets().containingText("Click here to continue").isEmpty()) {
                Timing.waitCondition(()->mp.getDialogues().clickContinue(), 1000);
                Timing.waitCondition(()->!isTalking(), 1000);
                return true;
            }
        }
        return false;
    }

    public boolean talkNPC(String npcName) {
        if (isTalking()) {
            clickContinue();
            return true;
        } else {
            NPC ob = mp.getNpcs().closest(npcName);
            if(ob != null) {
                ob.interact("Talk-to");
                Timing.waitCondition(()->isTalking(),2500);
            }
            return false;
        }
    }

    public void talkNPC(String npcName, String[] choices) {
        NPC ob = mp.getNpcs().closest(npcName);
        if(ob != null) {
            if(!isTalking())
                ob.interact("Talk-to");
            Timing.waitCondition(() -> isTalking(), 5000);
            while(isTalking()) {
                if(mp.getDialogues().isPendingOption())
                    if(!mp.getDialogues().selectOption(choices)) break;
                else if(mp.getDialogues().isPendingContinuation())
                    clickContinue();
            }
        }
    }

    public boolean talkNPC(String npcName, int[] choices) {
        NPC ob = mp.getNpcs().closest(npcName);
        if(ob != null) {
            int i = 0;
            if(!isTalking())
                ob.interact("Talk-to");
            Timing.waitCondition(() -> isTalking(), 5000);
            while(isTalking()) {
                if(mp.getDialogues().isPendingContinuation()) {
                    clickContinue();
                    Timing.waitCondition(() -> isTalking(), 5000);
                }
                else if(mp.getDialogues().isPendingOption()) {
                    if(i >= choices.length) break;
                    mp.getDialogues().selectOption(choices[i]);
                    i++;
                    Timing.waitCondition(() -> isTalking(), 5000);
                }
            }
        }
        return true;
    }

    /**
     *  Object, Item, Etc Interactions
     */

    public boolean shop(String name, String item, int amt) {
        NPC npc = mp.getNpcs().closest(name);
        if (npc != null) {
            if(!mp.getStore().isOpen()) {
                Timing.waitCondition(()-> mp.getStore().isOpen(),2500);
                return false;
            } else {
                return Timing.waitCondition(()->mp.getStore().buy(item, amt),2500);
            }
        }
        return false;
    }

    public boolean interactOb(String name, String act) {
        RS2Object ob = mp.getObjects().closest(name);
        if(ob != null) {
            return Timing.waitCondition(() -> ob.interact(act), 3500);
        }
        return false;
    }

    public boolean interactOb(String name, String act, String expectedItem) {
        RS2Object ob = mp.getObjects().closest(name);
        if(ob != null) {
            return Timing.waitCondition(() -> ob.interact(act), 2500);
        }
        return false;
    }

    public void useItemWithObject(String item, String object) {
        RS2Object ob = mp.getObjects().closest(object);
        Item inventoryItem = mp.getInventory().getItem(item);
        if(ob != null && inventoryItem != null) {
            inventoryItem.interact("Use");
            Timing.waitCondition(() -> mp.getInventory().isItemSelected(), 2500);
            if(mp.getInventory().isItemSelected()) {
                ob.interact("Use");
                Timing.waitCondition(() -> mp.myPlayer().isMoving() || mp.myPlayer().isAnimating(), 2500);
                Timing.waitCondition(() -> !mp.myPlayer().isMoving() && !mp.myPlayer().isAnimating(), 2500);
            }
        }
    }

    public boolean pickUpItem(String itemName) {
        GroundItem item = mp.getGroundItems().closest(itemName);
        if(item != null) {
            return Timing.waitCondition(()->item.interact("Take"),2500);
        }
        return false;
    }

    public boolean waitForInventoryChange(int waitTime) {
        int pre = mp.getInventory().getEmptySlots();
        Timing.waitCondition(() -> pre != mp.getInventory().getEmptySlots(), waitTime);
        if(pre != mp.getInventory().getEmptySlots())
            return true;
        else
            return false;
    }

    public void interact(Entity en) {
        //	Check if en is on-screen
        if(!en.isVisible()) {
            api.camera.lookAt(en);
            if(!mp.myPlayer().isVisible()) {
                api.camera.lookAt(mp.myPlayer());
            }
        }
        //	Interact
        String[] interactions = {"Chop down", "Pick", "Take", "Pick-from", "Open", "Climb-up", "Climb-down",
                "Climb up", "Climb down", "Dig", "Spin"};
        Timing.waitCondition(() -> en.interact(interactions), 3000);
    }

    public boolean combineItems(String item1, String item2) {
        Item[] pre = mp.getInventory().getItems();
        if(mp.getInventory().contains(item1) && mp.getInventory().contains(item2)) {
            mp.getInventory().interact("Use", item1);
            Timing.waitCondition(() -> mp.getInventory().isItemSelected(), 2000);
            if(mp.getInventory().isItemSelected()) {
                mp.getInventory().interact("Use", item2);
                waitForInventoryChange(10000);
                return true;
            }
        }
        return false;
    }

}
