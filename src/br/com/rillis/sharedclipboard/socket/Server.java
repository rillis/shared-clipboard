package br.com.rillis.sharedclipboard.socket;

import br.com.rillis.sharedclipboard.Main;
import br.com.rillis.sharedclipboard.clipboard.Clip;
import br.com.rillis.sharedclipboard.vars.Status;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static ServerSocket ss;

    private static DataInputStream dis;
    private static DataOutputStream dou;

    public static void init(){

    }
    public static void start(){
        try {
            Main.gui.setStatus(Status.HOSTING);
            System.out.println("[SERVER] Hosting on "+Main.gui.IP+":"+Main.PORT);
            ss = new ServerSocket(Main.PORT);

            new Thread(() -> {
                while(true){
                    try {
                        Socket s = ss.accept();
                        dis = new DataInputStream(s.getInputStream());
                        dou = new DataOutputStream(s.getOutputStream());
                        String client_ip = s.getInetAddress().getHostAddress();
                        System.out.println("[SERVER] Client connected ("+client_ip+")");
                        Main.gui.setStatus(Status.HOSTING_CONNECTED);

                        while(!s.isClosed()){
                            try {
                                String str = dis.readUTF();
                                messageReceivedFromClient(str);
                            }catch (Exception e){
                                s.close();
                            }
                        }

                        Main.gui.setStatus(Status.HOSTING);
                        System.out.println("[SERVER] Disconnected ("+client_ip+")");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendMessageToClient(String msg){
        try {
            if(Main.gui.getStatusCode() == Status.HOSTING_CONNECTED){
                dou.writeUTF(msg);
                dou.flush();
            }else{
                System.out.println("Not Connected");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void messageReceivedFromClient(String msg){
        if(Main.gui.getStatusCode() == Status.HOSTING_CONNECTED){
            Clip.setClipboard(msg);
        }
    }
}
