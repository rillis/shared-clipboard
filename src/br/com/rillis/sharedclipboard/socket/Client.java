package br.com.rillis.sharedclipboard.socket;

import br.com.rillis.sharedclipboard.Main;
import br.com.rillis.sharedclipboard.clipboard.Clip;
import br.com.rillis.sharedclipboard.vars.Status;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    private static DataInputStream dis;
    private static DataOutputStream dou;

    public static void init(){

    }

    public static void connect(String ip){
        try{
            System.out.println("[CLIENT] Trying to connect to "+ip+":"+Main.PORT);
            Socket s = new Socket(ip, Main.PORT);
            dis = new DataInputStream(s.getInputStream());
            dou = new DataOutputStream(s.getOutputStream());
            Main.gui.setStatus(Status.CONNECTED);
            System.out.println("[CLIENT] Connected");
            new Thread(() -> {
                try {
                    while(!s.isClosed()){
                        try {
                            String str = dis.readUTF();
                            messageReceivedFromServer(str);
                        }catch (Exception e){
                            s.close();
                        }
                    }
                    Main.gui.setStatus(Status.DISCONNECTED);
                    System.out.println("[CLIENT] Connection Lost");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }).start();
        }catch (UnknownHostException e){
            JOptionPane.showMessageDialog(null, "Unknown Host", "ERROR", JOptionPane.ERROR_MESSAGE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void sendMessageToServer(String msg){
        try {
            if(Main.gui.getStatusCode() == Status.CONNECTED){
                dou.writeUTF(msg);
                dou.flush();
            }else{
                System.out.println("Not Connected");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void messageReceivedFromServer(String msg){
        if(Main.gui.getStatusCode() == Status.CONNECTED){
            Clip.setClipboard(msg);
        }
    }
}
