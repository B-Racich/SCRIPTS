package Core.Api.Common.Interfaces;

import java.awt.*;

/**
 * This interface is a replacement for extending the osBot Script class
 *
 * By implementing this interface the Client class can run the script concurrently/parallel to other Classes such as
 * the Antiban and Camera.
 *
 * run() - the onLoop method of osBot, main Method
 *
 * pause() - pause functionality for other components to pause the script if supported
 */

public interface ApiScript {

    <state> state getState();

    void run() throws NullPointerException;

    void pause();

    void paint(Graphics2D g);
}
