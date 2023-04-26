package cz.cuni.mff.java.zapocet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReturnBook extends JPanel {

    JScrollPane scrollPane;
    JLabel notFoundLabel = new JLabel("Not Found");
    String documentIDString;
    JLabel customerNameLabel = new JLabel("Not Found");
    List<Integer> bookIdList = new ArrayList<>();
    List<Integer> amountRentBookList = new ArrayList<>();

    DefaultTableModel tableModel;

    public ReturnBook(){
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Vrácení knihy");
        title.setFont(new Font("Serif", Font.BOLD, 20));

        JLabel documentIDLabel = new JLabel("ID objednávky");
        JTextField documentIDTextField = new JTextField(20);

        JButton findButton = new JButton("Hledat");


        JButton submitButton = new JButton("Vrátit");
        submitButton.setVisible(false);

        submitButton.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_winter", "root", "")) {

                int documentID = Integer.parseInt(documentIDString);

                for (int i = 0; i < bookIdList.size(); i++){
                    int bookID = bookIdList.get(i);
                    int bookAmount = amountRentBookList.get(i);
                    String updateBookAmountSql = "UPDATE kniha SET amount=amount + ? WHERE id=?";
                    PreparedStatement updateBookAmountStatement = conn.prepareStatement(updateBookAmountSql);
                    updateBookAmountStatement.setInt(1, bookAmount);
                    updateBookAmountStatement.setInt(2, bookID);
                    updateBookAmountStatement.executeUpdate();
                }

                String deleteDokladSql = "DELETE FROM doklad WHERE id = ?";
                PreparedStatement deleteDokladStatement = conn.prepareStatement(deleteDokladSql);
                deleteDokladStatement.setInt(1, documentID);

                String deleteDokladKnihaSql = "DELETE FROM doklad_kniha WHERE id_doklad = ?";
                PreparedStatement deleteDokladKnihaStatement = conn.prepareStatement(deleteDokladKnihaSql);
                deleteDokladKnihaStatement.setInt(1, documentID);

                String deleteDokladZakaznikSql = "DELETE FROM doklad_zakaznik WHERE id_doklad = ?";
                PreparedStatement deleteDokladZakaznikStatement = conn.prepareStatement(deleteDokladZakaznikSql);
                deleteDokladZakaznikStatement.setInt(1, documentID);

                deleteDokladStatement.executeUpdate();
                deleteDokladKnihaStatement.executeUpdate();
                deleteDokladZakaznikStatement.executeUpdate();

                Notification.showSuccessMessage("Knihy byly vráceny");
                refreshWindow();

            } catch (SQLException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        });

        documentIDTextField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Get the text entered by the user and display it
                    documentIDString = documentIDTextField.getText();

                    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_winter", "root", "")) {
                        String sql = "SELECT kniha.nazev AS nazev, doklad.datum, doklad.datumTo, kd.amount, zakaznik.jmeno, zakaznik.id, kniha.id AS bookID\n" +
                                "FROM doklad\n" +
                                "JOIN doklad_kniha AS kd ON doklad.id = kd.id_doklad\n" +
                                "JOIN kniha ON kniha.id = kd.id_kniha\n" +
                                "JOIN doklad_zakaznik AS dz ON dz.id_doklad = doklad.id\n" +
                                "JOIN zakaznik ON zakaznik.id = dz.id_zakaznik\n" +
                                "WHERE doklad.id = ? AND doklad.datumTo IS NOT NULL;";

                        PreparedStatement statement = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                        statement.setInt(1, Integer.parseInt(documentIDString));

                        ResultSet resultSet = statement.executeQuery();

                        // Get the number of rows in the result set
                        resultSet.last();
                        int numRows = resultSet.getRow();
                        resultSet.beforeFirst();

                        // Create a 2D array to hold the table data
                        Object[][] tableData = new Object[numRows][4];

                        if(numRows > 0){
                            submitButton.setVisible(true);

                            // Populate the table data from the result set
                            int row = 0;

                            if (resultSet.next()){
                                customerNameLabel.setText("");
                                customerNameLabel = new JLabel( "Zákazník: " + resultSet.getString("jmeno") + " ( ID: " +resultSet.getInt("id") + ")");
                                gbc.gridx = 0;
                                gbc.gridy = 2;
                                add(customerNameLabel, gbc);

                                String nazev = resultSet.getString("nazev");
                                String datum = resultSet.getString("datum");
                                String datumTo = resultSet.getString("datumTo");
                                String amount = resultSet.getString("amount");

                                bookIdList.add(resultSet.getInt("bookID"));
                                amountRentBookList.add(resultSet.getInt("amount"));

                                tableData[row][0] = nazev;
                                tableData[row][1] = datum;
                                tableData[row][2] = datumTo;
                                tableData[row][3] = amount;
                                row++;

                                while (resultSet.next()) {

                                    nazev = resultSet.getString("nazev");
                                    datum = resultSet.getString("datum");
                                    datumTo = resultSet.getString("datumTo");
                                    amount = resultSet.getString("amount");

                                    bookIdList.add(resultSet.getInt("bookID"));
                                    amountRentBookList.add(resultSet.getInt("amount"));

                                    // Add the data to the 2D array
                                    tableData[row][0] = nazev;
                                    tableData[row][1] = datum;
                                    tableData[row][2] = datumTo;
                                    tableData[row][3] = amount;
                                    row++;
                                }
                            }

                            // Create a table model with the table data

                            String[] columnNames = {"Nazev", "Datum", "DatumTo", "Amount"};

                            if (tableModel == null) {
                                tableModel = new DefaultTableModel(tableData, columnNames);
                                JTable table = new JTable(tableModel);
                                scrollPane = new JScrollPane(table);
                                gbc.gridx = 0;
                                gbc.gridy = 3;
                                add(scrollPane, gbc);
                                notFoundLabel.setVisible(false);
                            } else {
                                tableModel.setDataVector(tableData, columnNames);
                                tableModel.fireTableDataChanged();
                            }

                        }else{
                            submitButton.setVisible(false);
                            if(scrollPane != null) {
                                scrollPane.setVisible(false);
                            }
                            notFoundLabel.setVisible(true);

                            gbc.gridx = 0;
                            gbc.gridy = 2;
                            add(notFoundLabel, gbc);

                            customerNameLabel.setVisible(false);
                        }
                        refreshWindow();
                    } catch (SQLException ex) {
                        System.out.println("Error: " + ex.getMessage());
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

        gbc.gridx = 2;
        add(findButton, gbc);

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
