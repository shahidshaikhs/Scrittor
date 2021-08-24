package com.shahid.nid.Categories;

import android.provider.BaseColumns;

/**
 * Created by shahi on 10/9/2017.
 */

public class CategoriesNotesContract {

    /**
     * Prevent people from calling this class accidentally
     */
    private CategoriesNotesContract() {
    }

    /**
     * This is my main notes table
     */
    public static class categoriesContract implements BaseColumns {
        public static final String TABLE_NAME = "mainCategories";
        public static final String CATEGORIES_BACKUP_TABLE_NAME = "backupCategories";
        public static final String COLUMN_NAME_CATEGORY = "categoryName";
        public static final String COLUMN_DESCRIPTION_CATEGORY = "categoryDescription";
        public static final String COLUMN_COLOR = "categoryColor";
        public static final String COLUMN_CATEGORY_UNIQUE_ID = "uniqueCategoryID";
    }
}
