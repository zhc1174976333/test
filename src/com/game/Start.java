package com.game;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Start {

    public static void main(String[] args){
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }catch (Exception e){
            e.printStackTrace();
        }
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(Boolean.TRUE);
        try {
            mainFrame.getGraphics().drawImage(ImageIO.read(Start.class.getResource("logo.png")), 10, 35, 295, 370, null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
