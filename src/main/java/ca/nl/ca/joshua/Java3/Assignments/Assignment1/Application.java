package ca.nl.ca.joshua.Java3.Assignments.Assignment1;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        try {
            // Initialize BookDatabaseManager and load data
            BookDatabaseManager dbManager = new BookDatabaseManager();
            dbManager.loadBooks(); // Load books from the database
            dbManager.loadAuthors(); // Load authors from the database
            dbManager.loadBookAuthorRelationship(); // Load author-book relationships

            Scanner scanner = new Scanner(System.in);

            while (true) { // Keeps the menu running until the user chooses to quit
                System.out.println(
                        "1. Print All Books From Database. \n" +
                                "2. Print All Authors From Database. \n" +
                                "3. Edit a Book's Attributes. \n" +
                                "4. Edit an Author's Attributes. \n" +
                                "5. Add an Author. \n" +
                                "6. Add a Book. \n" +
                                "7. Quit \n"
                );
                System.out.print("Enter your choice: ");
                String choice = scanner.next();

                switch (choice) {
                    case "1":
                        for (Book book : dbManager.getBooks()) {
                            System.out.println("Title: " + book.getTitle() + " | ISBN: " + book.getIsbn() + " | Edition Number: " + book.getEditionNumber() + " | Copyright: " + book.getCopyRight());

                            if (book.getAuthorList() == null || book.getAuthorList().isEmpty()) {
                                System.out.println("There are no authors for this book.");
                            } else {
                                for (Author author : book.getAuthorList()) {
                                    System.out.println("Author: " + author.getFirstName() + " " + author.getLastName());
                                }
                            }
                        }
                        break;

                    case "2":
                        for (Author author : dbManager.getAuthors()) {
                            System.out.println("Author: " + author.getFirstName() + " " + author.getLastName());

                            if (author.getBookList() == null || author.getBookList().isEmpty()) {
                                System.out.println("There are no books for this author.");
                            } else {
                                for (Book book : author.getBookList()) {
                                    System.out.println("  - " + book.getTitle() + " (ISBN: " + book.getIsbn() + ")");
                                }
                            }
                        }
                        break;

                    case "3": // Edit Book
                        System.out.print("Enter the ISBN of the book to edit: ");
                        String isbn = scanner.next();
                        scanner.nextLine(); // Consume newline

                        System.out.print("Enter new title: ");
                        String title = scanner.nextLine();

                        System.out.print("Enter new edition number: ");
                        int editionNumber = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                        System.out.print("Enter new copyright: ");
                        String copyright = scanner.nextLine();

                        dbManager.updateBook(isbn, title, editionNumber, copyright);
                        System.out.println("Book updated successfully.");
                        dbManager.loadBooks();
                        dbManager.loadAuthors();
                        break;

                    case "4": // Edit Author
                        System.out.print("Enter the Author ID to edit: ");
                        int authorID = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                        System.out.print("Enter new first name: ");
                        String firstName = scanner.nextLine();

                        System.out.print("Enter new last name: ");
                        String lastName = scanner.nextLine();

                        dbManager.updateAuthor(authorID, firstName, lastName);
                        System.out.println("Author updated successfully.");
                        dbManager.loadAuthors();
                        dbManager.loadBooks();
                        break;

                    case "5":
                        System.out.print("Enter first name: ");
                        String newfirstName = scanner.nextLine();

                        System.out.print("Enter last name: ");
                        String newlastName = scanner.nextLine();

// Get book ISBNs
                        List<String> bookISBNs = new ArrayList<>();
                        System.out.print("Enter book ISBNs (comma-separated): ");
                        String[] isbns = scanner.nextLine().split(",");
                        for (String bisbn : isbns) {
                            bookISBNs.add(bisbn.trim());
                        }

// Create the author
                        dbManager.createAuthor(newfirstName, newlastName, bookISBNs);
                        break;

                    case "6":
                        System.out.print("Enter ISBN: ");
                        String newisbn = scanner.nextLine();

                        System.out.print("Enter title: ");
                        String newtitle = scanner.nextLine();

                        System.out.print("Enter edition number: ");
                        int neweditionNumber = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                        System.out.print("Enter copyright year: ");
                        String newcopyright = scanner.nextLine();

                        // Get author IDs
                        List<Integer> authorIDs = new ArrayList<>();
                        System.out.print("Enter author IDs (comma-separated): ");
                        String[] ids = scanner.nextLine().split(",");
                        for (String id : ids) {
                            authorIDs.add(Integer.parseInt(id.trim()));
                        }

                        // Create the book
                        dbManager.createBook(newisbn, newtitle, neweditionNumber, newcopyright, authorIDs);
                        break;

                    case "7":
                        System.out.println("Exiting program.");
                        scanner.close();
                        return; // Exit the program

                    default:
                        System.out.println("Invalid choice. Please enter a number between 1-7.");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
