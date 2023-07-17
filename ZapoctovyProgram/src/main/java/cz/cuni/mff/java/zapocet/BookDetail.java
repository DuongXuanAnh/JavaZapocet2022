package cz.cuni.mff.java.zapocet;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a panel for displaying and managing book details, including the book's ID, title, number of copies, and price.
 * It allows users to search for books by title, edit book details, update the book database, and add books to their shopping cart.
 * The panel includes a table for displaying book details, as well as buttons for editing, updating, and adding books to the shopping cart.
 * The class connects to a MySQL database to retrieve book data and update book details as needed.
 * Additionally, the class provides functionality for displaying and managing book author information, including the author's ID and name.
 */
public class BookDetail extends JPanel {
    private JTextField searchField;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JButton editButton;
    private JButton updateButton;

    private int bookID;

    private ArrayList<JPanel> authorComBoxDeleteBtnPanelList;

    int idAuthorFilter;

    private JButton addBookToDocumentButton;

    private JComboBox<String> allAuthorsComboBox;
    Map<String, Integer> authorIdMap;

    /**
     * Constructs a new BookDetail object, which provides a user interface for displaying and managing book details.
     * The user interface includes a search field, a table of books with edit and update buttons, and a button for adding a book to a document.
     * The constructor connects to a MySQL database and sets up listeners for events such as document changes and mouse clicks on the table rows.
     * It calls the updateTableModel() method to update the table with the latest search results.
     */
    public BookDetail() {

        setLayout(new BorderLayout());

        // Create a titled border with a larger font
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                "Knihy",
                TitledBorder.CENTER,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 20)); // Set the font to 16pt Arial Bold

        // Set the border on the panel
        setBorder(titledBorder);

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

        JPanel northPanel = new JPanel(new BorderLayout());

        add(northPanel, BorderLayout.NORTH);


        NorthPanelArea(northPanel);

        JLabel westLabel = new JLabel("Název knihy: ");
        northPanel.add(westLabel, BorderLayout.WEST);
        northPanel.add(searchField, BorderLayout.CENTER);

        // Create the table model and table
        String[] columnNames = {"ID", "Název", "Počet kusů", "Cena"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultsTable = new JTable(tableModel);
        resultsTable.setDefaultEditor(Object.class, null); // Disable cell editing
        resultsTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    authorComBoxDeleteBtnPanelList = new ArrayList<>();
                    int row = resultsTable.getSelectedRow();
                    bookID = (int) resultsTable.getValueAt(row, 0);
                    showBookDetail(bookID);
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
                    bookID = (int) resultsTable.getValueAt(row, 0);
                    String title = (String) resultsTable.getValueAt(row, 1);
                    String priceAsString = resultsTable.getValueAt(row, 2).toString();
                    double price = Double.parseDouble(priceAsString);

                    updateBook(bookID, title, price);
                    resultsTable.setDefaultEditor(Object.class, null);
                    updateButton.setEnabled(false);

                }
            }
        });

        buttonPanel.add(updateButton);

        add(buttonPanel, BorderLayout.SOUTH);

        addBookToDocumentButton = new JButton("Přidat do košíku");
        addBookToDocumentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = resultsTable.getSelectedRow();
                if (row != -1) {

                    if (resultsTable.getCellEditor() != null) {
                        resultsTable.getCellEditor().stopCellEditing();
                    }

                    int id = (int) resultsTable.getValueAt(row, 0);
                    double price = (double) resultsTable.getValueAt(row, 3);
                    int amount = (int) resultsTable.getValueAt(row, 2);

                    if(amount >= 1){
                        addBookToDocument(id, price);
                    }else{
                        Notification.showErrorMessage("Kniha není momentálně dostupná");
                    }
                }
            }
        });
        buttonPanel.add(addBookToDocumentButton);

        add(buttonPanel, BorderLayout.SOUTH);

        updateTableModel();
    }

    /**
     Updates the table model with books matching the search query and, if an author filter is active,
     also filters by the selected author ID.
     */
    private void updateTableModel() {
        String query = searchField.getText();
        String sql;
        if(idAuthorFilter == 0){
            sql = "SELECT id, nazev, amount, cena FROM kniha WHERE nazev LIKE '%" + query + "%'";
        }else{
            sql = "SELECT * \n" +
                    "FROM `kniha`\n" +
                    "JOIN kniha_autor \n" +
                    "ON kniha.id = kniha_autor.id_kniha\n" +
                    "JOIN autor\n" +
                    "ON kniha_autor.id_autor = autor.id\n" +
                    "WHERE nazev LIKE '%" + query + "%'\n" +
                    "AND autor.id = ?";
        }
        try {
            PreparedStatement statement = Config.getConnection().prepareStatement(sql);

            if(idAuthorFilter != 0){
                statement.setInt(1, idAuthorFilter);
            }
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

    /**
     Creates a JPanel with a JComboBox that displays all the authors stored in the database and a delete button to remove the panel.

     @param selectedAuthorId the ID of the author to be selected in the JComboBox. If 0, no author is selected.

     @return the JPanel created with the JComboBox and the delete button.
     */
    private JPanel createPanelAuthorsComboBox(int selectedAuthorId) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        allAuthorsComboBox = new JComboBox<>();
        authorIdMap = new HashMap<>();
        try (Connection conn = Config.getConnection()) {
            String sql = "SELECT id, jmeno FROM autor";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("jmeno");
                allAuthorsComboBox.addItem(name);
                authorIdMap.put(name, id); // Store name and ID in the map
                if (id == selectedAuthorId) {
                    allAuthorsComboBox.setSelectedItem(name);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error loading authors: " + ex.getMessage());
        }

        JButton deleteButton = new JButton("X");
        deleteButton.addActionListener(e -> {
            if(authorComBoxDeleteBtnPanelList.size() > 1){
                Container parent = panel.getParent();
                authorComBoxDeleteBtnPanelList.remove(panel);
                parent.remove(panel);
                parent.revalidate();
                parent.repaint();
            }else{
                Notification.showErrorMessage("Kniha musí mít alespoň jednoho autora");
            }
        });

        panel.add(allAuthorsComboBox);
        authorComBoxDeleteBtnPanelList.add(panel);
        panel.add(Box.createHorizontalStrut(5)); // add some horizontal space between the components
        panel.add(deleteButton);

        return panel;
    }

    /**
     Shows a dialog box with the details of a book with the given ID.
     @param id the ID of the book to show details for
     */
    private void showBookDetail(int id) {
        try {
            String sql = "SELECT * \n" +
                    "FROM kniha \n" +
                    "JOIN kniha_autor \n" +
                    "ON kniha_autor.id_kniha = kniha.id\n" +
                    "JOIN autor ON autor.id = kniha_autor.id_autor\n" +
                    "WHERE kniha.id = ?";
            PreparedStatement statement = Config.getConnection().prepareStatement(sql);
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
                descriptionArea.setRows(5); // set the number of rows
                descriptionArea.setLineWrap(true); // enable line wrapping
                descriptionArea.setWrapStyleWord(true); // wrap at word boundaries

                JScrollPane scrollPane = new JScrollPane(descriptionArea);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


                JPanel panel = new JPanel();
                panel.setLayout(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.fill = GridBagConstraints.HORIZONTAL;


                gbc.gridx = 0;
                gbc.gridy = 0;
                panel.add(new JLabel("Název:"), gbc);

                gbc.gridx = 1;
                panel.add(titleField, gbc);

                gbc.gridx = 0;
                gbc.gridy = 1;
                panel.add(new JLabel("Žánr:"), gbc);

                gbc.gridx = 1;
                panel.add(genreComboBox, gbc);

                gbc.gridx = 0;
                gbc.gridy = 2;
                panel.add(new JLabel("Rok vydání:"), gbc);

                gbc.gridx = 1;
                panel.add(yearField, gbc);

                gbc.gridx = 0;
                gbc.gridy = 3;
                panel.add(new JLabel("Cena:"), gbc);

                gbc.gridx = 1;
                panel.add(priceField, gbc);

                gbc.gridx = 0;
                gbc.gridy = 4;
                panel.add(new JLabel("Množství:"), gbc);

                gbc.gridx = 1;
                panel.add(amountField, gbc);

                gbc.gridx = 0;
                gbc.gridy = 5;
                panel.add(new JLabel("Popis:"), gbc);

                // Add the JScrollPane to the panel instead of the descriptionArea
                gbc.gridx = 1;
                gbc.gridy = 5;
                panel.add(scrollPane, gbc);

                gbc.gridx = 0;
                gbc.gridy = 6;
                panel.add(new JLabel("Autor:"), gbc);

                gbc.gridx = 1;
                panel.add(createPanelAuthorsComboBox(resultSet.getInt("autor.id")), gbc);

                int row = 7;
                while(resultSet.next()){
                    gbc.gridx = 1;
                    gbc.gridy = row;
                    panel.add(createPanelAuthorsComboBox(resultSet.getInt("autor.id")), gbc);
                    row++;
                }

                gbc.gridx = 1;
                gbc.gridy = 1000;
                JButton addAuthorButton = new JButton("Přidat autora");
                panel.add(addAuthorButton, gbc);

                addAuthorButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JPanel newComboBox = createPanelAuthorsComboBox(0); // replace 0 with the default author ID
                        gbc.gridy--; // increment the grid Y position
                        panel.add(newComboBox, gbc); // add the new combo box to the panel

                        // Update the layout and preferred size of the panel
                        panel.revalidate();
                        panel.setPreferredSize(panel.getPreferredSize());

                        // Find the top-level container of the current component (e.g., JFrame)
                        Window topLevelContainer = SwingUtilities.getWindowAncestor(panel);

                        // Repack the top-level container to adjust its size
                        if (topLevelContainer instanceof JFrame) {
                            ((JFrame) topLevelContainer).pack();
                        }

                        // Repaint the entire GUI
                        topLevelContainer.repaint();
                    }
                });

                int result = JOptionPane.showConfirmDialog(null, panel, "Detail knihy", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    try{
                        String updatedTitle = titleField.getText();
                        int updatedYear = Integer.parseInt(yearField.getText());
                        double updatedPrice = Double.parseDouble(priceField.getText());
                        String updatedGenre = (String) genreComboBox.getSelectedItem();
                        int updatedAmount = Integer.parseInt(amountField.getText());
                        String updatedDescription = descriptionArea.getText();

                        String updateSql = "UPDATE kniha SET nazev=?, rok_vydani=?, cena=?, zanr=?, amount=?, popis=? WHERE id=?";
                        PreparedStatement updateStatement = Config.getConnection().prepareStatement(updateSql);
                        updateStatement.setString(1, updatedTitle);
                        updateStatement.setInt(2, updatedYear);
                        updateStatement.setDouble(3, updatedPrice);
                        updateStatement.setString(4, updatedGenre);
                        updateStatement.setInt(5, updatedAmount);
                        updateStatement.setString(6, updatedDescription);
                        updateStatement.setInt(7, id);
                        updateStatement.executeUpdate();

                        deleteAllConnectiveAuthorsBooks(bookID);

                        // loop through each panel in the list
                        for (JPanel p : authorComBoxDeleteBtnPanelList) {
                            // get the JComboBox from the panel
                            JComboBox<String> comboBox = (JComboBox<String>) p.getComponent(0);
                            // get the selected value from the JComboBox
                            String selectedValue = (String) comboBox.getSelectedItem();
                            // do something with the selected value
                            System.out.println("Selected value: " + selectedValue);

                            System.out.println(authorIdMap.get(selectedValue));

                            addAuthorForBook(authorIdMap.get(selectedValue), bookID);
                        }

                        updateTableModel();

                        Notification.showSuccessMessage("Kniha byla upravená");

                    }catch(SQLException e){
                        Notification.showErrorMessage("Nestala chyba, zkuste to znovu");
                        System.out.println("Error: " + e.getMessage());
                    }

                }


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     Updates the details of a book in the database.
     @param id the ID of the book to update
     @param title the new title of the book
     @param price the new price of the book
     */
    private void updateBook(int id, String title, double price) {
        String sql = "UPDATE kniha SET nazev=?, cena=? WHERE id=?";
        try {
            PreparedStatement statement = Config.getConnection().prepareStatement(sql);
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


    /**
     Adds a book to the user's shopping cart by writing its ID, price, and quantity to a file.
     @param id the ID of the book to add
     @param price the price of the book to add
     */
    private void addBookToDocument(int id, double price){
        String fileName = "OrderBooks.txt";
        String content = id + " " + price + " " + 1;
        try {
            FileWriter fileWriter = new FileWriter(fileName, true);
            fileWriter.write(System.lineSeparator()); // add a new line separator
            fileWriter.write(content);
            fileWriter.close();
            System.out.println("Successfully appended to the file.");
            String message = "Kniha byla přidána do košíku!";
            String title = "Info";
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     Sets up the north panel of the GUI by adding a JComboBox to filter books by author.
     @param panel the JPanel to add the JComboBox to
     */
    private void NorthPanelArea(JPanel panel){
        JComboBox<String> authorComboBox = new JComboBox<>();
        authorComboBox.addItem("");
        Map<String, Integer> authorIdMap = new HashMap<>(); // Map to store author names and their IDs
        try (Connection conn = Config.getConnection()) {
            String sql = "SELECT id, jmeno FROM autor";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("jmeno");
                authorComboBox.addItem(name);
                authorIdMap.put(name, id); // Store name and ID in the map
            }
        } catch (SQLException ex) {
            System.out.println("Error loading authors: " + ex.getMessage());
        }

        authorComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String selectedAuthor = (String) authorComboBox.getSelectedItem();
                    if(selectedAuthor.isEmpty()){
                        idAuthorFilter = 0;
                        updateTableModel();
                        return;
                    }
                    Integer authorId = authorIdMap.get(selectedAuthor);
                    idAuthorFilter = authorId;


                    try (Connection conn = Config.getConnection()) {
                        String sql = "SELECT * \n" +
                                "FROM `kniha` \n" +
                                "JOIN kniha_autor\n" +
                                "ON kniha_autor.id_kniha = kniha.id\n" +
                                "JOIN autor ON autor.id = kniha_autor.id_autor\n" +
                                "WHERE autor.id = ?";
                        PreparedStatement statement = conn.prepareStatement(sql);
                        statement.setInt(1, authorId);
                        ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            updateTableModel();
                        }
                    }catch (SQLException ex) {
                        System.out.println("Error loading authors: " + ex.getMessage());
                    }
                }
            }
        });
        panel.add(authorComboBox, BorderLayout.NORTH);
    }

    /**
     Adds an author for a given book ID to the "kniha_autor" table.
     This method inserts a new row into the "kniha_autor" table with the given book ID and author ID.
     @param idAutor the ID of the author to be added
     @param idBook the ID of the book to add the author to
     @throws SQLException if a database access error occurs or the SQL query is invalid
     */
    private void addAuthorForBook(int idAutor, int idBook) throws SQLException{
        String sql_kniha_autor = "INSERT INTO kniha_autor (id_kniha, id_autor) VALUES (?, ?)";
        try{
            PreparedStatement statement = Config.getConnection().prepareStatement(sql_kniha_autor);
            statement.setInt(1, idBook);
            statement.setInt(2, idAutor);
            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     Deletes all connective authors books associated with a given book ID.
     This method deletes all rows from the "kniha_autor" table where the "id_kniha" column matches the given book ID.
     @param idBook the ID of the book whose connective authors books should be deleted
     @throws SQLException if a database access error occurs or the SQL query is invalid
     */
    private void deleteAllConnectiveAuthorsBooks(int idBook){
        String sql_kniha_autor = "DELETE FROM kniha_autor WHERE id_kniha = ?";
        try{
            PreparedStatement statement = Config.getConnection().prepareStatement(sql_kniha_autor);
            statement.setInt(1, idBook);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}