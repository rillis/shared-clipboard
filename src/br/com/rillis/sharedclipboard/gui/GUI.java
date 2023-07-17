package br.com.rillis.sharedclipboard.gui;

import br.com.rillis.sharedclipboard.Main;
import br.com.rillis.sharedclipboard.socket.Client;
import br.com.rillis.sharedclipboard.socket.Server;
import br.com.rillis.sharedclipboard.vars.Status;
import jdk.jshell.spi.ExecutionControl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class GUI extends JFrame {
    JPanel contentPane;
    JLabel status;
    JLabel lblIP;
    MaskedIPAddressTextField txtIP;
    JButton btnConnect;
    JButton btnHost;
    JButton btnPort;
    JCheckBox chkShare;

    public String IP;
    private int statusCode;

    public GUI() {
        super("Shared Clipboard");

        try {
            // Set Windows look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setSize(500, 290);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);

        contentPane = new JPanel();
        contentPane.setBounds(0, 0, getWidth(), getHeight());
        contentPane.setLayout(null);
        setContentPane(contentPane);

        int i = 10;
        int space = 20;

        JLabel lbl = new JLabel("Shared Clipboard");
        lbl.setBounds(0, i, 500, 30);
        i += lbl.getHeight()+space;
        lbl.setFont(lbl.getFont().deriveFont(25f));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        lbl.setVerticalAlignment(JLabel.CENTER);
        contentPane.add(lbl);

        status = new JLabel("Status: Disconnected");
        status.setForeground(java.awt.Color.RED);
        status.setBounds(0, i, 500, 20);
        i += status.getHeight()+space;
        status.setFont(lbl.getFont().deriveFont(17f));
        status.setHorizontalAlignment(JLabel.CENTER);
        status.setVerticalAlignment(JLabel.CENTER);
        contentPane.add(status);

        lblIP = new JLabel("IP:");
        lblIP.setBounds(0, i, 100, 30);
        lblIP.setFont(lbl.getFont().deriveFont(17f));
        lblIP.setHorizontalAlignment(JLabel.CENTER);
        lblIP.setVerticalAlignment(JLabel.CENTER);
        contentPane.add(lblIP);

        txtIP = new MaskedIPAddressTextField();
        txtIP.setColumns(15);
        txtIP.setBounds(100, i, 350, lblIP.getHeight());
        txtIP.setFont(lbl.getFont().deriveFont(17f));
        i += lblIP.getHeight()+space;
        contentPane.add(txtIP);

        btnPort = new JButton("Port");
        btnPort.setBounds(20, i, 220, 30);
        btnPort.addActionListener(e -> {
            String chosenPort = JOptionPane.showInputDialog(null, "Current port is "+Main.PORT+"\nType the new port", JOptionPane.MESSAGE_TYPE_PROPERTY, JOptionPane.PLAIN_MESSAGE);
            if(chosenPort != null && chosenPort.replace(" ","").length()>0){
                try{
                    int newPort = Integer.parseInt(chosenPort);
                    if(newPort > 1023 && newPort <= 65535){
                        Main.PORT = newPort;
                        JOptionPane.showMessageDialog(null, "Port changed!", JOptionPane.MESSAGE_TYPE_PROPERTY, JOptionPane.PLAIN_MESSAGE);
                    }else{
                        JOptionPane.showMessageDialog(null, "Must be between 1024 - 65535.", JOptionPane.MESSAGE_TYPE_PROPERTY, JOptionPane.ERROR_MESSAGE);
                    }
                }catch (Exception er){
                    JOptionPane.showMessageDialog(null, "Cannot parse to integer.", JOptionPane.MESSAGE_TYPE_PROPERTY, JOptionPane.ERROR_MESSAGE);
                }
            }

        });
        contentPane.add(btnPort);

        chkShare = new JCheckBox("Share this Clipboard");
        chkShare.setBounds(290, i, 130, 30);
        //chkShare.setBackground(Color.RED);
        chkShare.setSelected(true);
        i += chkShare.getHeight()+3;
        contentPane.add(chkShare);

        btnConnect = new JButton("Connect");
        btnConnect.setBounds(20, i, 220, 30);
        btnConnect.addActionListener(e -> {
            Client.connect(txtIP.getText());
        });
        contentPane.add(btnConnect);

        btnHost = new JButton("Host");
        btnHost.setBounds(240, i, 220, 30);
        i += btnHost.getHeight()+space;
        btnHost.addActionListener(e -> {
            Server.start();
        });
        contentPane.add(btnHost);

        InetAddress localhost = null;
        try {
            localhost = InetAddress.getLocalHost();
            IP = localhost.getHostAddress();
            JLabel lblyIP = new JLabel("Your IP: "+IP);
            lblyIP.setBounds(0, i-space+5, 500, 10);
            lblyIP.setHorizontalAlignment(JLabel.CENTER);
            lblyIP.setVerticalAlignment(JLabel.CENTER);
            contentPane.add(lblyIP);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        setVisible(true);

        revalidate();
        repaint();
    }

    private void setLocked(boolean locked){
        txtIP.setEnabled(!locked);
        btnConnect.setEnabled(!locked);
        btnHost.setEnabled(!locked);
        btnPort.setEnabled(!locked);
    }

    public void setStatus(int statuscode){
        this.statusCode = statuscode;
        if(statuscode == Status.DISCONNECTED){
            status.setText("Status: Disconnected");
            status.setForeground(Color.RED);
            setLocked(false);
        } else if (statuscode == Status.HOSTING || statuscode == Status.HOSTING_CONNECTED) {
            status.setText(statuscode == Status.HOSTING?"Status: Hosting":"Status: Hosting (Connected)");
            status.setForeground(Color.BLUE);
            setLocked(true);
        }else if (statuscode == Status.CONNECTED) {
            status.setText("Status: Connected");
            status.setForeground(Color.GREEN);
            setLocked(true);
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isSharing(){
        return chkShare.isSelected();
    }
}
