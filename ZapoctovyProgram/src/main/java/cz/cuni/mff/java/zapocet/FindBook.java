package cz.cuni.mff.java.zapocet;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.*;

public class FindBook extends JPanel {
    private JTextField searchField;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private Connection connection;

    public FindBook() {
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
        String[] columnNames = {"ID", "Název", "Cena"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultsTable = new JTable(tableModel);
        resultsTable.setDefaultEditor(Object.class, null); // Disable cell editing
        resultsTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable) e.getSource();
                    int row = target.getSelectedRow();
                    int id = (int) target.getValueAt(row, 0);
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

        // Connect to the database
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_winter", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTableModel() {
        String query = searchField.getText();
        String sql = "SELECT id, nazev, cena FROM kniha WHERE nazev LIKE '%" + query + "%'";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            tableModel.setRowCount(0);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("nazev");
                int price = resultSet.getInt("cena");
                tableModel.addRow(new Object[]{id, title, price});
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
                int amount = resultSet.getInt("amount");
                int price = resultSet.getInt("cena");
                String description = resultSet.getString("popis");

                String detailText = "Název: " + title + "\n" +
                        "Rok vydání:" + year + "\n " +
                        "Počet: " + amount + "\n" +
                        "Cena: " + price + "\n" +
                        "Popis: " + description + "\n";

                JOptionPane.showMessageDialog(this, detailText, "Detail knihy", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}