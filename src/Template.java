import Core.API;
import Core.Api.Common.Interfaces.ApiScript;

import java.awt.*;
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

    private API api;

    private long timeBegan;
    private long timeRan;

    public Template(API api) {
        this.api = api;
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
    public state getState() {
        return null;
    }
}
