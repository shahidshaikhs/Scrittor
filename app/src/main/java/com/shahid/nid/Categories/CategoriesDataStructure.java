package com.shahid.nid.Categories;

/**
 * Created by shahi on 10/9/2017.
 */

public class CategoriesDataStructure {

    private String categoryName, categoryColor;
    private String description;
    private String categoryUniqueId;

    public CategoriesDataStructure() {
    }

    public CategoriesDataStructure(String categoryName, String categoryColor) {
        this.categoryName = categoryName;
        this.categoryColor = categoryColor;
    }

    public String getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(String categoryColor) {
        this.categoryColor = categoryColor;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryUniqueId() {
        return categoryUniqueId;
    }

    public void setCategoryUniqueId(String categoryUniqueId) {
        this.categoryUniqueId = categoryUniqueId;
    }
}
