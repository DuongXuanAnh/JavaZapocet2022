package cz.cuni.mff.java.zapocet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * A panel that allows the user to add a new author to a database.
 */
public class AddAuthor extends JPanel {
    /**
     * Creates a new AddAuthor panel with a title label, name and nationality input fields,
     * and a submit button. When the submit button is clicked, a new author is inserted
     * into the database.
     */
    public AddAuthor(){
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title label
        JLabel title = new JLabel("Přidat autora");
        title.setFont(new Font("Serif", Font.BOLD, 20));

        // Name input field
        JLabel nameLabel = new JLabel("Jméno Autora");
        JTextField nameField = new JTextField(20);

        // Nationality input field
        JLabel nationalLabel = new JLabel("Národnost");
        JComboBox<String> nationalComboBox = new JComboBox<>(
                new String[]{"American", "British", "Canadian", "Czechia", "Chinese", "French", "German", "Indian", "Italian", "Japanese", "Mexican", "Russian", "Spanish","Vietnam"});

        // Submit button
        JButton submitButton = new JButton("Přidat autora");

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String national = (String) nationalComboBox.getSelectedItem();

                if (name.length() < 4 || name.length() > 25) {
                    // Show popup message if name is too short or too long
                    JOptionPane.showMessageDialog(AddAuthor.this, "Jméno autora musí být dlouhé mezi 4 a 25 znaky.", "Chyba", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_winter?useSSL=false", "root", "");
                     PreparedStatement checkStatement = conn.prepareStatement("SELECT id, jmeno FROM autor WHERE jmeno = ?");
                     PreparedStatement insertStatement = conn.prepareStatement("INSERT INTO autor (jmeno, narodnost) VALUES (?, ?)")) {
                    // Check if the author already exists
                    checkStatement.setString(1, name);
                    ResultSet resultSet = checkStatement.executeQuery();

                    if (resultSet.next()) {
                        // Author already exists, show popup message
                        JOptionPane.showMessageDialog(AddAuthor.this, "Autor " + name + " již už existuje.", "Autor již existuje", JOptionPane.WARNING_MESSAGE);
                    } else {
                        // Author doesn't exist, insert new author
                        insertStatement.setString(1, name);
                        insertStatement.setString(2, national);
                        int rowsInserted = insertStatement.executeUpdate();
                        if (rowsInserted > 0) {
                            System.out.println("New author inserted successfully!");
                            JOptionPane.showMessageDialog(AddAuthor.this, "Autor " + name + " byl úspěšně přidán", "successfully", JOptionPane.INFORMATION_MESSAGE);
                            nameField.setText("");
                            nationalComboBox.setSelectedIndex(0);
                            repaint();
                        }
                    }
                } catch (SQLException ex) {
                    System.out.println("Error inserting author: " + ex.getMessage());
                    JOptionPane.showMessageDialog(AddAuthor.this, "Chyba", "Error", JOptionPane.ERROR_MESSAGE);

                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(title, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(nameLabel, gbc);

        gbc.gridx = 1;
        add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(nationalLabel, gbc);

        gbc.gridx = 1;
        add(nationalComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);
    }

}
