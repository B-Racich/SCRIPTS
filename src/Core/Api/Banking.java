package Core.Api;

import Core.Api.Common.Timing;
import org.osbot.rs07.Bot;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.script.MethodProvider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Banking {

    private API api;
    private Bot bot;
    private MethodProvider mp;

    public enum methods {
        DEPOSIT,
        DEPOSIT_ALL,
        WITHDRAW,
        DEPOSIT_WITHDRAW,
        DEPOSIT_ALL_WITHDRAW,
        DEPOSIT_ALL_EXCEPT_WITHDRAW
    }

    Banking(API api) {
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
                else return false;
            } else return false;
        } else return false;
    }

    public boolean close() {
        if(mp.getBank().isOpen()) return mp.getBank().close();
        else return false;
    }

    public void bank(HashMap<String, Integer> deposit, HashMap<String, Integer> withdraw, methods bm ) {
        api.util.log("Banking");
        String[] dep = deposit.keySet().toArray(new String[deposit.keySet().size()]);
        String[] wth = withdraw.keySet().toArray(new String[withdraw.keySet().size()]);
        switch(bm) {
            case DEPOSIT_ALL_WITHDRAW:
                if(open()) {
                    Timing.waitCondition(() -> mp.getBank().depositAll(), 250, 1000);
                    Timing.waitCondition(()->mp.getBank().withdraw(withdraw),250,2000);
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
        }
    }

}
