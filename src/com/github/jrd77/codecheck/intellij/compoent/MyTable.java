package com.github.jrd77.codecheck.intellij.compoent;

import javax.swing.*;

public class MyTable extends JTable {

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
