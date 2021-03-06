package com.lirc572.ip;

/**
 * Represents a todo task (nothing more than a general task).
 */
public class TodoTask extends Task {

    /**
     * Constructs a new TodoTask with the specified name.
     *
     * @param name The name of the task.
     */
    public TodoTask(String name) {
        super(name);
    }

    /**
     * Returns the string representation of the TodoTask for storage.
     *
     * @return The string representation of the TodoTask for storage.
     */
    @Override
    public String getSavedString() {
        return String.format(
                "T | %d | %s",
                this.getIsDone() ? 1 : 0,
                this.getName()
        );
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
