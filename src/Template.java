import Core.API;
import Core.Api.Common.ApiScript;
import Core.Client;

import java.awt.*;
import java.util.HashMap;

/**
 * The basic script template
 *
 * state - can be used to setup a state based script, otherwise create a regular onLoop script within the run() method
 *
 * pause() - if setup can be used by other classes to pause the script when needed
 *
 * if tasks are supported complete the task methods
 */

public class Template implements ApiScript {

    private Client client;
    private API api;

    private long timeBegan;
    private long timeRan;

    public Template(Client client) {
        this.client = client;
        api = client.api;
    }

    enum state {}

    @Override
    public void run() {

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
        return false;
    }

    @Override
    public boolean completedTask() {
        return false;
    }

    @Override
    public state getState() {
        return null;
    }
}
