package cz.cuni.mff.java.zapocet;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.NumberFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.NumberFormat;

public class AddBook extends JPanel {
    private List<JComboBox<String>> authorComboBoxes;
    int positionCombobox = 2;
    public AddBook() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Název");
        JTextField nameField = new JTextField(20);

        JLabel authorLabel = new JLabel("Autor");

        JComboBox<String> authorComboBox = new JComboBox<>();

        Map<String, Integer> authorIdMap = new HashMap<>(); // Map to store author names and their IDs

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_winter", "root", "")) {
            String sql = "SELECT id, jmeno FROM autor";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("jmeno");
                authorComboBox.addItem(name);
                authorIdMap.put(name, id); // Store name and ID in the map
            }
        } catch (SQLException ex) {
            System.out.println("Error loading authors: " + ex.getMessage());
        }

        JLabel genresLabel = new JLabel("Žánr");
        String[] zanry = {"Sci-fi (vědeckofantastický)", "Romantika", "Thriller", "Detektivka", "Fantasy", "Horor", "Komedie", "Akční", "Drama", "Historický"};
        Arrays.sort(zanry);
        JComboBox<String> genresComboBox = new JComboBox<>(zanry);

        JLabel priceLabel = new JLabel("Cena (Kč)");
        JFormattedTextField priceField = new JFormattedTextField(createNumberFormatter());
        priceField.setValue(0);

        JLabel yearLabel = new JLabel("Rok vydání");
        JFormattedTextField yearField = new JFormattedTextField(createIntFormatter());
        yearField.setValue(0);

        JLabel quantityLabel = new JLabel("Počet kusů");
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));

        JLabel descriptionLabel = new JLabel("Popis:");
        JTextArea descriptionArea = new JTextArea(10, 20);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);

        JButton addAuthorButton = new JButton("Přidat autora");
        gbc.gridx = 2;
        gbc.gridy = 1;
        add(addAuthorButton, gbc);

        // Create a new list to store the "Remove" buttons
        List<JButton> removeAuthorButtons = new ArrayList<>();

        addAuthorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create and populate the new combo box with author names
                JComboBox<String> newAuthorComboBox = new JComboBox<>();

                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_winter", "root", "")) {
                    String sql = "SELECT id, jmeno FROM autor";
                    PreparedStatement statement = conn.prepareStatement(sql);
                    ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String name = resultSet.getString("jmeno");
                        newAuthorComboBox.addItem(name);
                    }
                } catch (SQLException ex) {
                    System.out.println("Error loading authors: " + ex.getMessage());
                }

                // Create the "Remove" button for the new combo box
                JButton removeAuthorButton = new JButton("X");

                // Add an ActionListener to the "Remove" button
                removeAuthorButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Remove the combo box and the "Remove" button from the panel
                        remove(newAuthorComboBox);
                        remove(removeAuthorButton);

                        // Update the layout
                        revalidate();

                        // Find the top-level container of the current component (e.g., JFrame)
                        Window topLevelContainer = SwingUtilities.getWindowAncestor(AddBook.this);

                        // Repack the top-level container to adjust its size
                        if (topLevelContainer instanceof JFrame) {
                            ((JFrame) topLevelContainer).pack();
                        }

                        // Repaint the entire GUI
                        topLevelContainer.repaint();
                    }
                });

                // Add the new combo box and the "Remove" button to the panel and to the list
                authorComboBoxes.add(newAuthorComboBox);
                removeAuthorButtons.add(removeAuthorButton);
                gbc.gridx = 1;
                gbc.gridy = positionCombobox;
                add(newAuthorComboBox, gbc);
                gbc.gridx = 3;
                add(removeAuthorButton, gbc);
                positionCombobox++;

                // Update the layout
                revalidate();

                // Find the top-level container of the current component (e.g., JFrame)
                Window topLevelContainer = SwingUtilities.getWindowAncestor(AddBook.this);

                // Repack the top-level container to adjust its size
                if (topLevelContainer instanceof JFrame) {
                    ((JFrame) topLevelContainer).pack();
                }

                // Repaint the entire GUI
                topLevelContainer.repaint();
            }
        });

        JButton submitButton = new JButton("Přidat knihu");

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                List<String> authorNames = new ArrayList<>();
                for (JComboBox<String> authorComboBox : authorComboBoxes) {
                    String authorName = (String) authorComboBox.getSelectedItem();
                    authorNames.add(authorName);
                }
                String genre = (String) genresComboBox.getSelectedItem();
                double price = ((Number) priceField.getValue()).doubleValue();
                int year = ((Number) yearField.getValue()).intValue();
                int quantity = (int) quantitySpinner.getValue();
                String description = descriptionArea.getText();

                System.out.println("Name: " + name);
                System.out.println("Authors: " + authorNames);
                System.out.println("Genre: " + genre);
                System.out.println("Price: " + price);
                System.out.println("Year: " + year);
                System.out.println("Quantity: " + quantity);
                System.out.println("Description: " + description);

                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_winter?useSSL=false", "root", "");
                     PreparedStatement insertStatement = conn.prepareStatement("INSERT INTO `kniha`(nazev, rok_vydani, cena, zanr, amount, popis) VALUES (?,?,?,?,?,?)")) {

                    insertStatement.setString(1, name);
                    insertStatement.setInt(2, year);
                    insertStatement.setDouble(3, price);
                    insertStatement.setString(4, genre);
                    insertStatement.setInt(5, quantity);
                    insertStatement.setString(6, description);

                    int rowsInserted = insertStatement.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("New book inserted successfully!");
                    }

                } catch (SQLException ex) {
                    System.out.println("Error inserting book: " + ex.getMessage());
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(nameLabel, gbc);

        gbc.gridx = 1;
        add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(authorLabel, gbc);

        gbc.gridx = 1;
        add(authorComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = positionCombobox + 5;
        add(genresLabel, gbc);

        gbc.gridx = 1;
        add(genresComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = positionCombobox + 5;
        add(genresLabel, gbc);
        gbc.gridx = 1;
        add(genresComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = positionCombobox + 6;
        add(priceLabel, gbc);

        gbc.gridx = 1;
        add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = positionCombobox + 7;
        add(yearLabel, gbc);

        gbc.gridx = 1;
        add(yearField, gbc);

        gbc.gridx = 0;
        gbc.gridy = positionCombobox + 8;
        add(quantityLabel, gbc);

        gbc.gridx = 1;
        add(quantitySpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = positionCombobox + 9;
        add(descriptionLabel, gbc);

        gbc.gridx = 1;
        add(descriptionScrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = positionCombobox + 10;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);

        authorComboBoxes = new ArrayList<>();
        authorComboBoxes.add(authorComboBox);
    }

    private NumberFormatter createNumberFormatter() {
        NumberFormat format = NumberFormat.getNumberInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(0.0);
        formatter.setMaximum(Double.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        return formatter;
    }

    private NumberFormatter createIntFormatter() {
        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        return formatter;
    }
}
