package kg.argenalmaz00.ObserVNC.Ui;

import kg.argenalmaz00.ObserVNC.Ui.Widget.Widget;

import javax.swing.*;
import java.awt.*;

public class MainWindow {
    public static JDesktopPane desktopWindowVnc  = Widget.desktopWindowVnc();
    public MainWindow(){
        JFrame frame = new JFrame("ObserVNC");
        frame.setSize(500,500);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(Widget.panel(),BorderLayout.NORTH);
        frame.add(desktopWindowVnc,BorderLayout.CENTER);
        frame.show();
    }

}
