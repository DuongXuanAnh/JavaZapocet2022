package cz.cuni.mff.java.zapocet;

import javax.swing.*;
import java.awt.*;

public class MyDatePicker extends JPanel {
    private SpinnerNumberModel dayModel;
    private SpinnerListModel monthModel;
    private SpinnerNumberModel yearModel;

    public MyDatePicker() {
        dayModel = new SpinnerNumberModel(1, 1, 31, 1);
        monthModel = new SpinnerListModel(new String[]{"leden", "únor", "březen", "duben", "květen", "červen",
                "červenec", "srpen", "září", "říjen", "listopad", "prosinec"});
        yearModel = new SpinnerNumberModel(2000, 1900, 2100, 1);

        JSpinner daySpinner = new JSpinner(dayModel);
        JSpinner monthSpinner = new JSpinner(monthModel);
        JSpinner yearSpinner = new JSpinner(yearModel);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Datum"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Den"), gbc);

        gbc.gridx = 1;
        add(daySpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Měsíc"), gbc);

        gbc.gridx = 1;
        add(monthSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Rok"), gbc);

        gbc.gridx = 1;
        add(yearSpinner, gbc);
    }

    public int getDay() {
        return dayModel.getNumber().intValue();
    }

    public String getMonth() {
        return (String) monthModel.getValue();
    }

    public int getYear() {
        return yearModel.getNumber().intValue();
    }
}
