package Core.Api;

import Core.API;
import Core.Api.Common.Timing;
import org.osbot.rs07.Bot;
import org.osbot.rs07.api.GrandExchange;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.script.MethodProvider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Banking {

    private API api;
    private Bot bot;
    private MethodProvider mp;

    /**
     * DEPOSIT,
     * DEPOSIT_ALL,
     * DEPOSIT_ALL_EXCEPT,
     * WITHDRAW,
     * DEPOSIT_WITHDRAW,
     * DEPOSIT_ALL_WITHDRAW,
     * DEPOSIT_ALL_EXCEPT_WITHDRAW
     */
    public enum methods {
        DEPOSIT,
        DEPOSIT_ALL,
        DEPOSIT_ALL_EXCEPT,
        WITHDRAW,
        DEPOSIT_WITHDRAW,
        DEPOSIT_ALL_WITHDRAW,
        DEPOSIT_ALL_EXCEPT_WITHDRAW
    }

    public Banking(API api) {
        this.api = api;
        bot = api.bot;
        mp = api.mp;
    }

    public boolean open() {
        api.util.log("Open bank");
        List<NPC> bankers = mp.getNpcs().filter(npc -> npc.getName().equals("Banker"));
        if(bankers != null && !bankers.isEmpty()) {
            Iterator<NPC> bankerItr = bankers.iterator();
            if(bankerItr.hasNext()) {
                NPC banker = bankerItr.next();
                banker.interact("Bank");
                Timing.waitCondition(() -> mp.getBank().isOpen(), 250, 2000);
                if(mp.getBank().isOpen()) return true;
            }
        }
        return false;
    }

    public boolean openGE() {
        api.util.log("Open GE");
        List<NPC> bankers = mp.getNpcs().filter(npc -> npc.getName().equals("Grand Exchange Clerk"));
        if(bankers != null && !bankers.isEmpty()) {
            Iterator<NPC> bankerItr = bankers.iterator();
            if(bankerItr.hasNext()) {
                NPC banker = bankerItr.next();
                banker.interact("Exchange");
                Timing.waitCondition(() -> mp.getBank().isOpen(), 250, 2000);
                if(mp.getBank().isOpen()) return true;
            }
        }
        return false;
    }

    public GrandExchange.Box getEmptySlotGE() {
        if(mp.getGrandExchange().isOpen()) {
            if(mp.getGrandExchange().getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.EMPTY) {
                return GrandExchange.Box.BOX_1;
            }
            if(mp.getGrandExchange().getStatus(GrandExchange.Box.BOX_2) == GrandExchange.Status.EMPTY) {
                return GrandExchange.Box.BOX_2;
            }
            if(mp.getGrandExchange().getStatus(GrandExchange.Box.BOX_3) == GrandExchange.Status.EMPTY) {
                return GrandExchange.Box.BOX_3;
            }
        }
        return null;
    }

    public boolean buyItem(String item) {
        if(mp.getGrandExchange().isOpen()) {
//            int chocolatePrice = mp.grandExchange.g
//           mp.getGrandExchange().buy
        }
        return false;
    }

    public boolean close() {
        if(mp.getBank().isOpen()) return mp.getBank().close();
        else return false;
    }

    public void bank(methods bm ) {
        api.util.log("Banking");
        switch(bm) {
            case DEPOSIT_ALL:
                if(open()) {
                    Timing.waitCondition(() -> mp.getBank().depositAll(), 250, 1000);
                    api.util.log("Banked");
                }
                break;
        }
    }

    /**
     *  Banking method that uses HashMaps for Withdraw and Deposit parameters, supply a banking method state
     *  to control the banking operations performed.
     * @param deposit <String, Integer>
     * @param withdraw <String, Integer>
     * @param bm One of methods
     * @see methods
     */
    public void bank(HashMap<String, Integer> deposit, HashMap<String, Integer> withdraw, methods bm ) {
        api.util.log("Banking");
        if(deposit == null) deposit = new HashMap<>();
        if(withdraw == null) withdraw = new HashMap<>();
        String[] dep = deposit.keySet().toArray(new String[deposit.keySet().size()]);
        String[] wth = withdraw.keySet().toArray(new String[withdraw.keySet().size()]);
        switch(bm) {
            case DEPOSIT_ALL_WITHDRAW:
                if(open()) {
                    Timing.waitCondition(() -> mp.getBank().depositAll(), 250, 1000);
                    HashMap<String, Integer> finalWithdraw = withdraw;
                    Timing.waitCondition(()->mp.getBank().withdraw(finalWithdraw),250,2000);
                    api.util.log("Banked");
                }
                break;
            case DEPOSIT_ALL_EXCEPT_WITHDRAW:
                if(open()) {
                    mp.getBank().depositAllExcept(dep);
                    Timing.waitCondition(() -> mp.getInventory().onlyContains(dep), 2000);
                    if (mp.getBank().contains(wth)) {
                        mp.getBank().withdraw(withdraw);
                        Timing.waitCondition(() -> mp.getInventory().contains(wth), 2000);
                    }
                }
                break;
            case DEPOSIT_ALL_EXCEPT:
                if(open()) {
                    mp.getBank().depositAllExcept(dep);
                    Timing.waitCondition(() -> mp.getInventory().onlyContains(dep), 2000);
                }
                break;
        }
    }

    public boolean findBank() {
        api.util.log("Finding bank");
        return true;
    }

}
