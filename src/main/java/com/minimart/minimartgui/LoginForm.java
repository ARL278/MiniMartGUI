package com.minimart.minimartgui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point Form Login untuk kompatibilitas NetBeans Form.
 * Mengarahkan dan membuka tampilan form autentikasi di package view.
 */
public class LoginForm extends com.minimart.minimartgui.view.LoginForm {

    public LoginForm() {
        super();
    }

    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
