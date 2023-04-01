package cz.cuni.mff.java.zapocet;

import javax.swing.*;
import java.awt.*;
import javax.swing.text.NumberFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Arrays;

public class AddBook extends JPanel {

    public AddBook() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Název");
        JTextField nameField = new JTextField(20);

        JLabel authorLabel = new JLabel("Autor");
        JComboBox<String> authorComboBox = new JComboBox<>(new String[]{"Autor 1", "Autor 2", "Autor 3"});

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

        JButton submitButton = new JButton("Přidat knihu");

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String author = (String) authorComboBox.getSelectedItem();
                String genre = (String) genresComboBox.getSelectedItem();
                double price = ((Number) priceField.getValue()).doubleValue();
                int year = ((Number) yearField.getValue()).intValue();
                int quantity = (int) quantitySpinner.getValue();
                String description = descriptionArea.getText();

                System.out.println("Name: " + name);
                System.out.println("Author: " + author);
                System.out.println("Genre: " + genre);
                System.out.println("Price: " + price);
                System.out.println("Year: " + year);
                System.out.println("Quantity: " + quantity);
                System.out.println("Description: " + description);
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
        gbc.gridy = 2;
        add(genresLabel, gbc);

        gbc.gridx = 1;
        add(genresComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(priceLabel, gbc);

        gbc.gridx = 1;
        add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(yearLabel, gbc);

        gbc.gridx = 1;
        add(yearField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        add(quantityLabel, gbc);

        gbc.gridx = 1;
        add(quantitySpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        add(descriptionLabel, gbc);

        gbc.gridx = 1;
        add(descriptionScrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);
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