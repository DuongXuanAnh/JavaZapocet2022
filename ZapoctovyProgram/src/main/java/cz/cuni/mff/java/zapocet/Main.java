package cz.cuni.mff.java.zapocet;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Book Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the menu panel with buttons to switch between different panels
        JPanel menuPanel = new JPanel(new GridLayout(1, 0));
        JButton authorButton = new JButton("Přidat autora");
        JButton bookButton = new JButton("Přidat knihu");
        JButton customerButton = new JButton("Přidat zákazníka");
        JButton documentButton = new JButton("Objednávka");
        JButton bookDetailButton = new JButton("Knihy");
        JButton returnBookButton = new JButton("Vrátit knihu");

        // Create the card panel to hold the different panels
        JPanel cardPanel = new JPanel(new CardLayout());
        JPanel bookDetailPanel = new BookDetail();
//        JPanel bookDetailPanel = new ReturnBook();

        cardPanel.add(bookDetailPanel, "bookDetail");

        // Add ActionListeners to each button to switch the displayed panel
        authorButton.addActionListener(e -> {
            JPanel authorPanel = new AddAuthor();
            cardPanel.add(authorPanel, "author");
            CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
            cardLayout.show(cardPanel, "author");
        });

        bookButton.addActionListener(e -> {
            JPanel addBookPanel = new AddBook();
            cardPanel.add(addBookPanel, "book");
            CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
            cardLayout.show(cardPanel, "book");
        });

        customerButton.addActionListener(e -> {
            JPanel customerPanel = new AddCustomer();
            cardPanel.add(customerPanel, "customer");
            CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
            cardLayout.show(cardPanel, "customer");
        });

        documentButton.addActionListener(e -> {
            JPanel documentPanel = new AddDocument();
            cardPanel.add(documentPanel, "document");

            CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
            cardLayout.show(cardPanel, "document");
        });

        bookDetailButton.addActionListener(e -> {
            JPanel bookDetailPanel2 = new BookDetail();
            cardPanel.add(bookDetailPanel2, "bookDetail");
            CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
            cardLayout.show(cardPanel, "bookDetail");
        });

        returnBookButton.addActionListener(e -> {
            JPanel returnBookPanel2 = new ReturnBook();
            cardPanel.add(returnBookPanel2, "returnBook");
            CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
            cardLayout.show(cardPanel, "returnBook");
        });

        // Add the buttons to the menu panel
        menuPanel.add(bookDetailButton);
        menuPanel.add(authorButton);
        menuPanel.add(bookButton);
        menuPanel.add(customerButton);
        menuPanel.add(documentButton);
        menuPanel.add(returnBookButton);

        // Add the menu panel and card panel to the frame
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(menuPanel, BorderLayout.NORTH);
        frame.getContentPane().add(cardPanel, BorderLayout.CENTER);

        // Set the frame to be displayed in full screen
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        frame.setVisible(true);
    }
}
