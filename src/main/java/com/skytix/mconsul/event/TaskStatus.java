package com.skytix.mconsul.event;

/**
 * Created by marc on 13/11/2016.
 */
public enum TaskStatus {
    TASK_STAGING,
    TASK_STARTING,
    TASK_RUNNING,
    TASK_FINISHED(true),
    TASK_FAILED(true),
    TASK_DROPPED(true),
    TASK_KILLED(true),
    TASK_KILLING(true),
    TASK_UNREACHABLE(true),
    TASK_LOST;

    private final boolean mTerminal;

    TaskStatus() {
        mTerminal = false;
    }

    TaskStatus(boolean aTerminal) {
        mTerminal = aTerminal;
    }

    public boolean isTerminal() {
        return mTerminal;
    }

}
