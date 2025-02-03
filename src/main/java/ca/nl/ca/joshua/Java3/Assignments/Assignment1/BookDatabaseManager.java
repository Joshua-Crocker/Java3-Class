package ca.nl.ca.joshua.Java3.Assignments.Assignment1;

import org.mariadb.jdbc.Connection;
import org.mariadb.jdbc.Statement;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static ca.nl.ca.joshua.Java3.Assignments.Assignment1.dbProperties.*;

public class BookDatabaseManager {
    private List<Book> books = new ArrayList<>();
    private List<Author> authors = new ArrayList<>();
    private Connection connection;

    public BookDatabaseManager() throws SQLException {
        connection = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public void add(Book book) {
        this.books.add(book);
    }

    public void add(Author author) {
        this.authors.add(author);
    }

    public void loadBooks() throws SQLException {
        String bookQuery = "SELECT * FROM titles";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(bookQuery)) {
            while (rs.next()) {
                String isbn = rs.getString("isbn");
                String title = rs.getString("title");
                int editionNumber = rs.getInt("editionNumber");
                String copyright = rs.getString("copyright");
                books.add(new Book(isbn, title, editionNumber, copyright));
            }
        }
    }

    public void loadAuthors() throws SQLException {
        String authorQuery = "SELECT * FROM authors";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(authorQuery)) {
            while (rs.next()) {
                int authorID = rs.getInt("authorID");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                authors.add(new Author(authorID, firstName, lastName));
            }
        }
    }

    public void loadBookAuthorRelationship() throws SQLException {
        String bookAuthorQuery = "SELECT * FROM authorisbn";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(bookAuthorQuery)) {
            while (rs.next()) {
                int authorID = rs.getInt("authorID"); // This is an integer
                String isbn = rs.getString("isbn");

                // Find the book with the given ISBN
                Book bookToAdd = null;
                for (Book book : books) {
                    if (book.getIsbn().equals(isbn)) {
                        bookToAdd = book;
                        break;
                    }
                }

                // If the book exists, associate it with the correct author
                if (bookToAdd != null) {
                    for (Author author : authors) {
                        if (author.getAuthorID() == authorID) { // Compare as integers
                            if (!author.getBookList().contains(bookToAdd)) { // Check if the book is already in the list
                                author.addBook(bookToAdd); // Add the book to the author
                            }
                            break; // Exit loop once the correct author is found
                        }
                    }
                }
            }
        }
    }
}
