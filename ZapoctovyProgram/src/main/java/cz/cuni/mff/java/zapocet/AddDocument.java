package cz.cuni.mff.java.zapocet;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddDocument extends JPanel {

    JLabel totalDocumentPriceLabel = new JLabel("");
    double totalDocumentPrice = 0;

    JLabel customerName = new JLabel("");

    ArrayList<Integer> chosenBookID = new ArrayList<>();
    ArrayList<Double> bookPrice = new ArrayList<>();
    ArrayList<Integer> bookQuantity = new ArrayList<>();

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
                dateChooser.setVisible(!selectedOption.equals("Koupit"));
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

                            if (id == Integer.parseInt(idString)) {
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

        JLabel chosenBooks = new JLabel("Vybrané knihy:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(chosenBooks, gbc);

        ReadOrderFile();

        String sql = "SELECT * FROM kniha WHERE id IN (";
        for (int i = 0; i < chosenBookID.size(); i++) {
            sql += "?";
            if (i < chosenBookID.size() - 1) {
                sql += ",";
            }
        }
        sql += ")";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_winter", "root", "");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < chosenBookID.size(); i++) {
                stmt.setInt(i + 1, chosenBookID.get(i));
            }
            ResultSet rs = stmt.executeQuery();

            // Vytvoření Mapy pro mapování Spinneru na cenu knihy
            Map<JSpinner, Integer> spinnerPriceMap = new HashMap<>();

            int position = 6;
            while (rs.next()) {
                int id = rs.getInt("id");
                String nazev = rs.getString("nazev");
                int amount = rs.getInt("amount");
                double cena = rs.getDouble("cena");

                totalDocumentPrice += cena;

                JLabel chosenBook = new JLabel("* " + nazev + " - " + cena + "Kč");
                gbc.gridx = 0;
                gbc.gridy = position;
                add(chosenBook, gbc);
                SpinnerModel model = new SpinnerNumberModel(1, 0, amount, 1);
                JSpinner spinner = new JSpinner(model);
                spinnerPriceMap.put(spinner, id);

                // Přidání listeneru pro změny hodnoty v Spinneru
                spinner.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        // Získání hodnoty ze Spinneru
                        int value = (int) spinner.getValue();

                        int id = spinnerPriceMap.get(spinner);

                        updateQuantityInFile(id, value);
                        totalDocumentPrice = getUpdateTotalDocumentPrice();
                        totalDocumentPriceLabel.setText(totalDocumentPrice + "");
                        refreshWindow();
                    }
                });

                gbc.gridx = 1;
                gbc.gridy = position;
                add(spinner, gbc);
                position++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        JLabel celkemLabel = new JLabel("Celkem: ");
        gbc.gridx = 0;
        gbc.gridy = 99;
        add(celkemLabel, gbc);
        totalDocumentPriceLabel.setText(totalDocumentPrice + " Kč");
        gbc.gridx = 1;
        gbc.gridy = 99;
        add(totalDocumentPriceLabel, gbc);

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
                String customerIDText = customerIDTextField.getText();
                String customerNameText = customerName.getText();

                if (!isValidCustomer(customerIDText, customerNameText)) {
                    showErrorMessage("Nebyl zadaně zprávně zákazník");
                    return;
                }
                int customerID = getCustomerID(customerIDText);

                try {
                    int dokladId = -1;
                    if (type.equals("Koupit")) {
                        dokladId = insertDocument(totalDocumentPrice, null);
                    } else {
                        String dateRentTo = String.format("%1$tY-%1$tm-%1$td", selectedDate);
                        dokladId = insertDocument(totalDocumentPrice, dateRentTo);
                    }
                    if (dokladId != -1) {
                        insertDocumentItems(dokladId);
                        insertDocumentCustomer(dokladId, customerID);
                    }
                } catch (SQLException ex) {
                    System.out.println("Error inserting document: " + ex.getMessage());
                }
            }});
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

    private void ReadOrderFile() {
        String filePath = "OrderBooks.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    chosenBookID.add(Integer.parseInt(line.split(" ")[0]));
                    bookPrice.add(Double.parseDouble(line.split(" ") [1]));
                    bookQuantity.add(Integer.parseInt(line.split(" ")[2]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void updateQuantityInFile(int id, int value) {
        for(int i = 0; i < chosenBookID.size(); i++){
            if(chosenBookID.get(i) == id){
                bookQuantity.set(i, value);
            }
        }
    }

    private double getUpdateTotalDocumentPrice(){
        double temp = 0;
        for(int i = 0; i < chosenBookID.size(); i++){
            temp += bookPrice.get(i) * bookQuantity.get(i);
        }
        return temp;
    }

    private void errorMessage(String error){
        JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE);
    }



    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/java_winter", "root", "");
    }

    private int insertDocument(double totalPrice, String dateRentTo) throws SQLException {
        String sql_doklad = (dateRentTo == null) ?
                "INSERT INTO doklad (totalPrice) VALUES (?)" :
                "INSERT INTO doklad (datumTo, totalPrice) VALUES (?, ?)";
        PreparedStatement statement_doklad = getConnection().prepareStatement(sql_doklad, Statement.RETURN_GENERATED_KEYS);
        if (dateRentTo == null) {
            statement_doklad.setDouble(1, totalPrice);
        } else {
            statement_doklad.setString(1, dateRentTo);
            statement_doklad.setDouble(2, totalPrice);
        }
        int rowsInserted = statement_doklad.executeUpdate();
        int dokladId = -1;
        if (rowsInserted > 0) {
            ResultSet generatedKeys = statement_doklad.getGeneratedKeys();
            if (generatedKeys.next()) {
                dokladId = generatedKeys.getInt(1);
            }
        }
        return dokladId;
    }

    private void insertDocumentItems(int dokladId) throws SQLException {
        for (int i = 0; i < chosenBookID.size(); i++) {
            String sql_doklad_kniha = "INSERT INTO doklad_kniha (id_doklad, id_kniha, amount) VALUES (?, ?, ?)";
            PreparedStatement statement_doklad_kniha = getConnection().prepareStatement(sql_doklad_kniha);
            statement_doklad_kniha.setInt(1, dokladId);
            statement_doklad_kniha.setInt(2, chosenBookID.get(i));
            statement_doklad_kniha.setInt(3, bookQuantity.get(i));
            statement_doklad_kniha.executeUpdate();
        }
    }

    private void insertDocumentCustomer(int dokladId, int customerID) throws SQLException {
        String sql_doklad_zakaznik = "INSERT INTO doklad_zakaznik (id_doklad, id_zakaznik) VALUES (?, ?)";
        PreparedStatement statement_doklad_zakaznik = getConnection().prepareStatement(sql_doklad_zakaznik);
        statement_doklad_zakaznik.setInt(1, dokladId);
        statement_doklad_zakaznik.setInt(2, customerID);
        statement_doklad_zakaznik.executeUpdate();
    }

    private boolean isValidCustomer(String customerID, String customerName) {
        return !customerID.isEmpty() && !customerName.equals("Not found");
    }

    private int getCustomerID(String customerID) {
        return Integer.parseInt(customerID);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}