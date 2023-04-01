package cz.cuni.mff.java.zapocet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class AddAuthor extends JPanel {

    public AddAuthor(){
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Přidat autora");
        title.setFont(new Font("Serif", Font.BOLD, 20));

        JLabel nameLabel = new JLabel("Jméno Autora");
        JTextField nameField = new JTextField(20);

        JLabel nationalLabel = new JLabel("Národnost");
        JComboBox<String> nationalComboBox = new JComboBox<>(
                new String[]{"American", "British", "Canadian", "Czechia", "Chinese", "French", "German", "Indian", "Italian", "Japanese", "Mexican", "Russian", "Spanish",});

        JButton submitButton = new JButton("Přidat autora");

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String national = (String) nationalComboBox.getSelectedItem();

                System.out.println("Name: " + name);
                System.out.println("Národnost: " + national);
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
