package com.groep11.eva_app.data.remote;

public class Task {

    private String dueDate;
    private Challenge challenge;
    private int status;
    private String _id;

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
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(challenge).append("\n")
                .append("due date: ").append(dueDate).append("\n")
                .append("status: ").append(status).append("\n")
                .append("id: ").append(_id);
        return sb.toString();
    }
}
