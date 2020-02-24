package Core.Api.Common;

import java.awt.*;
import java.util.HashMap;

public interface ApiScript {

    void run();

    void pause();

    void paint(Graphics2D g);

    HashMap<String, Integer> tasks = null;

    void setTask(HashMap<String, Integer> tasks);

    boolean hasTask();

    boolean completedTask();

}
