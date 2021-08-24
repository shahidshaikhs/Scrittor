package com.shahid.nid.CategoriesDialog;

/**
 * Created by shahi on 10/9/2017.
 */

public class DialogCategoriesDataStructure {

    private String categoryName, categoryColor;
    private long categoryID;

    public DialogCategoriesDataStructure() {
    }

    public DialogCategoriesDataStructure(String categoryName, String categoryColor, long categoryID) {
        this.categoryName = categoryName;
        this.categoryColor = categoryColor;
        this.categoryID = categoryID;
    }

    public long getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(long categoryID) {
        this.categoryID = categoryID;
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
}
