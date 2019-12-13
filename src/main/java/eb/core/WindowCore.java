package eb.core;

import javax.swing.*;

public class WindowCore {

    public static void main(String[] args) {

        SingleProcess.checkLock();

        try {
            Class.forName("eb.core.Config");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new EasyBlogCore();
        });
        Config.getCurrentAuth();

    }
}
