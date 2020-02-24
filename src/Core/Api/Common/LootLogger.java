package Core.Api.Common;

import Core.Api.API;

import java.util.HashMap;

public class LootLogger {

    private HashMap<String, Long> items = new HashMap<>();
    private API api;

    public LootLogger(API api) {
        this.api = api;
    }

    public void log(String name, long amount) {
        if(items.containsKey(name)) {
            long pre = items.get(name);
            long post = pre+amount;
            items.put(name, post);
        } else {
            items.put(name, amount);
        }
    }

    public long amount(String name) {
        return items.get(name);
    }

    public String print(String name) {
        return name+": "+items.get(name);
    }

    public boolean hasItems(HashMap<String, Integer> needs) {
        String[] these = needs.keySet().toArray(new String[needs.keySet().size()]);
        for (String item : these) {
            int req = needs.get(item);
            if(!items.containsKey(item) || items.get(item) < req) { return false; }
        } return true;
    }

}
