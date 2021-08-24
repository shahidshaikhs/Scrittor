package com.shahid.nid;

import java.util.Objects;

/**
 * Created by shahi on 9/3/2017.
 */

public class NoteDataStructure {

    private String noteTitle, noteContent, creationDate;
    private String categoryName;
    private String categoryColor;
    private String categoryId;
    private String isStarred;
    private String noteUniqueId; // across devices
    private int noteID;
    private String lastEdited;

    public NoteDataStructure() {
    }

    public NoteDataStructure(String noteTitle, String noteContent, String creationDate, int noteID) {
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.creationDate = creationDate;
        this.noteID = noteID;
    }

    public int getNoteID() {
        return noteID;
    }

    public void setNoteID(int noteID) {
        this.noteID = noteID;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(String categoryColor) {
        this.categoryColor = categoryColor;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getIsStarred() {
        return isStarred;
    }

    public void setIsStarred(String isStarred) {
        this.isStarred = isStarred;
    }

    public String getNoteUniqueId() {
        return noteUniqueId;
    }

    public void setNoteUniqueId(String noteUniqueId) {
        this.noteUniqueId = noteUniqueId;
    }

    public String getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(String lastEdited) {
        this.lastEdited = lastEdited;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteDataStructure that = (NoteDataStructure) o;
        return Objects.equals(noteUniqueId, that.noteUniqueId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(noteUniqueId);
    }
}
