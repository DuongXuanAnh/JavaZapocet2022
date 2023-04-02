package cz.cuni.mff.java.zapocet;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Book Manager");
//        frame.setPreferredSize(new Dimension(600, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new AddCustomer());
        frame.pack();
        frame.setVisible(true);
    }
}
