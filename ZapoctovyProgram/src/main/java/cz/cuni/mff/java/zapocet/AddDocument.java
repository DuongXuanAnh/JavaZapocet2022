package cz.cuni.mff.java.zapocet;


import javax.swing.*;
import java.awt.*;

public class AddDocument extends JPanel{
    public AddDocument(){
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Přidat autora");
        title.setFont(new Font("Serif", Font.BOLD, 20));

        JLabel typeLabel = new JLabel("Typ");
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"Pujčit", "Koupit"});

        JLabel dateLabel = new JLabel("Datum");

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(title, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(typeLabel, gbc);

        gbc.gridx = 1;
        add(typeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(dateLabel, gbc);


    }
}