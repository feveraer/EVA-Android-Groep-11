package com.groep11.eva_app.data.remote;

public class Task {

    private String dueDate;
    private Challenge challenge;
    private boolean completed;

    public Task() {}

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

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(challenge).append("\n")
                .append("due date: ").append(dueDate).append("\n")
                .append("completed: ").append(completed);
        return sb.toString();
    }
}
