package com.company;

import com.company.ui.Form;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Form f = new Form();
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setSize(500,500);
        f.setVisible(true);
    }
}
