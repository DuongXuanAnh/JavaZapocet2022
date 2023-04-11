package cz.cuni.mff.java.zapocet;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Book Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the menu panel with buttons to switch between different panels
        JPanel menuPanel = new JPanel(new GridLayout(1, 0));
        JButton authorButton = new JButton("Add Author");
        JButton bookButton = new JButton("Add Book");
        JButton customerButton = new JButton("Add Customer");
        JButton documentButton = new JButton("Add Document");
        JButton bookDetailButton = new JButton("Book Detail");

        // Create the card panel to hold the different panels
        JPanel cardPanel = new JPanel(new CardLayout());

        // Create the different panels and add them to the card panel
        JPanel authorPanel = new AddAuthor();
        JPanel bookPanel = new AddBook();
        JPanel customerPanel = new AddCustomer();
        JPanel documentPanel = new AddDocument();
        JPanel bookDetailPanel = new BookDetail();


        cardPanel.add(bookDetailPanel, "bookDetail");
        cardPanel.add(authorPanel, "author");
        cardPanel.add(bookPanel, "book");
        cardPanel.add(customerPanel, "customer");
        cardPanel.add(documentPanel, "document");


        // Add ActionListeners to each button to switch the displayed panel
        authorButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
            cardLayout.show(cardPanel, "author");
        });

        bookButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
            cardLayout.show(cardPanel, "book");
        });

        customerButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
            cardLayout.show(cardPanel, "customer");
        });

        documentButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
            cardLayout.show(cardPanel, "document");
        });

        bookDetailButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
            cardLayout.show(cardPanel, "bookDetail");
        });

        // Add the buttons to the menu panel
        menuPanel.add(bookDetailButton);
        menuPanel.add(authorButton);
        menuPanel.add(bookButton);
        menuPanel.add(customerButton);
        menuPanel.add(documentButton);


        // Add the menu panel and card panel to the frame
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(menuPanel, BorderLayout.NORTH);
        frame.getContentPane().add(cardPanel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }
}
