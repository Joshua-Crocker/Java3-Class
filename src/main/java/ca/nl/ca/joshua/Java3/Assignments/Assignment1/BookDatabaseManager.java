package ca.nl.ca.joshua.Java3.Assignments.Assignment1;

import org.mariadb.jdbc.Connection;
import org.mariadb.jdbc.Statement;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

                Author authorToAdd = null;
                for (Author author : authors) {
                    if (author.getAuthorID() == authorID) {
                        authorToAdd = author;
                        break;
                    }
                }

                if (authorToAdd != null) {
                    for (Book book : books) {
                        if (book.getIsbn().equals(isbn)) {
                            if (!book.getAuthorList().contains(authorToAdd)) {
                                book.addAuthor(authorToAdd);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    public void updateBook(String isbn, String title, int editionNumber, String copyright) throws SQLException {
        books.clear();
        authors.clear();
        String updateBookQuery = "UPDATE titles SET title = ?, editionNumber = ?, copyright = ? WHERE isbn = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateBookQuery)) {
            pstmt.setString(1, title);
            pstmt.setInt(2, editionNumber);
            pstmt.setString(3, copyright);
            pstmt.setString(4, isbn);
            int affectedRows = pstmt.executeUpdate(); // Use executeUpdate() for UPDATE statements

            if (affectedRows == 0) {
                System.out.println("No book found with ISBN: " + isbn);
            } else {
                System.out.println("Book updated successfully.");
            }
        }
    }

    public void updateAuthor(int authorID, String firstName, String lastName) throws SQLException {
        authors.clear();
        books.clear();
        String updateAuthorQuery = "UPDATE authors SET firstName = ?, lastName = ? WHERE authorID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateAuthorQuery)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setInt(3, authorID);
            int affectedRows = pstmt.executeUpdate(); // Execute the update

            if (affectedRows == 0) {
                System.out.println("No author found with ID: " + authorID);
            } else {
                System.out.println("Author updated successfully.");
            }
        }
    }

    public void createBook(String isbn, String title, int editionNumber, String copyright, List<Integer> authorIDs) throws SQLException {
        String insertBookQuery = "INSERT INTO titles (isbn, title, editionNumber, copyright) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertBookQuery)) {
            pstmt.setString(1, isbn);
            pstmt.setString(2, title);
            pstmt.setInt(3, editionNumber);
            pstmt.setString(4, copyright);
            int bookInserted = pstmt.executeUpdate();

            if (bookInserted > 0) {
                System.out.println("Book added successfully.");
            } else {
                System.out.println("Failed to add book.");
                return;
            }
        }

        // Associate the book with the provided authors
        String insertAuthorRelationQuery = "INSERT INTO authorisbn (authorID, isbn) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertAuthorRelationQuery)) {
            for (int authorID : authorIDs) {
                pstmt.setInt(1, authorID);
                pstmt.setString(2, isbn);
                pstmt.executeUpdate();
            }
            System.out.println("Authors linked to the book successfully.");
        }
    }


    public void createAuthor(String firstName, String lastName, List<String> bookISBNs) throws SQLException {
        String insertAuthorQuery = "INSERT INTO authors (firstName, lastName) VALUES (?, ?)";

        int authorID = -1; // Placeholder for the newly created author ID
        try (PreparedStatement pstmt = connection.prepareStatement(insertAuthorQuery, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            int authorInserted = pstmt.executeUpdate();

            if (authorInserted > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        authorID = generatedKeys.getInt(1); // Retrieve the generated author ID
                        System.out.println("Author added successfully with ID: " + authorID);
                    }
                }
            } else {
                System.out.println("Failed to add author.");
                return;
            }
        }

        // Associate the author with the provided books
        if (authorID != -1) {
            String insertBookRelationQuery = "INSERT INTO authorisbn (authorID, isbn) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertBookRelationQuery)) {
                for (String isbn : bookISBNs) {
                    pstmt.setInt(1, authorID);
                    pstmt.setString(2, isbn);
                    pstmt.executeUpdate();
                }
                System.out.println("Author linked to books successfully.");
            }
        }
    }
}
