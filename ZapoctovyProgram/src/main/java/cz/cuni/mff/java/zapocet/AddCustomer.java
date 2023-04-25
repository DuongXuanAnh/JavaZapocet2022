package cz.cuni.mff.java.zapocet;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

    /**
        A panel for adding a new customer to the database. Contains a form for entering the customer's name and date of birth,
        and a submit button for adding the customer to the database. Upon successful insertion, displays a success message and
        resets the form.
    */
public class AddCustomer extends JPanel {
    /**
     Constructs a new AddCustomer panel with a form for entering the customer's name and date of birth, and a submit
     button for adding the customer to the database.
     */
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
                    Notification.showErrorMessage("Datum nebyl vybrán.");
                    return;
                }

                if(!selectedDate.before(new Date())){
                    System.out.println("Chyba: Datum je v budoucnosti.");
                    Notification.showErrorMessage("Datum musí být v minulosti.");
                    return;
                }

                // Format date as string
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String dateStr = format.format(selectedDate);

                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_winter", "root", "")) {
                    String sql = "INSERT INTO zakaznik (jmeno, datum_narozeni) VALUES (?, ?)";
                    PreparedStatement statement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                    statement.setString(1, customerName);
                    statement.setString(2, dateStr);
                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("New customer inserted successfully!");
                        ResultSet rs = statement.getGeneratedKeys();
                        if (rs.next()) {
                            int generatedId = rs.getInt(1);
                            System.out.println("Generated ID: " + generatedId);
                            Notification.showSuccessMessage("Nový zákazník byl založen s ID: " + generatedId);
                            resetPanel();
                        }else{
                            Notification.showErrorMessage("Nastala chyba, prosím zkuste to znovu");
                        }
                    }
                } catch (SQLException ex) {
                    System.out.println("Error inserting customer: " + ex.getMessage());
                    Notification.showErrorMessage("Chyba, zkuste to znovu nebo kontaktujte IT oddělení");
                }
            }
        });
    }

    /**
     Resets the AddCustomer panel to its initial state by creating a new instance of the panel and displaying it.
     */
    void resetPanel(){
        AddCustomer newCustomerPanel = new AddCustomer();
        getParent().add(newCustomerPanel, "addCustomer");
        CardLayout cardLayout = (CardLayout) getParent().getLayout();
        cardLayout.show(getParent(), "addCustomer");
    }

}
