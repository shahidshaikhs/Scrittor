package com.shahid.nid;

import android.provider.BaseColumns;

/**
 * Created by shahi on 9/2/2017.
 */

public final class NotesContract {

    /**
     * Prevent people from calling this class accidentally
     */
    private NotesContract() {
    }

    /**
     * This is my main notes table
     */
    public static class mainNotes implements BaseColumns {
        public static final String TABLE_NAME = "mainNotes";
        public static final String BACKUP_TABLE_NAME_FOR_UPGRADE_DB = "backupNotes";
        public static final String COLUMN_NAME_TITLE = "noteTitle";
        public static final String COLUMN_NAME_CONTENT = "noteContent";
        public static final String COLUMN_DATE = "creationDate";
        public static final String COLUMN_CATEGORY = "noteCategory";
        public static final String COLUMN_CATEGORY_ID = "categoryID";
        public static final String COLUMN_CATEGORY_COLOR = "categoryColor";
        public static final String COLUMN_STARRED_CHECK = "starredCheck";
        public static final String COLUMN_UNIQUE_NOTE_ID = "uniqueNoteId";
        public static final String COLUMN_LAST_EDITED = "lastEdited";

        public static final int PASSWORD_CHECK = 0;
    }

}
