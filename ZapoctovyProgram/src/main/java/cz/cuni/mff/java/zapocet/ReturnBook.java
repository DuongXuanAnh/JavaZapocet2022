package cz.cuni.mff.java.zapocet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReturnBook extends JPanel {

    public ReturnBook(){
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Vrácení knihy");
        title.setFont(new Font("Serif", Font.BOLD, 20));

        JLabel documentIDLabel = new JLabel("ID objednávky");
        JTextField documentIDTextField = new JTextField(20);

        JButton submitButton = new JButton("Vrátit knihu");



        documentIDTextField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Get the text entered by the user and display it
                    String idString = documentIDTextField.getText();

                    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_winter", "root", "")) {
                        String sql = "SELECT kniha.nazev AS nazev, doklad.datum, doklad.datumTo, kd.amount\n" +
                                "FROM doklad\n" +
                                "JOIN doklad_kniha AS kd\n" +
                                "ON doklad.id = kd.id_doklad\n" +
                                "JOIN kniha\n" +
                                "ON kniha.id = kd.id_kniha\n" +
                                "WHERE doklad.id = ? AND doklad.datumTo IS NOT NULL";
                        PreparedStatement statement = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                        statement.setInt(1, Integer.parseInt(idString));

                        ResultSet resultSet = statement.executeQuery();

                        // Get the number of rows in the result set
                        resultSet.last();
                        int numRows = resultSet.getRow();
                        resultSet.beforeFirst();

                        // Create a 2D array to hold the table data
                        Object[][] tableData = new Object[numRows][4];

                        // Populate the table data from the result set
                        int row = 0;
                        while (resultSet.next()) {
                            String nazev = resultSet.getString("nazev");
                            String datum = resultSet.getString("datum");
                            String datumTo = resultSet.getString("datumTo");
                            String amount = resultSet.getString("amount");
                            System.out.println(nazev + datum + datumTo + amount);

                            // Add the data to the 2D array
                            tableData[row][0] = nazev;
                            tableData[row][1] = datum;
                            tableData[row][2] = datumTo;
                            tableData[row][3] = amount;
                            row++;
                        }

                        // Create a table model with the table data
                        String[] columnNames = {"Nazev", "Datum", "DatumTo", "Amount"};
                        DefaultTableModel tableModel = new DefaultTableModel(tableData, columnNames);

                        // Create a table with the table model
                        JTable table = new JTable(tableModel);

                        // Add the table to a scroll pane and display it
                        JScrollPane scrollPane = new JScrollPane(table);

                        gbc.gridx = 0;
                        gbc.gridy = 2;
                        add(scrollPane, gbc);

                        refreshWindow();
                    } catch (SQLException ex) {
                        System.out.println("Error loading books: " + ex.getMessage());
                    }
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(title, gbc);


        gbc.gridx = 0;
        gbc.gridy = 1;
        add(documentIDLabel, gbc);

        gbc.gridx = 1;
        add(documentIDTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 100;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);
    }

    /**
     Refreshes the window to update its layout and adjust its size.
     This method first calls the revalidate() method to update the layout of the current component,
     then it finds the top-level container of the current component using the getWindowAncestor() method of the SwingUtilities class.
     If the top-level container is a JFrame, it calls the pack() method of the JFrame to adjust its size to fit its contents.
     Finally, it calls the repaint() method of the top-level container to repaint the entire GUI.
     */
    public void refreshWindow() {
        // Update the layout
        revalidate();
        // Find the top-level container of the current component (e.g., JFrame)
        Window topLevelContainer = SwingUtilities.getWindowAncestor(ReturnBook.this);
        // Repack the top-level container to adjust its size
        if (topLevelContainer instanceof JFrame) {
            ((JFrame) topLevelContainer).pack();
        }
        // Repaint the entire GUI
        topLevelContainer.repaint();
    }

}
