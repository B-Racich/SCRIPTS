package Core.Api;

import Core.API;
import Core.Api.Common.Timing;
import org.osbot.rs07.Bot;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.MethodProvider;

import java.util.HashMap;

public class Miner {

    private API api;
    private Bot bot;
    private MethodProvider mp;
    private HashMap<String, Integer> tasks;
    private boolean taskSet = false, taskComplete = false;

    public Miner(API api) {
        this.api = api;
    }

    public enum Rock {
        CLAY(new short[]{6705}),
        COPPER(new short[]{4645, 4510}),
        TIN(new short[]{53}),
        IRON(new short[]{2576}),
        SILVER(new short[]{74}),
        COAL(new short[]{10508}),
        GOLD(new short[]{8885}),
        MITHRIL(new short[]{-22239}),
        ADAMANTITE(new short[]{21662}),
        RUNITE(new short[]{-31437});

        private short[] colours;

        Rock(final short[] colours) {
            this.colours = colours;
        }

        public boolean hasOre(final Entity rockEntity) {
            if (rockEntity.getDefinition() == null) {
                return false;
            }

            short[] colours = rockEntity.getDefinition().getModifiedModelColors();

            if (colours == null) {
                return false;
            }

            for (short rockColour : this.colours) {
                for (short entityColour : colours) {
                    if (rockColour == entityColour) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public void setTask(HashMap<String, Integer> tasks) {
        taskSet = true;
        taskComplete = false;
        this.tasks = tasks;
    }

    public boolean completedTask() {
        if(taskSet && !taskComplete && api.logger.hasItems(tasks)) {
            taskComplete = true;
            return true;
        } return false;
    }

    public boolean mine(Rock rock) {
        if(!mp.myPlayer().isAnimating()) {
            RS2Object available_rock = mp.getObjects().closest(ob -> rock.hasOre(ob));
            if(available_rock != null && available_rock.interact("Mine")) {
                Timing.waitCondition(() -> !available_rock.exists(), 250, 5000);
                if(!available_rock.exists()) return true;
                else return false;
            } else return false;
        } else return false;
    }
}
