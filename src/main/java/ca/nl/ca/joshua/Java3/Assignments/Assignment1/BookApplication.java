package ca.nl.ca.joshua.Java3.Assignments.Assignment1;

import java.sql.SQLException;
import java.util.Scanner;

public class BookApplication {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            BookDatabaseManager dbManager = new BookDatabaseManager("jdbc:mariadb://localhost:3306/books", "root", "root");
            dbManager.loadBooksAndAuthors();

            while (true) {
                System.out.println("1. Print all books");
                System.out.println("2. Print all authors");
                System.out.println("3. Edit a book");
                System.out.println("4. Edit an author");
                System.out.println("5. Add a book");
                System.out.println("6. Add an author");
                System.out.println("7. Quit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        dbManager.getBooks().forEach(System.out::println);
                        break;
                    case 2:
                        dbManager.getAuthors().forEach(System.out::println);
                        break;
                    case 3:
                        System.out.println("Enter the ISBN of the book you would like to edit: ");
                        String isbnToEdit = scanner.nextLine();
                        Book bookToEdit = dbManager.getBooks().stream().filter(book -> book.getIsbn().equals(isbnToEdit)).findFirst().orElse(null);
                        if (bookToEdit != null) {
                            System.out.println("Enter a new title: ");
                            String newTitle = scanner.nextLine();
                            System.out.println("Enter a new editionNumber: ");
                            int newEditionNumber = scanner.nextInt();
                            System.out.println("Enter a new copyright year: ");
                            String newCopyrightYear = scanner.nextLine();

                            bookToEdit.setTitle(newTitle);
                            bookToEdit.setEditionNumber(newEditionNumber);
                            bookToEdit.setIsbn(newCopyrightYear);

                            dbManager.updateBook(bookToEdit);
                            System.out.println("Book updated!");
                        } else {
                            System.out.println("Book not found!");
                        }
                        break;
                    case 4:
                        System.out.println("Enter the Author ID of the author you would like to edit: ");
                        int authorIDToEdit = scanner.nextInt();
                        Author authorToEdit = dbManager.getAuthors().stream().filter(author -> author.getAuthorID() == authorIDToEdit).findFirst().orElse(null);
                        if (authorToEdit != null) {
                            System.out.println("Enter a new first name: ");
                            String newFirstName = scanner.nextLine();
                            System.out.println("Enter a new last name: ");
                            String newLastName = scanner.nextLine();
                            authorToEdit.setFirstName(newFirstName);
                            authorToEdit.setLastName(newLastName);

                            dbManager.updateAuthor(authorToEdit);
                            System.out.println("Author updated!");
                        } else {
                            System.out.println("Author not found!");
                        }
                        break;
                    case 5:
                        System.out.print("Enter ISBN: ");
                        String isbn = scanner.nextLine();
                        System.out.print("Enter title: ");
                        String title = scanner.nextLine();
                        System.out.print("Enter edition number: ");
                        int editionNumber = scanner.nextInt();
                        scanner.nextLine();  // Consume the newline
                        System.out.print("Enter copyright: ");
                        String copyright = scanner.nextLine();

                        Book newBook = new Book(isbn, title, editionNumber, copyright);
                        dbManager.addBook(newBook);
                        System.out.println("Book added successfully!");
                        break;
                    case 6:
                        // Add an author
                        System.out.print("Enter first name: ");
                        String firstName = scanner.nextLine();
                        System.out.print("Enter last name: ");
                        String lastName = scanner.nextLine();

                        Author newAuthor = new Author(firstName, lastName);
                        dbManager.addAuthor(newAuthor);
                        System.out.println("Author added successfully!");
                        break;
                    case 7:
                        // Quit
                        System.out.println("Exiting application...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice, please try again.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
