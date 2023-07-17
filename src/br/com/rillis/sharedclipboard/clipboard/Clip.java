package br.com.rillis.sharedclipboard.clipboard;

import br.com.rillis.sharedclipboard.Main;
import br.com.rillis.sharedclipboard.socket.Client;
import br.com.rillis.sharedclipboard.socket.Server;
import br.com.rillis.sharedclipboard.vars.Status;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

public class Clip {
    private static Clipboard clipboard;
    private static String last;

    public static void init(){
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        last = getClipboard();

        new Thread(() -> {
            try{
                while(true){
                    Thread.sleep(1000);

                    if((Main.gui.getStatusCode() == Status.CONNECTED || Main.gui.getStatusCode() == Status.HOSTING_CONNECTED) && Main.gui.isSharing()){
                        String n = getClipboard();
                        if (!last.equals(n) && n.replace(" ", "").length() > 0){
                            System.out.println("SENDING Clip: "+n.length());
                            if(Main.gui.getStatusCode() == Status.CONNECTED){
                                Client.sendMessageToServer(n);
                            }else{
                                Server.sendMessageToClient(n);
                            }
                            last = n;
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    public static String getClipboard(){
        try {
            return (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return "";
    }

    public static void setClipboard(String text){
        System.out.println("RECEIVING Clip: "+text.length());
        StringSelection stringSelection = new StringSelection(text);
        clipboard.setContents(stringSelection, null);
    }
}
