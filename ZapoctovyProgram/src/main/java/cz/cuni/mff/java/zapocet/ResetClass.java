package cz.cuni.mff.java.zapocet;

import javax.swing.*;
import java.awt.*;

public class ResetClass extends JPanel {

    public void resetPanel(String panelName){
        AddAuthor newAuthorPanel = new AddAuthor();
        getParent().add(newAuthorPanel, panelName);
        CardLayout cardLayout = (CardLayout) getParent().getLayout();
        cardLayout.show(getParent(), panelName);
    }
}
