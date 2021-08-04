package com.github.jrd77.codecheck;

import javax.swing.*;

public class MyTable extends JTable {

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
