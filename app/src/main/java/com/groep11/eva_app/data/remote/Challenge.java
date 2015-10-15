package com.groep11.eva_app.data.remote;

public class Challenge {

    private String title;
    private String description;
    private String difficulty;

    public Challenge(String title, String description, String difficulty) {
        setTitle(title);
        setDescription(description);
        setDifficulty(difficulty);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("title: ").append(title).append("\n")
                .append("description: ").append(description).append("\n")
                .append("difficulty: ").append(difficulty);
        return sb.toString();
    }
}
