package Core.Api.Common.Interfaces;

/**
 * Quest extension for ApiScript
 */

public interface Quest {
    int quest_id = 0;
    int quest_state = 0;

    boolean isCompleted();
}
