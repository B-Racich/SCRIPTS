package Scripts;

import Core.API;
import Core.Api.Common.ApiScript;
import Core.Client;
import org.osbot.rs07.script.MethodProvider;

import java.awt.*;
import java.util.HashMap;

public class AIO_ORBS implements ApiScript {

    private Client client;
    private API api;
    private MethodProvider mp;
    private RuneCrafter.states state;
    private boolean firstRun = true;
    public RuneCrafter.Altar altar;
    public boolean runTime = false;

    public int gp = 0;

    enum states {

    }

    AIO_ORBS(Client client) {
        this.client = client;
        api = client.api;
        mp = api.mp;
    }

    @Override
    public void run() {
        if(firstRun) {
            firstRun = false;
        }
        try {
            execute();
        } catch(NullPointerException e) {
            api.util.log(e.toString() + " - "+ state);
        }
    }

    private void execute() {
        api.util.log(state.name());
        switch(state) {

        }
    }

    public void setup() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void paint(Graphics2D g) {

    }

    @Override
    public void setTask(HashMap<String, Integer> tasks) {

    }

    @Override
    public boolean hasTask() {
        if(tasks !=  null && tasks.size() > 0) return true;
        else return false;
    }

    @Override
    public boolean completedTask() {
        if(hasTask())
            if(api.logger.hasItems(tasks)) return true;
        return false;
    }

    @Override
    public <state> state getState() {
        return null;
    }
}
