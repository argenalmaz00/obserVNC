package kg.argenalmaz00.ObserVNC.Ui.Widget;

import com.shinyhut.vernacular.client.VernacularClient;
import com.shinyhut.vernacular.client.VernacularConfig;
import com.shinyhut.vernacular.client.rendering.ColorDepth;
import kg.argenalmaz00.ObserVNC.Ui.MainWindow;
import kg.argenalmaz00.ObserVNC.utilis.Utilities;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class Widget {
    public static JPanel panel(){
        JPanel panelControl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton buttonAddVnc = new JButton("add vnc");
        buttonAddVnc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("show window connector");
                MainWindow.desktopWindowVnc.add(addWindowVnc());

            }
        });
        panelControl.add(buttonAddVnc);
        return panelControl;
    }
    public static JInternalFrame addWindowVnc(){
        JInternalFrame frame = new JInternalFrame("Connector",false,true,false);
        frame.setSize(400,200);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));

        // Элементы интерфейса
        JLabel pcNameLabel = new JLabel("PC Name:");
        JTextField pcNameField = new JTextField();

        JLabel ipLabel = new JLabel("IP Address:");
        JTextField ipField = new JTextField();

        JLabel portLabel = new JLabel("Port:");
        JTextField portField = new JTextField("5900");

        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean status = true;
                if (ipField.getText().isEmpty()){
                    status = false;
                    Utilities.emptyInputToDingAndRedBackground(ipLabel);
                }
                if (portField.getText().isEmpty()){
                    Utilities.emptyInputToDingAndRedBackground(portLabel);
                }
                if (status){
                    MainWindow.desktopWindowVnc.add(
                            windowVnc(pcNameField.getText(),ipField.getText(),Integer.parseInt(portField.getText())
                            )
                    );
                }
            }
        });

        panel.add(pcNameLabel);
        panel.add(pcNameField);
        panel.add(ipLabel);
        panel.add(ipField);
        panel.add(portLabel);
        panel.add(portField);
        panel.add(connectButton);
        frame.add(panel);
        frame.show();
        frame.requestFocus();
        return frame;
    }
    public static JDesktopPane desktopWindowVnc(){
        JDesktopPane desktopPane = new JDesktopPane();
        return desktopPane;
    }
    public static JInternalFrame windowVnc(String namePC,String ip,int port){
        if (namePC.isEmpty()){
            namePC = ip + ":" + port;
        }
        JInternalFrame frame = new JInternalFrame("VNC :" + namePC,true,true,true,true);
        frame.setLayout(new BorderLayout());
        frame.setMinimumSize(new Dimension(300,300));
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<ColorDepth> colorDepthJComboBox = new JComboBox<>(ColorDepth.values());
        controlPanel.add(colorDepthJComboBox);
        JLabel displayLabel = new JLabel();
        Thread t = new Thread(()->{
            VernacularConfig config = new VernacularConfig();
            VernacularClient client = new VernacularClient(config);
            config.setColorDepth(ColorDepth.BPP_8_TRUE);
            colorDepthJComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ColorDepth colorDepth = (ColorDepth) colorDepthJComboBox.getSelectedItem();
                    config.setColorDepth(colorDepth);
                    System.out.println("selecte :" + colorDepth.name());
                }
            });
            config.setErrorListener((e)->{
                throw new RuntimeException(e);
            });
            config.setPasswordSupplier(new Supplier<String>() {
                @Override
                public String get() {
                    CompletableFuture<String> completableFuture = new CompletableFuture<>();

                    JPanel panelPassword = new JPanel();
                    panelPassword.setLayout(new GridLayout(1,3));
                    JLabel labelPasswordMessage = new JLabel("Password:");
                    JTextField textFieldPassword = new JTextField();
                    JButton btSubmit = new JButton("OK");
                    btSubmit.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            completableFuture.complete(textFieldPassword.getText());
                        }
                    });
                    panelPassword.add(labelPasswordMessage);
                    panelPassword.add(textFieldPassword);
                    panelPassword.add(btSubmit);
                    frame.add(panelPassword,BorderLayout.NORTH);

                    try {
                        String pass = completableFuture.get();
                        frame.remove(panelPassword);
                        frame.revalidate();
                        frame.repaint();
                        return pass;
                    }
                    catch (Exception e){
                        frame.dispose();
                        throw new RuntimeException(e);
                    }
                }
            });
            config.setScreenUpdateListener((image -> {
                image = image.getScaledInstance(displayLabel.getWidth(), displayLabel.getHeight(),Image.SCALE_FAST);
                displayLabel.setIcon(new ImageIcon(image));
            }));
            client.start(ip,port);
            while (true){
                if (Thread.currentThread().isInterrupted()){
                    client.stop();
                    Thread.currentThread().stop();
                    return;
                }
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        t.start();
        frame.setSize(300,300);
        frame.addInternalFrameListener(new InternalFrameListener() {
            @Override
            public void internalFrameOpened(InternalFrameEvent e) {

            }

            @Override
            public void internalFrameClosing(InternalFrameEvent e) {

            }

            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                System.out.println(frame.getTitle() + " : Closed");
                t.interrupt();
                try {
                    t.join(1000);
                }
                catch (InterruptedException ex) {}
            }

            @Override
            public void internalFrameIconified(InternalFrameEvent e) {

            }

            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {

            }

            @Override
            public void internalFrameActivated(InternalFrameEvent e) {

            }

            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {

            }
        });
        frame.add(displayLabel,BorderLayout.CENTER);
        frame.add(controlPanel,BorderLayout.PAGE_END);
        frame.show();
        frame.requestFocus();
        return frame;
    }
}

