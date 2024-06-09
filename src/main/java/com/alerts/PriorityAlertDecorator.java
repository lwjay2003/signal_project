package com.alerts;

public class PriorityAlertDecorator extends AlertDecorator {
    private final int priorityLevel;

    public PriorityAlertDecorator(Alert decoratedAlert, int priorityLevel) {
        super(decoratedAlert);
        this.priorityLevel = priorityLevel;
    }

    @Override
    public String toString() {
        return decoratedAlert.toString() + ", Priority Level " + priorityLevel;
    }
}
