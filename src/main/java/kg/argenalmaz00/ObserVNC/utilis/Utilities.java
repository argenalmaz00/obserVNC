package kg.argenalmaz00.ObserVNC.utilis;

import javax.swing.*;
import java.awt.*;

public class Utilities {
    public static void emptyInputToDingAndRedBackground(JLabel l){
        Thread t = new Thread(()->{
            Color cur = l.getBackground();
            for (int i = 0;i < 4;i++){
                if (cur != Color.red){
                    l.setBackground(cur);
                }
                else {
                    l.setBackground(Color.red);
                }
                try {
                    Thread.sleep(700);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
