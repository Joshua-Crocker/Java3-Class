package ca.nl.ca.joshua.Java3.Assignments.Assignment1;

import java.util.ArrayList;
import java.util.List;

public class Book {
    private String isbn;
    private String title;
    private int editionNumber;
    private String copyRight;
    private List<Author> authorList;

    public Book(String isbn, String title, int editionNumber, String copyRight) {
        this.isbn = isbn;
        this.title = title;
        this.editionNumber = editionNumber;
        this.copyRight = copyRight;
        this.authorList = new ArrayList<>(); // Initialize the list properly
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getEditionNumber() {
        return editionNumber;
    }

    public void setEditionNumber(int editionNumber) {
        this.editionNumber = editionNumber;
    }

    public String getCopyRight() {
        return copyRight;
    }

    public void setCopyRight(String copyRight) {
        this.copyRight = copyRight;
    }

    public List<Author> getAuthorList() {
        return authorList;
    }

    public void setAuthorList(List<Author> authorList) {
        this.authorList = authorList;
    }

    public void addAuthor(Author author) {
        this.authorList.add(author);
    }
}
