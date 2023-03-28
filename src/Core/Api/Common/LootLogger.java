package Core.Api.Common;

import Core.API;

import java.util.HashMap;

public class LootLogger {

    private HashMap<String, Long> items = new HashMap<>();

    public LootLogger(API api) {
        API api1 = api;
    }

    public void log(String name, long amount) {
        if(items.containsKey(name)) {
            items.put(name, items.get(name)+amount);
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

    /**
     * Looks in the logged items for the correct items+counts
     * @param needs HashMap<String, Integer>
     * @return boolean
     */
    public boolean hasItems(HashMap<String, Integer> needs) {
        String[] these = needs.keySet().toArray(new String[needs.keySet().size()]);
        for (String item : these) {
            int req = needs.get(item);
            if(!items.containsKey(item) || items.get(item) < req) { return false; }
        } return true;
    }

}
