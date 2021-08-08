package com.github.jrd77.codecheck.intellij.compoent;

import javax.swing.*;
import java.awt.*;

public class MyButton extends JButton {

    public MyButton() {
        super();
        super.setPreferredSize(new Dimension(super.getWidth()/2,super.getHeight()));
    }
}
