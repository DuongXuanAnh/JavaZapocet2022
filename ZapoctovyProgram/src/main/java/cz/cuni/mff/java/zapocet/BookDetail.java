package cz.cuni.mff.java.zapocet;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.*;

public class BookDetail extends JPanel {
    private JTextField searchField;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private Connection connection;
    private JButton editButton;
    private JButton updateButton;

    public BookDetail() {
        setLayout(new BorderLayout());

        // Create the search field
        searchField = new JTextField();
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTableModel();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTableModel();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTableModel();
            }
        });

        add(searchField, BorderLayout.NORTH);

        // Create the table model and table
        String[] columnNames = {"ID", "Název", "Počet kusů", "Cena"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultsTable = new JTable(tableModel);
        resultsTable.setDefaultEditor(Object.class, null); // Disable cell editing
        resultsTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = resultsTable.getSelectedRow();
                    int id = (int) resultsTable.getValueAt(row, 0);
                    showBookDetail(id);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        add(new JScrollPane(resultsTable), BorderLayout.CENTER);

        // Create the edit and update buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        editButton = new JButton("Edit");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = resultsTable.getSelectedRow();
                if (row != -1) {
                    resultsTable.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()));
                    updateButton.setEnabled(true);
                }
            }
        });
        buttonPanel.add(editButton);

        updateButton = new JButton("Update");
        updateButton.setEnabled(false);
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = resultsTable.getSelectedRow();
                if (row != -1) {
                    if (resultsTable.getCellEditor() != null) {
                        resultsTable.getCellEditor().stopCellEditing();
                    }
                    int id = (int) resultsTable.getValueAt(row, 0);
                    String title = (String) resultsTable.getValueAt(row, 1);
                    String priceAsString = resultsTable.getValueAt(row, 2).toString();
                    double price = Double.parseDouble(priceAsString);

                    updateBook(id, title, price);
                    resultsTable.setDefaultEditor(Object.class, null);
                    updateButton.setEnabled(false);

                    // Refresh the table with updated values
//                    updateTableModel();
                }
            }
        });
        buttonPanel.add(updateButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Connect to the database
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_winter", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTableModel() {
        String query = searchField.getText();
        String sql = "SELECT id, nazev, amount, cena FROM kniha WHERE nazev LIKE '%" + query + "%'";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            tableModel.setRowCount(0);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("nazev");
                int amount = resultSet.getInt("amount");
                double price = resultSet.getDouble("cena");
                tableModel.addRow(new Object[]{id, title, amount, price});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showBookDetail(int id) {
        try {
            String sql = "SELECT * FROM kniha WHERE id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String title = resultSet.getString("nazev");
                int year = resultSet.getInt("rok_vydani");
                double price = resultSet.getDouble("cena");
                String genre = resultSet.getString("zanr");
                int amount = resultSet.getInt("amount");
                String description = resultSet.getString("popis");

                JTextField titleField = new JTextField(title);
                JTextField yearField = new JTextField(String.valueOf(year));
                JTextField priceField = new JTextField(String.valueOf(price));
                JComboBox<String> genreComboBox = new JComboBox<>(new String[]{"Sci-fi (vědeckofantastický)", "Romantika", "Thriller", "Detektivka", "Fantasy", "Horor", "Komedie", "Akční", "Drama", "Historický"}); // Add your genres here
                genreComboBox.setSelectedItem(genre);
                JTextField amountField = new JTextField(String.valueOf(amount));
                JTextArea descriptionArea = new JTextArea(description);

                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("Název:"));
                panel.add(titleField);
                panel.add(new JLabel("Rok vydání:"));
                panel.add(yearField);
                panel.add(new JLabel("Cena:"));
                panel.add(priceField);
                panel.add(new JLabel("Žánr:"));
                panel.add(genreComboBox);
                panel.add(new JLabel("Množství:"));
                panel.add(amountField);
                panel.add(new JLabel("Popis:"));
                panel.add(new JScrollPane(descriptionArea));

                int result = JOptionPane.showConfirmDialog(null, panel, "Detail knihy", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    String updatedTitle = titleField.getText();
                    int updatedYear = Integer.parseInt(yearField.getText());
                    double updatedPrice = Double.parseDouble(priceField.getText());
                    String updatedGenre = (String) genreComboBox.getSelectedItem();
                    int updatedAmount = Integer.parseInt(amountField.getText());
                    String updatedDescription = descriptionArea.getText();

                    String updateSql = "UPDATE kniha SET nazev=?, rok_vydani=?, cena=?, zanr=?, amount=?, popis=? WHERE id=?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                    updateStatement.setString(1, updatedTitle);
                    updateStatement.setInt(2, updatedYear);
                    updateStatement.setDouble(3, updatedPrice);
                    updateStatement.setString(4, updatedGenre);
                    updateStatement.setInt(5, updatedAmount);
                    updateStatement.setString(6, updatedDescription);
                    updateStatement.setInt(7, id);
                    updateStatement.executeUpdate();

                    updateTableModel();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateBook(int id, String title, double price) {
        String sql = "UPDATE kniha SET nazev=?, cena=? WHERE id=?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, title);
            statement.setDouble(2, price);
            statement.setInt(3, id);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Knihy byla aktualizována v databázi.", "Aktualizace knihy", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}