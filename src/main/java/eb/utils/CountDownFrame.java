package eb.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.WindowConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * @author Cqh_i
 * @Title: CountDownFrame.java 
 * @Description: 倒计时弹出窗口
 * @date 2018年10月14日 上午9:57:24   
 */
public class CountDownFrame {
    private int secends = 11;// 倒计时时间
//    private JButton ConfirmButton;
    private JButton Cancelbutton;
    private JLabel label;
    private JFrame jf;
    int res = 0;

    /**
     * @param jfather 非模态方式返回值意义不大
     */
    public void showCountDownFrame(JFrame jfather) {

//        ConfirmButton = new JButton("确认");
//        ConfirmButton.setBounds(14, 44, 99, 27);
//        ConfirmButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                res = 1;
//                jf.dispose();
//            }
//        });

        Cancelbutton = new JButton("取消");
        Cancelbutton.setBounds(139, 44, 99, 27);
        Cancelbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                res = 0;
                jf.dispose();
            }
        });

        label = new JLabel("这是一个倒计时弹出窗口");
        label.setBounds(55, 13, 165, 18);

        jf = new JFrame();
        jf.getContentPane().setLayout(null);
        jf.setSize(266, 119);
        jf.setUndecorated(true); // 去掉窗口的装饰
        jf.getRootPane().setWindowDecorationStyle(JRootPane.WARNING_DIALOG); // 采用指定的窗口装饰风格
        jf.setResizable(false);
        jf.setVisible(true);
        jf.setLocationRelativeTo(jfather);// 窗口居中
        jf.setTitle("请再次确认");
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        jf.getContentPane().add(label);
//        jf.getContentPane().add(ConfirmButton);
        jf.getContentPane().add(Cancelbutton);

        ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();
        s.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                secends--;
                String str = "取消(" + String.valueOf(secends) + "秒)";
                if (secends == 0) {
                    res = 0;
                    jf.dispose();
                } else {
                    Cancelbutton.setText(str);
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

}