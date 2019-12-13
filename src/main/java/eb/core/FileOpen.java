package eb.core;

import javax.swing.*;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class FileOpen {

    public static void main(String[] args) {

        JFrame frame = new JFrame();
        JFileChooser  fileChooser = new JFileChooser(".");
        Action details = fileChooser.getActionMap().get("viewTypeDetails");
        details.actionPerformed(null);
        fileChooser.showOpenDialog(frame);

    }

}
