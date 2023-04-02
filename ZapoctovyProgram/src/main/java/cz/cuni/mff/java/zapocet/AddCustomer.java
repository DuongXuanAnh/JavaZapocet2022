package cz.cuni.mff.java.zapocet;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;


public class AddCustomer extends JPanel {

    public AddCustomer(){
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Přidat zákazníka");
        title.setFont(new Font("Serif", Font.BOLD, 20));

        JLabel nameLabel = new JLabel("Jméno zákazníka");
        JTextField nameField = new JTextField(20);

        JLabel dateLabel = new JLabel("Datum:");
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd.MM.yyyy");


        JButton submitButton = new JButton("Přidat zákazníka");



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
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(dateLabel, gbc);
        gbc.gridx = 1;
        add(dateChooser, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String customerName = nameField.getText();
                Date selectedDate = dateChooser.getDate();

                // Check if date is null
                if (selectedDate == null) {
                    System.out.println("Chyba: Datum nebyl vybrán.");
                    return;
                }

                Date currentDate = new Date();
                if (selectedDate.compareTo(currentDate) >= 0) {
                    System.out.println("Chyba: Vybrané datum je v budoucnosti.");
                    return;
                }

                System.out.println(customerName);
                // Format date as string
                String dateStr = String.format("%1$td.%1$tm.%1$tY", selectedDate);
                System.out.println(dateStr);

            }
        });
    }



}
