package cz.cuni.mff.java.zapocet;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Date;

public class AddDocument extends JPanel {

    int positionCombobox = 4;
    public AddDocument() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Vytvořit objednávku");
        title.setFont(new Font("Serif", Font.BOLD, 20));

        JLabel typeLabel = new JLabel("Typ:");
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"Pujčit", "Koupit"});

        JLabel dateLabel = new JLabel("Datum:");
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd.MM.yyyy");



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


        JButton chooseBookButton = new JButton("Vybrat knihu");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(chooseBookButton, gbc);

        chooseBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> bookComboBox = new JComboBox<>();

                gbc.gridx = 0;
                gbc.gridy = positionCombobox;
                add(bookComboBox, gbc);

                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_winter", "root", "")) {
                    String sql = "SELECT id, nazev, cena FROM kniha";
                    PreparedStatement statement = conn.prepareStatement(sql);
                    ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        String name = resultSet.getString("nazev");
                        double cena = resultSet.getDouble("cena");

                        bookComboBox.addItem(name + " (cena: " + cena + ")");
                    }
                } catch (SQLException ex) {
                    System.out.println("Error loading books: " + ex.getMessage());
                }

                positionCombobox++;
                // Find the top-level container of the current component (e.g., JFrame)
                Window topLevelContainer = SwingUtilities.getWindowAncestor(AddDocument.this);
                // Repack the top-level container to adjust its size
                if (topLevelContainer instanceof JFrame) {
                    ((JFrame) topLevelContainer).pack();
                }
                // Repaint the entire GUI
                topLevelContainer.repaint();
            }
        });


        JButton submitButton = new JButton("Přidat doklad");
        gbc.gridx = 0;
        gbc.gridy = 100;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String type = (String) typeComboBox.getSelectedItem();
                Date selectedDate = dateChooser.getDate();

                // Format date as string
                String dateStr = String.format("%1$td.%1$tm.%1$tY", selectedDate);
                System.out.println(dateStr);
            }
        });
    }
}
