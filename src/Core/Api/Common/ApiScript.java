package Core.Api.Common;

import Core.Client;

import java.awt.*;
import java.util.HashMap;

/**
 * This interface is a replacement for extending the osBot Quester class
 *
 * By implementing this interface the Client class can run the script concurrently/parallel to other Classes such as
 * the Antiban and Camera.
 *
 * run() - the onLoop method of osBot, main Method
 *
 * pause() - pause functionality for other components to pause the script if supported
 *
 * tasks - A script can have tasks which can be used to designate a botting goal
 */

public interface ApiScript {

    void run() throws NullPointerException;

    void pause();

    void paint(Graphics2D g);

    HashMap<String, Integer> tasks = null;

    void setTask(HashMap<String, Integer> tasks);

    boolean hasTask();

    boolean completedTask();

    <state> state getState();

}
