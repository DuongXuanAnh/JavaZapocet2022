package cz.cuni.mff.java.zapocet;

import javax.swing.*;
import java.awt.*;
import javax.swing.text.NumberFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

public class AddBook extends JPanel {


    public AddBook() {
        setLayout(new GridLayout(0, 1, 5, 5)); // 0 řádků, 2 sloupce, 10 pixelů mezera vodorovně i svisle

        JLabel nameLabel = new JLabel("Název");
        JTextField nameField = new JTextField();

        JLabel authorLabel = new JLabel("Autor");
        JComboBox<String> authorComboBox = new JComboBox<>(new String[]{"Autor 1", "Autor 2", "Autor 3"});

        JLabel genresLabel = new JLabel("Žánr");
        JComboBox<String> genresComboBox = new JComboBox<>(new String[]{"Zanr 1", "Zanr 2", "Zanr 3"});

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


        JButton submitButton = new JButton("Submit");
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

        // Přidání prvků do JPanelu
        add(nameLabel);
        add(nameField);
        add(authorLabel);
        add(authorComboBox);
        add(genresLabel);
        add(genresComboBox);
        add(priceLabel);
        add(priceField);
        add(yearLabel);
        add(yearField);
        add(quantityLabel);
        add(quantitySpinner);
        add(descriptionLabel);
        add(descriptionArea);
        add(submitButton);

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Přidání paddingu mezi prvky a okrajem JPanelu

        // Nastavení preferované velikosti panelu
        Dimension preferredSize = new Dimension(500, Integer.MAX_VALUE);
        setPreferredSize(preferredSize);
    }

    /**
     * Vytvoří a vrátí formátovací objekt pro textové pole ceny, který povoluje pouze číselné hodnoty.
     */
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