package br.com.rillis.sharedclipboard;

import br.com.rillis.sharedclipboard.clipboard.Clip;
import br.com.rillis.sharedclipboard.gui.GUI;
import br.com.rillis.sharedclipboard.socket.Client;
import br.com.rillis.sharedclipboard.socket.Server;
import br.com.rillis.sharedclipboard.vars.Status;

public class Main {

    public static int PORT = 27333;

    public static GUI gui;

    public static void main(String[] args) {



        Server.init();
        Client.init();

        Clip.init();


        gui = new GUI();
        gui.setStatus(Status.DISCONNECTED);
    }
}
