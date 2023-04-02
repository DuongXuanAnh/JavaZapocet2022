package cz.cuni.mff.java.zapocet;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class AddDocument extends JPanel {
    public AddDocument() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Vytvořit doklad");
        title.setFont(new Font("Serif", Font.BOLD, 20));

        JLabel typeLabel = new JLabel("Typ:");
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"Pujčit", "Koupit"});

        JLabel dateLabel = new JLabel("Datum:");
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd.MM.yyyy");

        JLabel priceLabel = new JLabel("Cena (Kč):");
        JFormattedTextField priceField = new JFormattedTextField();
        priceField.setValue(0);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(title, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 1;
        add(typeLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        add(typeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(dateLabel, gbc);
        gbc.gridx = 1;
        add(dateChooser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(priceLabel, gbc);

        gbc.gridx = 1;
        add(priceField, gbc);

        JButton submitButton = new JButton("Přidat doklad");
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String type = (String) typeComboBox.getSelectedItem();
                Date selectedDate = dateChooser.getDate();
                String price = priceField.getText();

                // Check if date is null
                if (selectedDate == null) {
                    System.out.println("Chyba: Datum nebyl vybrán.");
                    return;
                }

                Date currentDate = new Date();
                if (selectedDate.compareTo(currentDate) <= 0) {
                    System.out.println("Chyba: Vybrané datum je v minulosti.");
                    return;
                }

                // Format date as string
                String dateStr = String.format("%1$td.%1$tm.%1$tY", selectedDate);

                System.out.println((String.format("Typ: %s, Datum: %s, Cena: %s", type, dateStr, price)));
            }
        });
    }
}
