package cz.cuni.mff.java.zapocet;

import javax.swing.JOptionPane;

public class Notification {
    public static void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, message, JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(null, message, message, JOptionPane.INFORMATION_MESSAGE);
    }

}
