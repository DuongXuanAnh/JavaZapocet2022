package cz.cuni.mff.java.zapocet;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

        typeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedOption = (String) typeComboBox.getSelectedItem();
                if (selectedOption.equals("Koupit")) {
                    dateChooser.setVisible(false);
                } else {
                    dateChooser.setVisible(true);
                }
            }
        });

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

        JLabel customerIDLabel = new JLabel("ID zákaznika:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(customerIDLabel, gbc);

        JTextField customerIDTextField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(customerIDTextField, gbc);

        customerIDTextField.addKeyListener(new KeyAdapter() {
            JLabel customerName = new JLabel("");
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Get the text entered by the user and display it
                    String idString = customerIDTextField.getText();

                    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_winter", "root", "")) {
                        String sql = "SELECT id, jmeno, datum_narozeni FROM zakaznik";
                        PreparedStatement statement = conn.prepareStatement(sql);
                        ResultSet resultSet = statement.executeQuery();
                        String name = "Not found";
                        String datumNarozeni = "";
                        while (resultSet.next()) {
                            int id = resultSet.getInt("id");

                            if(id == Integer.parseInt(idString)){
                                name = resultSet.getString("jmeno");
                                String dateString = resultSet.getString("datum_narozeni");
                                DateTimeFormatter originalFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                DateTimeFormatter targetFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                LocalDate date = LocalDate.parse(dateString, originalFormat);
                                datumNarozeni = date.format(targetFormat);
                                break;
                            }
                        }

                        customerName.setText(name.equals("Not found") ? name : name + " (" + datumNarozeni + ")");
                        gbc.gridx = 0;
                        gbc.gridy = 4;
                        add(customerName, gbc);
                        refreshWindow();

                    } catch (SQLException ex) {
                        System.out.println("Error loading books: " + ex.getMessage());
                    }

                }
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

    private void refreshWindow() {
        // Update the layout
        revalidate();

        // Find the top-level container of the current component (e.g., JFrame)
        Window topLevelContainer = SwingUtilities.getWindowAncestor(AddDocument.this);

        // Repack the top-level container to adjust its size
        if (topLevelContainer instanceof JFrame) {
            ((JFrame) topLevelContainer).pack();
        }

        // Repaint the entire GUI
        topLevelContainer.repaint();
    }




}
