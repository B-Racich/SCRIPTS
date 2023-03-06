package Core.Api.Common.Interfaces;

import java.util.HashMap;

/**
 * Task extension for ApiScript
 */

public interface Task {
    HashMap<String, Integer> tasks = null;

    void setTask(HashMap<String, Integer> tasks);

    boolean hasTask();

    boolean completedTask();

}
