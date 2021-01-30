package com.lirc572.ip;

public class TodoTask extends Task {
    public TodoTask(String name) {
        super(name);
    }

    @Override
    public String toSavedString() {
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
