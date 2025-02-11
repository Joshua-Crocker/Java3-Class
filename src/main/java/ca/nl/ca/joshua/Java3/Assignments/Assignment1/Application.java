package ca.nl.ca.joshua.Java3.Assignments.Assignment1;

import java.sql.SQLException;

public class Application {
    public static void main(String[] args) {
        try {
            // Initialize BookDatabaseManager and load data
            BookDatabaseManager dbManager = new BookDatabaseManager();
            dbManager.loadBooks(); // Load books from the database
            dbManager.loadAuthors(); // Load authors from the database
            dbManager.loadBookAuthorRelationship(); // Load author-book relationships

            // Loop through all authors and print their books
            for (Author author : dbManager.getAuthors()) {
                System.out.println("Author: " + author.getFirstName() + " " + author.getLastName());

                if (author.getBookList() == null || author.getBookList().isEmpty()) {
                    System.out.println("  No books found for this author.");
                } else {
                    for (Book book : author.getBookList()) {
                        System.out.println("  - " + book.getTitle() + " (ISBN: " + book.getIsbn() + ")");
                    }
                }
                System.out.println(); // Add spacing for better readability
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
