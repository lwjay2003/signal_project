package com.alerts;

import com.alerts.Alert;
import com.alerts.AlertDecorator;

public class RepeatedAlertDecorator extends AlertDecorator {
    private final int repeatCount;

    public RepeatedAlertDecorator(Alert decoratedAlert, int repeatCount) {
        super(decoratedAlert);
        this.repeatCount = repeatCount;
    }

    @Override
    public String toString() {
        return decoratedAlert.toString() + ", Repeated " + repeatCount + " times";
    }
}
