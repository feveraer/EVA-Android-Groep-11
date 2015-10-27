package com.groep11.eva_app.data.remote;

public class Task {

    private String dueDate;
    private Challenge challenge;
    private TaskStatus status;

    public Task() {
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setStatus(int status) {
        this.status = TaskStatus.values()[status];
    }

    public int getStatus() {
        return status.ordinal();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(challenge).append("\n")
                .append("due date: ").append(dueDate).append("\n")
                .append("status: ").append(status);
        return sb.toString();
    }
}
