package ca.nl.ca.joshua.Java3.Assignments.Assignment1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDatabaseManager {
    private Connection connection;
    private List<Book> books = new ArrayList<>();
    private List<Author> authors = new ArrayList<>();

    public BookDatabaseManager(String DB_URL, String USER, String PASS) throws SQLException {
        connection = DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public void loadBooksAndAuthors() throws SQLException {
        String query = "SELECT * FROM titles";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            Book book = new Book(rs.getString("isbn"), rs.getString("title"), rs.getInt("editionNumber"), rs.getString("copyright"));
            books.add(book);
        }

        query = "SELECT * FROM authors";
        rs = stmt.executeQuery(query);

        while (rs.next()) {
            Author author = new Author(rs.getInt("authorID"), rs.getString("firstName"), rs.getString("lastName"));
            authors.add(author);
        }

        query = "SELECT * FROM authorisbn";
        rs = stmt.executeQuery(query);

        while (rs.next()) {
            int authorID = rs.getInt("authorID");
            String isbn = rs.getString("isbn");

            Book book = books.stream().filter(b -> b.toString().contains(isbn)).findFirst().orElse(null);
            Author author = authors.stream().filter(a -> a.toString().contains(String.valueOf(authorID))).findFirst().orElse(null);

            if (book != null && author != null) {
                book.addAuthor(author);
            }
        }
    }

    public void addBook(Book book) throws SQLException {
        String query = "INSERT INTO titles (isbn, title, editionNumber, copyright) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, book.getIsbn());
        pstmt.setString(2, book.getTitle());
        pstmt.setInt(3, book.getEditionNumber());
        pstmt.setString(4, book.getCopyright());
        pstmt.executeUpdate();
        books.add(book);
    }

    public void addAuthor(Author author) throws SQLException {
        String query = "INSERT INTO authors (firstName, lastName) VALUES (?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        pstmt.setString(1, author.getFirstName());
        pstmt.setString(2, author.getLastName());
        pstmt.executeUpdate();
        authors.add(author);
    }

    public void updateBook(Book book) throws SQLException {
        String query = "UPDATE titles SET title = ?, editionNumber = ?, copyright = ? WHERE isbn = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, book.getTitle());
        pstmt.setInt(2, book.getEditionNumber());
        pstmt.setString(3, book.getCopyright());
        pstmt.setString(4, book.getIsbn());
        pstmt.executeUpdate();
    }

    public void updateAuthor(Author author) throws SQLException {
        String query = "UPDATE authors SET firstName = ?, lastName = ? WHERE authorID = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, author.getFirstName());
        pstmt.setString(2, author.getLastName());
        pstmt.setInt(3, author.getAuthorID());
        pstmt.executeUpdate();
    }

    public void deleteBook(String isbn) throws SQLException {
        String query = "DELETE FROM titles WHERE isbn = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, isbn);
        pstmt.executeUpdate();
        books.removeIf(book -> book.toString().contains(isbn));
    }

    public void deleteAuthor(int authorID) throws SQLException {
        String query = "DELETE FROM authors WHERE authorID = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, authorID);
        pstmt.executeUpdate();
        authors.removeIf(author -> author.toString().contains(String.valueOf(authorID)));
    }

    public List<Book> getBooks() {
        return books;
    }

    public List<Author> getAuthors() {
        return authors;
    }
}


