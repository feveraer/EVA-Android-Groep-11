package com.groep11.eva_app.data.remote;

public class Challenge {

    private String title;
    private String description;
    private String difficulty;
    // TODO: category is an ID that points to a different 'table'
    private Category category;

    public Challenge() {
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("title: ").append(title).append("\n")
                .append("description: ").append(description).append("\n")
                .append("difficulty: ").append(difficulty)
                .append("category: ").append(category).toString();
    }
}
