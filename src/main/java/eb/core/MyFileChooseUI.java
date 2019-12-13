package eb.core;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.FileChooserUI;
import java.io.File;

public class MyFileChooseUI extends FileChooserUI {

    public MyFileChooseUI() {
    }

    @Override
    public FileFilter getAcceptAllFileFilter(JFileChooser fc) {
        return null;
    }

    @Override
    public FileView getFileView(JFileChooser fc) {
        return null;
    }

    @Override
    public String getApproveButtonText(JFileChooser fc) {
        return null;
    }

    @Override
    public String getDialogTitle(JFileChooser fc) {
        return null;
    }

    @Override
    public void rescanCurrentDirectory(JFileChooser fc) {

    }

    @Override
    public void ensureFileIsVisible(JFileChooser fc, File f) {

    }
}
