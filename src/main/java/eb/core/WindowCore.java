package eb.core;

import javax.swing.*;

public class WindowCore {

    public static void main(String[] args) {

        try {
            Class.forName("eb.core.Config");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            EasyBlogCore easyBlogCore = new EasyBlogCore();
            easyBlogCore.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });

        Config.checkAndUpdate();
    }
}
