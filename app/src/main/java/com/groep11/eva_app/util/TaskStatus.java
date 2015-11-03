package com.groep11.eva_app.util;

public enum TaskStatus {
    NONE(0),
    CHOSEN(1),
    COMPLETED(2);

    public final int value;

    TaskStatus(int status) {
        this.value = status;
    }
}
