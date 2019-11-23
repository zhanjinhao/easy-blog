package eb.core;

import eb.service.BaiduService;
import eb.service.QiniuService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class EasyBlogCore extends JFrame implements ActionListener {

    // 屏幕截图的暂存路径
    public String captureImgPath;

    public EasyBlogCore() {
        super("easy-blog");
        try {
            // 设置为系统界面风格
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/cute.jpg"));
        this.setIconImage(imageIcon.getImage());
        initWindow();
        initDir();
        initTrayIcon();
    }

    // 公共区的按钮：屏幕截图、本地文件、保存所有、移除所有
    private JButton captureCut, localFile, saveAll, removeAll;

    // 一个面板
    private JPanel outsidePanel;

    // 临时存储当前的图像
    private BufferedImage tempBufferedImage;

    // 放置很多份图片的面板
    private JTabbedPane jtp;

    // 一个一直会递增的索引，用于标认图片
    private int index;

    private void initDir() {
        // 初始化存放暂时图片的路径
        String canonicalPath = null;
        try {
            canonicalPath = new File(".").getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (canonicalPath == null) {
            System.exit(-1);
        }
        captureImgPath = canonicalPath + File.separator + "temp.jpg";
        System.out.println("canonicalPath : " + canonicalPath);

        // 浏览图片的路径
        File file2 = new File(Config.LOCAL_DIR_PATH);
        if (!file2.exists() || !file2.isDirectory()) {
            Config.LOCAL_DIR_PATH = "D:\\";
        }

        // 图片保存至本地的路径
        File file3 = new File(Config.SAVE_TO_LOCAL_PATH);
        if (!file3.exists() || !file3.isDirectory()) {
            Config.SAVE_TO_LOCAL_PATH = "D:\\";
        }
    }

    private void initWindow() {
        JPanel publicOperateButtons = new JPanel();
        /**
         *  设置公共操作区
         */
        // 创建公共操作区的按钮
        captureCut = new JButton("<html><span color=red>开始截取</span><html>");
        localFile = new JButton("<html><span color=red>本地文件</span><html>");
        saveAll = new JButton("<html><span color=red>保存所有</span><html>");
        removeAll = new JButton("<html><span color=red>全部移除</span><html>");
        captureCut.addActionListener(this);
        saveAll.addActionListener(this);
        removeAll.addActionListener(this);
        localFile.addActionListener(this);
        publicOperateButtons.add(captureCut);
        publicOperateButtons.add(localFile);
        publicOperateButtons.add(saveAll);
        publicOperateButtons.add(removeAll);
        TitledBorder publicOperate = BorderFactory.createTitledBorder("公共操作区");
        publicOperateButtons.setBorder(publicOperate);
        JPanel all = new JPanel();
        all.add(publicOperateButtons);
        this.getContentPane().add(all, BorderLayout.SOUTH);     // 公共操作区放在页面的 下部

        /**
         * 设置起始界面
         */
        outsidePanel = new JPanel(new BorderLayout());  // 设置为边界布局
        outsidePanel.setBackground(Color.BLACK);  // 背景设置为黑色
        JLabel start = new JLabel("EASY-BLOG", JLabel.CENTER);
        start.setFont(new Font("黑体", Font.BOLD, 40));
        start.setForeground(Color.RED);
        outsidePanel.add(start, BorderLayout.CENTER);
        this.getContentPane().add(outsidePanel, BorderLayout.CENTER);   // 放在界面的下面

        // 设置JFrame的大小
        this.setSize(500, 300);

        // 初始化放置图片的区域
        jtp = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

        // 设置JFrame的大小为屏幕居中
        this.setLocationRelativeTo(null);
        // 设置JFrame的大小可见
        this.setVisible(true);
    }

    private void captureCut() {
        try {
            this.setVisible(false);
            Thread.sleep(500);  // 睡500毫秒是为了让主窗完全不见
            Robot ro = new Robot();
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension di = tk.getScreenSize();
            Rectangle rec = new Rectangle(0, 0, di.width, di.height);

            BufferedImage bi = ro.createScreenCapture(rec);

            JFrame jf = new JFrame();
            Temp temp = new Temp(jf, bi, di.width, di.height);
            jf.getContentPane().add(temp, BorderLayout.CENTER);
            jf.setUndecorated(true);
            jf.setSize(di);
            jf.setVisible(true);
            jf.setAlwaysOnTop(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 公用的处理保存图片的方法
     * 这个方法不再私有了
     */
    public void doSave(BufferedImage currentImg) {
        try {
            if (currentImg == null) {
                JOptionPane.showMessageDialog(this, "图片不能为空!!", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JFileChooser jfc = new JFileChooser(Config.SAVE_TO_LOCAL_PATH);
            jfc.setDialogType(JFileChooser.OPEN_DIALOG);

            int i = jfc.showSaveDialog(this);
            if (i == JFileChooser.APPROVE_OPTION) {
                File file = jfc.getSelectedFile();
                String ext = "jpg";
                file = new File(file.toString().toLowerCase() + File.separator + ext);

                String absolutePath = file.getAbsolutePath();
                Config.SAVE_TO_LOCAL_PATH = absolutePath.substring(0, absolutePath.lastIndexOf('\\'));

                // BufferedImage 后缀名 文件
                if (ImageIO.write(currentImg, ext, file)) {
                    JOptionPane.showMessageDialog(this, "保存成功！");
                } else
                    JOptionPane.showMessageDialog(this, "保存失败！");
            }
        } catch (Exception exe) {
            exe.printStackTrace();
        }
    }

    private boolean isImage(File file) {
        if (!file.exists()) {
            return false;
        }
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
            if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void doLocalFile() {
        try {

            JFileChooser jfc2 = new JFileChooser(Config.LOCAL_DIR_PATH);
            int i = jfc2.showSaveDialog(this);
            File file = null;

            if (i == JFileChooser.APPROVE_OPTION) {
                file = jfc2.getSelectedFile();


                if (!isImage(file)) {
                    JOptionPane.showMessageDialog(null, "请选择图片文件！");
                    return;
                }

                BufferedImage sourceImg = ImageIO.read(new FileInputStream(file));
                this.setVisible(false);
                Thread.sleep(500);  // 睡500毫秒是为了让主窗完全不见
                JFrame jf = new JFrame();
                Temp temp = new Temp(jf, sourceImg, sourceImg.getWidth(), sourceImg.getHeight());
                jf.getContentPane().add(temp, BorderLayout.CENTER);
                jf.setUndecorated(true);
                jf.setSize(new Dimension(sourceImg.getWidth(), sourceImg.getHeight()));
                jf.setVisible(true);
                jf.setAlwaysOnTop(true);
            }

        } catch (Exception exe) {
            exe.printStackTrace();
            this.setVisible(true);
        }
    }

    public void doSaveAll() {
        if (jtp.getTabCount() == 0 || index == 0) {
            JOptionPane.showMessageDialog(this, "图片不能为空!!", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JFileChooser jfc = new JFileChooser(Config.SAVE_TO_LOCAL_PATH);
        jfc.setDialogType(JFileChooser.OPEN_DIALOG);
        int i = jfc.showSaveDialog(this);
        if (i == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            String ext = "png";
            String name = file.toString();
            //起一个线程去保存这些图片并显示出进度条
            new SaveAllThread(name, ext).setVisible(true);
        }
    }


    // 专门用来保存所有图片的线程类,它还要显示出保存的进度条
    private class SaveAllThread extends JDialog implements Runnable {
        private String name;    // 文件名头部份
        private String ext;     // 文件格式
        private JProgressBar jpb;   // 一个进度条
        private JLabel info;        // 一个信息显示条
        private int allTask, doneTask;      // 所有任务,已完成任务

        public SaveAllThread(String name, String ext) {
            super(EasyBlogCore.this, "保存", true);
            this.name = name;
            this.ext = ext;
            initWindow();
        }

        private void initWindow() {
            jpb = new JProgressBar();
            allTask = jtp.getTabCount();
            jpb.setMaximum(allTask);
            jpb.setMinimum(0);
            jpb.setValue(0);
            jpb.setStringPainted(true);
            setProgressBarString();
            info = new JLabel();
            this.getContentPane().setBackground(Color.CYAN);
            this.add(info, BorderLayout.NORTH);
            this.add(jpb, BorderLayout.SOUTH);
            this.setUndecorated(true);
            this.setSize(500, 70);
            this.setLocationRelativeTo(EasyBlogCore.this);
            new Thread(this).start();
        }

        private void setProgressBarString() {
            jpb.setString("" + doneTask + "/" + allTask);
        }

        public void run() {
            try {
                for (int i = 0; i < allTask; i++) {
                    PicPanel pp = (PicPanel) jtp.getComponentAt(i);
                    BufferedImage image = pp.getImage();
                    File f = new File(name + "-" + (doneTask + 1) + File.separator + ext.toLowerCase());

                    String absolutePath = f.getAbsolutePath();

                    Config.SAVE_TO_LOCAL_PATH = absolutePath.substring(0, absolutePath.lastIndexOf('\\'));

                    info.setText("<html><b>正在保存到: </b>" + f.toString() + "</html>");
                    ImageIO.write(image, ext, f);
                    doneTask++;
                    jpb.setValue(doneTask);
                    setProgressBarString();
                    Thread.sleep(500);
                }
                JOptionPane.showMessageDialog(this, "保存完毕!!");
                this.dispose();
            } catch (Exception exe) {
                exe.printStackTrace();
                this.dispose();
            }
        }
    }

    // 一个内部类，它表示一个面板，一个可以被放进tabpane的面板
    // 也有自己的一套处理保存和复制的方法
    private class PicPanel extends JPanel implements ActionListener {
        JButton save, copy, remove, ocr, qiniu;   //表示保存，复制，关闭，识别文字，上传至七牛云的按钮
        BufferedImage get;  // 面板里的图片

        public PicPanel(BufferedImage get) {
            super(new BorderLayout());
            this.get = get;
            initPanel();
        }

        public BufferedImage getImage() {
            return get;
        }

        private void initPanel() {
            save = new JButton("保存");
            copy = new JButton("复制");
            ocr = new JButton("识别文字");
            qiniu = new JButton("上传至七牛云");
            remove = new JButton("移除");

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(copy);
            buttonPanel.add(save);
            buttonPanel.add(ocr);
            buttonPanel.add(qiniu);
            buttonPanel.add(remove);

            JLabel icon = new JLabel(new ImageIcon(get));
            this.add(new JScrollPane(icon), BorderLayout.CENTER);
            this.add(buttonPanel, BorderLayout.SOUTH);
            save.addActionListener(this);
            copy.addActionListener(this);
            remove.addActionListener(this);
            ocr.addActionListener(this);
            qiniu.addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == save) {
                doSave(get);
            } else if (source == copy) {
                doCopy(get);
            } else if (source == remove) {
                get = null;
                doRemove(this);
            } else if (source == ocr) {
                BaiduService.doOcr(get, captureImgPath);
            } else if (source == qiniu) {
                QiniuService.doQiniuUpload(get, captureImgPath);
            }
        }
    }

    //处理关闭事件
    private void doRemove(Component c) {
        jtp.remove(c);
        index--;
        c = null;
        System.gc();

        if (index == 0) {
            /**
             * 设置起始界面
             */
            outsidePanel.removeAll();
            jtp = null;
            System.gc();
            JLabel start = new JLabel("EASY-BLOG", JLabel.CENTER);
            start.setFont(new Font("黑体", Font.BOLD, 40));
            start.setForeground(Color.RED);
            outsidePanel.add(start, BorderLayout.CENTER);
            this.getContentPane().add(outsidePanel, BorderLayout.CENTER);   // 放在界面的下面

            // 设置JFrame的大小
            this.setSize(500, 300);

            // 初始化放置图片的区域
            jtp = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
            SwingUtilities.updateComponentTreeUI(outsidePanel);
            this.setLocationRelativeTo(null);
        }
    }


    private void doRemoveAll() {
        int tabCount = jtp.getTabCount();
        for (int i = tabCount - 1; i >= 0; i--) {
            jtp.remove(i);
            index--;
        }

        /**
         * 设置起始界面
         */
        outsidePanel.removeAll();
        jtp = null;
        System.gc();
        JLabel start = new JLabel("EASY-BLOG", JLabel.CENTER);
        start.setFont(new Font("黑体", Font.BOLD, 40));
        start.setForeground(Color.RED);
        outsidePanel.add(start, BorderLayout.CENTER);
        this.getContentPane().add(outsidePanel, BorderLayout.CENTER);   // 放在界面的下面

        // 设置JFrame的大小
        this.setSize(500, 300);

        // 初始化放置图片的区域
        jtp = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        SwingUtilities.updateComponentTreeUI(outsidePanel);
        this.setLocationRelativeTo(null);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == captureCut) {
            captureCut();
        } else if (source == removeAll) {
            doRemoveAll();
        } else if (source == saveAll) {
            doSaveAll();
        } else if (source == localFile) {
            doLocalFile();
        }
    }


    /**
     * 公共的处理把当前的图片加入剪帖板的方法
     */
    public void doCopy(final BufferedImage image) {
        try {
            if (tempBufferedImage == null) {
                JOptionPane.showMessageDialog(this, "图片不能为空!!", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Transferable trans = new Transferable() {
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[]{DataFlavor.imageFlavor};
                }

                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return DataFlavor.imageFlavor.equals(flavor);
                }

                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                    if (isDataFlavorSupported(flavor))
                        return image;
                    throw new UnsupportedFlavorException(flavor);
                }
            };
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
            JOptionPane.showMessageDialog(this, "已复制到系统粘帖板!!");
        } catch (Exception exe) {
            exe.printStackTrace();
            JOptionPane.showMessageDialog(this, "复制到系统粘帖板出错!!", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updates() {
        this.setVisible(true);
        this.setSize(1000, 750);
        this.setLocationRelativeTo(null);
        if (tempBufferedImage != null) {
            // 如果索引是0，表示一张图片都没有被加入过，要清除当前的东西，重新把tabpane放进来
            if (index == 0) {
                outsidePanel.removeAll();
                outsidePanel.add(jtp, BorderLayout.CENTER);
            } else {
                // 否则的话,直接对tabpane添加面板就可以了
                // 就什么都不用做了
            }
            PicPanel pic = new PicPanel(tempBufferedImage);
            jtp.addTab("图片" + (++index), pic);
            jtp.setSelectedComponent(pic);
            SwingUtilities.updateComponentTreeUI(outsidePanel);
        }
    }


    //一个暂时类，用于显示当前的屏幕图像
    private class Temp extends JPanel implements MouseListener, MouseMotionListener {
        private BufferedImage bi;
        private int width, height;
        private int startX, startY, endX, endY, tempX, tempY;
        private JFrame jf;
        private Rectangle select = new Rectangle(0, 0, 0, 0);   //表示选中的区域
        private Cursor cs = new Cursor(Cursor.CROSSHAIR_CURSOR);                    //表示一般情况下的鼠标状态
        private States current = States.DEFAULT;        // 表示当前的编辑状态
        private Rectangle[] rec;        //表示八个编辑点的区域

        //下面四个常量，分别表示谁是被选中的那条线上的端点
        public static final int START_X = 1;
        public static final int START_Y = 2;
        public static final int END_X = 3;
        public static final int END_Y = 4;
        private int currentX, currentY; //当前被选中的X和Y,只有这两个需要改变
        private Point p = new Point();  //当前鼠标移的地点
        private boolean showTip = true; //是否显示提示.如果鼠标左键一按,则提示不再显了

        public Temp(JFrame jf, BufferedImage bi, int width, int height) {
            this.jf = jf;
            this.bi = bi;
            this.width = width;
            this.height = height;
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
            initRecs();
        }

        private void initRecs() {
            rec = new Rectangle[8];
            for (int i = 0; i < rec.length; i++) {
                rec[i] = new Rectangle();
            }
        }

        public void paintComponent(Graphics g) {
            g.drawImage(bi, 0, 0, width, height, this);
            g.setColor(Color.RED);
            g.drawLine(startX, startY, endX, startY);
            g.drawLine(startX, endY, endX, endY);
            g.drawLine(startX, startY, startX, endY);
            g.drawLine(endX, startY, endX, endY);
            int x = startX < endX ? startX : endX;
            int y = startY < endY ? startY : endY;
            select = new Rectangle(x, y, Math.abs(endX - startX), Math.abs(endY - startY));
            int x1 = (startX + endX) / 2;
            int y1 = (startY + endY) / 2;
            g.fillRect(x1 - 2, startY - 2, 5, 5);
            g.fillRect(x1 - 2, endY - 2, 5, 5);
            g.fillRect(startX - 2, y1 - 2, 5, 5);
            g.fillRect(endX - 2, y1 - 2, 5, 5);
            g.fillRect(startX - 2, startY - 2, 5, 5);
            g.fillRect(startX - 2, endY - 2, 5, 5);
            g.fillRect(endX - 2, startY - 2, 5, 5);
            g.fillRect(endX - 2, endY - 2, 5, 5);
            rec[0] = new Rectangle(x - 5, y - 5, 10, 10);
            rec[1] = new Rectangle(x1 - 5, y - 5, 10, 10);
            rec[2] = new Rectangle((startX > endX ? startX : endX) - 5, y - 5, 10, 10);
            rec[3] = new Rectangle((startX > endX ? startX : endX) - 5, y1 - 5, 10, 10);
            rec[4] = new Rectangle((startX > endX ? startX : endX) - 5, (startY > endY ? startY : endY) - 5, 10, 10);
            rec[5] = new Rectangle(x1 - 5, (startY > endY ? startY : endY) - 5, 10, 10);
            rec[6] = new Rectangle(x - 5, (startY > endY ? startY : endY) - 5, 10, 10);
            rec[7] = new Rectangle(x - 5, y1 - 5, 10, 10);
            if (showTip) {
                g.setColor(Color.CYAN);
                g.fillRect(p.x, p.y, 170, 20);
                g.setColor(Color.RED);
                g.drawRect(p.x, p.y, 170, 20);
                g.setColor(Color.BLACK);
                g.drawString("请按住鼠标左键不放选择截图区", p.x, p.y + 15);
            }
        }

        //根据东南西北等八个方向决定选中的要修改的X和Y的座标
        private void initSelect(States state) {
            switch (state) {
                case DEFAULT:
                    currentX = 0;
                    currentY = 0;
                    break;
                case EAST:
                    currentX = (endX > startX ? END_X : START_X);
                    currentY = 0;
                    break;
                case WEST:
                    currentX = (endX > startX ? START_X : END_X);
                    currentY = 0;
                    break;
                case NORTH:
                    currentX = 0;
                    currentY = (startY > endY ? END_Y : START_Y);
                    break;
                case SOUTH:
                    currentX = 0;
                    currentY = (startY > endY ? START_Y : END_Y);
                    break;
                case NORTH_EAST:
                    currentY = (startY > endY ? END_Y : START_Y);
                    currentX = (endX > startX ? END_X : START_X);
                    break;
                case NORTH_WEST:
                    currentY = (startY > endY ? END_Y : START_Y);
                    currentX = (endX > startX ? START_X : END_X);
                    break;
                case SOUTH_EAST:
                    currentY = (startY > endY ? START_Y : END_Y);
                    currentX = (endX > startX ? END_X : START_X);
                    break;
                case SOUTH_WEST:
                    currentY = (startY > endY ? START_Y : END_Y);
                    currentX = (endX > startX ? START_X : END_X);
                    break;
                default:
                    currentX = 0;
                    currentY = 0;
                    break;
            }
        }

        public void mouseMoved(MouseEvent me) {
            doMouseMoved(me);
            initSelect(current);
            if (showTip) {
                p = me.getPoint();
                repaint();
            }
        }

        //特意定义一个方法处理鼠标移动,是为了每次都能初始化一下所要选择的地区
        private void doMouseMoved(MouseEvent me) {
            if (select.contains(me.getPoint())) {
                this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                current = States.MOVE;
            } else {
                States[] st = States.values();
                for (int i = 0; i < rec.length; i++) {
                    if (rec[i].contains(me.getPoint())) {
                        current = st[i];
                        this.setCursor(st[i].getCursor());
                        return;
                    }
                }
                this.setCursor(cs);
                current = States.DEFAULT;
            }
        }

        public void mouseExited(MouseEvent me) {
        }

        public void mouseEntered(MouseEvent me) {
        }

        public void mouseDragged(MouseEvent me) {
            int x = me.getX();
            int y = me.getY();
            if (current == States.MOVE) {
                startX += (x - tempX);
                startY += (y - tempY);
                endX += (x - tempX);
                endY += (y - tempY);
                tempX = x;
                tempY = y;
            } else if (current == States.EAST || current == States.WEST) {
                if (currentX == START_X) {
                    startX += (x - tempX);
                    tempX = x;
                } else {
                    endX += (x - tempX);
                    tempX = x;
                }
            } else if (current == States.NORTH || current == States.SOUTH) {
                if (currentY == START_Y) {
                    startY += (y - tempY);
                    tempY = y;
                } else {
                    endY += (y - tempY);
                    tempY = y;
                }
            } else if (current == States.NORTH_EAST || current == States.NORTH_EAST ||
                    current == States.SOUTH_EAST || current == States.SOUTH_WEST) {
                if (currentY == START_Y) {
                    startY += (y - tempY);
                    tempY = y;
                } else {
                    endY += (y - tempY);
                    tempY = y;
                }
                if (currentX == START_X) {
                    startX += (x - tempX);
                    tempX = x;
                } else {
                    endX += (x - tempX);
                    tempX = x;
                }
            } else {
                startX = tempX;
                startY = tempY;
                endX = me.getX();
                endY = me.getY();
            }
            this.repaint();
        }

        public void mousePressed(MouseEvent me) {
            showTip = false;
            tempX = me.getX();
            tempY = me.getY();
        }

        public void mouseReleased(MouseEvent me) {
            if (me.isPopupTrigger()) {
                if (current == States.MOVE) {
                    showTip = true;
                    p = me.getPoint();
                    startX = 0;
                    startY = 0;
                    endX = 0;
                    endY = 0;
                    repaint();
                } else {
                    jf.dispose();
                    updates();
                }
            }
        }

        public void mouseClicked(MouseEvent me) {
            if (me.getClickCount() == 2) {
                //Rectangle rec=new Rectangle(startX,startY,Math.abs(endX-startX),Math.abs(endY-startY));
                Point p = me.getPoint();
                if (select.contains(p)) {
                    if (select.x + select.width < this.getWidth() && select.y + select.height < this.getHeight()) {
                        tempBufferedImage = bi.getSubimage(select.x, select.y, select.width, select.height);
                        jf.dispose();
                        updates();
                    } else {
                        int wid = select.width, het = select.height;
                        if (select.x + select.width >= this.getWidth()) {
                            wid = this.getWidth() - select.x;
                        }
                        if (select.y + select.height >= this.getHeight()) {
                            het = this.getHeight() - select.y;
                        }
                        tempBufferedImage = bi.getSubimage(select.x, select.y, wid, het);
                        jf.dispose();
                        updates();
                    }
                }
            }
        }
    }
    private void initTrayIcon() {
        try {

            SystemTray st = SystemTray.getSystemTray();
            Image im = ImageIO.read(this.getClass().getResource("/cute.jpg"));
            PopupMenu pm = new PopupMenu("xxxx");
            pm.add(new MenuItem("about")).addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    JOptionPane.showMessageDialog(EasyBlogCore.this,
                            "<html><Font size=4 color=blue>作者: ISJINHAO<br>" +
                            "github: https://github.com/isjinhao/easy-blog&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<br><br>感谢: https://www.jb51.net/article/75114.htm</Font></html>");
                }
            });
            pm.addSeparator();
            pm.add(new MenuItem("window")).addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    EasyBlogCore.this.setVisible(true);
                }
            });
            pm.add(new MenuItem("capture")).addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    captureCut();
                }
            });
            pm.add(new MenuItem("local-file")).addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                   doLocalFile();
                }
            });
            pm.add(new MenuItem("exit")).addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    System.exit(0);
                }
            });
            TrayIcon ti = new TrayIcon(im, "easy-blog", pm);
            st.add(ti);
            ti.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    EasyBlogCore.this.setVisible(true);
                }
            });
        } catch (Exception exe) {
            exe.printStackTrace();
        }
    }
}