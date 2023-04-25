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

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_winter", "root", "")){
            String sql = "SELECT * FROM kniha WHERE id IN (";
            for (int i = 0; i < chosenBookID.size(); i++) {
                sql += "?";
                if (i < chosenBookID.size() - 1) {
                    sql += ",";
                }
            }
            sql += ")";

            PreparedStatement stmt = conn.prepareStatement(sql);
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

                SpinnerModel model = new SpinnerNumberModel(1, 1, amount, 1);
                JSpinner spinner = new JSpinner(model);
                spinner.setEditor(new CustomSpinnerEditor(spinner));
                spinnerPriceMap.put(spinner, id);

                // Přidání listeneru pro změny hodnoty v Spinneru
                spinner.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        // Získání hodnoty ze Spinneru
                        int value = (int) spinner.getValue();

                        int id = spinnerPriceMap.get(spinner);

                        updateQuantity(id, value);
                        totalDocumentPrice = getUpdateTotalDocumentPrice();
                        totalDocumentPriceLabel.setText(totalDocumentPrice + "");
                        refreshWindow();
                    }
                });

                gbc.gridx = 1;
                gbc.gridy = position;
                add(spinner, gbc);


                JButton removeOrderBookButton = new JButton("X");

                removeOrderBookButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        deleteBookFromFile(id);
                        ReadOrderFile();
                        totalDocumentPrice = getUpdateTotalDocumentPrice();
                        totalDocumentPriceLabel.setText(totalDocumentPrice + "");
                        remove(chosenBook);
                        remove(spinner);
                        remove(removeOrderBookButton);

                        refreshWindow();
                    }
                });

                gbc.gridx = 2;
                gbc.gridy = position;
                add(removeOrderBookButton, gbc);

                position++;
            }


        } catch (SQLException ex) {
            System.out.println("Error inserting customer: " + ex.getMessage());
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

                if(chosenBookID.size() == 0){
                    Notification.showErrorMessage("Objednávka je prázdná!");
                    return;
                }

                String type = (String) typeComboBox.getSelectedItem();
                Date selectedDate = dateChooser.getDate();
                String customerIDText = customerIDTextField.getText();
                String customerNameText = customerName.getText();

                if (!isValidCustomer(customerIDText, customerNameText)) {
                    Notification.showErrorMessage("Nebyl zadaně zprávně zákazník!");
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

                    Notification.showSuccessMessage("Doklad byl úspěšně vytvořen");

                } catch (SQLException ex) {
                    System.out.println("Error inserting document: " + ex.getMessage());
                    Notification.showErrorMessage("Chyba, zkuste to znovu nebo kontaktujte IT oddělení");
                }
            }});


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
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
                chosenBookID = new ArrayList<>();
                bookPrice = new ArrayList<>();
                bookQuantity = new ArrayList<>();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            chosenBookID = new ArrayList<>();
            bookPrice = new ArrayList<>();
            bookQuantity = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    chosenBookID.add(Integer.parseInt(line.split(" ")[0]));
                    bookPrice.add(Double.parseDouble(line.split(" ")[1]));
                    bookQuantity.add(Integer.parseInt(line.split(" ")[2]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateQuantity(int id, int value) {
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

            String sql_kniha = "UPDATE kniha SET amount = amount - ? WHERE id = ?";
            PreparedStatement statement_kniha = getConnection().prepareStatement(sql_kniha);
            statement_kniha.setInt(1, bookQuantity.get(i));
            statement_kniha.setInt(2, chosenBookID.get(i));
            statement_kniha.executeUpdate();

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

    private void deleteBookFromFile(int id){
        File inputFile = new File("OrderBooks.txt");
        ArrayList<String> lines = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line;
            while ((line = reader.readLine()) != null) {

                if(!line.isEmpty()){
                    int bookID = Integer.parseInt(line.split(" ")[0]);
                    if (bookID != id) {
                        lines.add(line);
                    }
                }
            }
            reader.close();
            FileWriter writer = new FileWriter(inputFile);
            for (String newLine : lines) {
                writer.write(newLine + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A custom editor for a JSpinner component that restricts user input to arrow key events only.
     * This class extends JSpinner.DefaultEditor and customizes the behavior of the text field component of the editor.
     * The text field is set to be non-editable and non-focusable, preventing direct text input and text selection.
     * Only arrow key events are allowed to change the value of the spinner, while other key events are ignored.
     */
    public class CustomSpinnerEditor extends JSpinner.DefaultEditor {
        /**
         * Constructs a new CustomSpinnerEditor with the specified JSpinner object.
         * @param spinner the JSpinner object to customize
         */
        public CustomSpinnerEditor(JSpinner spinner) {
            super(spinner);
            JFormattedTextField textField = getTextField();
            textField.setEditable(false); // Disable direct text input
            textField.setFocusable(false); // Disable focus to prevent text selection
        }

        /**
         * Processes key events to allow only arrow key events to change the value of the spinner.
         * @param evt the key event to process
         */
        @Override
        public void processKeyEvent(KeyEvent evt) {
            // Allow only arrow key events to change the value
            if (evt.getID() == KeyEvent.KEY_PRESSED &&
                    (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                super.processKeyEvent(evt);
            }
        }
    }

    /**
     * Deletes all contents of the file specified by the given file path.
     * @param filePath the path of the file to delete the contents of
     * @throws NullPointerException if the file path is null
     * @throws SecurityException if a security manager exists and denies write access to the file
     */
    public void deleteFileContents(String filePath) {
        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.print("");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }



}