package com.company.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// класс слушающий нажатия по Меню
class DialogActionListener implements ActionListener {
    private int choiceDialog;

    public void setChoiceDialog(int choice) {
        choiceDialog = choice;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        switch (choiceDialog) {
            // About Program
            case 0: {
                AboutProgram dialog = new AboutProgram();
                dialog.pack();
                dialog.setVisible(true);
                break;
            }
            // About Authors
            case 1: {
                AboutAuthors dialog = new AboutAuthors();
                dialog.pack();
                dialog.setVisible(true);
                break;
            }
        }
    }
}
