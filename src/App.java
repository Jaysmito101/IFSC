/*
 * Copyright (c) 2021.
 *
 * This code is Copyright © 2021 - 2030 by Jaysmito Mukherjee
 *
 * All rights reserved. No part of this code may be reproduced, copied, distributed, or transmitted in any form or by any means, including copying, recording, or other electronic or mechanical methods, without the prior written permission of the developer, except in the case of brief quotations embodied in critical reviews. For permission requests, write to the email given below:
 *
 * And any problems, damages you cause by the use of this code is solely your responsibility and the developer can in no way be responsible for anything.
 *
 * The Developer doesn't guarantee of the accuracy of the data provided by this code.Neither is the developer responsible for any errors in the code causing problems.
 *
 * You may use this only and only if you agree with all the conditions listed above else you should destroy all copies of this code in your possession immediately.
 *
 * Jaysmito Mukherjee
 *
 * jaysmito101@gmail.com
 *
 */


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.*;
import java.util.Scanner;

import java.net.URI;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.net.URLEncoder;

import java.util.HashMap;


public class App extends JFrame implements KeyListener, ActionListener {

    private JLabel infoText, ifscInputLabel, resultHeader;
    private JTextArea result;
    public int option;
    private JTextField ifscinput;
    private JMenuBar menuBar;
    private  JMenu options, about;
    private JMenuItem aboutdev, devssite;
    public JMenuItem searchLocal, searchrazorpay, setDataset, downloadDataset;
    private JButton find;
    private  WorkBookReader workBookReaderer;
    public String dataset;
    private InputStream datasetInputStream;

    private static App instance;

    public static App getInstance(){
        if(instance == null)
            instance = new App();
        return instance;
    }

    private App(){
        if(JOptionPane.showConfirmDialog(new JFrame(), "This project has been designed to search for IFSC codes issued by Reserve Bank of India for various bank\n" +
                "branches operating in India as on 15th of January 2021. This project is meant only for educational purpose as\n" +
                "a part of high school curriculum. The users are discouraged from using it for any sort of online or offline real\n" +
                "life transactions with banks. It is notified that the student, executing this project as well as the educational\n" +
                "institution where this project has been submitted will not be responsible for any financial loss or nonfinancial\n" +
                "damage arising from such transaction to any person or organization.\n\n" +
                "Are you sure that you want to continue ? ") != JOptionPane.YES_OPTION){
            System.exit(0);
        }
        dataset = "";
        option = 1;
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuBar = new JMenuBar();
        options = new JMenu("Options");
        searchLocal = new JMenuItem("Search From Local Data");
        searchLocal.setBackground(Color.lightGray);
        searchLocal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(App.getInstance().option == 0)
                    return;
                App.getInstance().option = 0;
                App.getInstance().searchrazorpay.setEnabled(true);
                App.getInstance().searchrazorpay.setBackground(Color.lightGray);
                App.getInstance().searchLocal.setEnabled(false);
                App.getInstance().searchLocal.setBackground(Color.GRAY);
                App.getInstance().optionChange();
            }
        });
        options.add(searchLocal);
        searchrazorpay = new JMenuItem("Search From Razorpay Web API(Recommended)");
        searchrazorpay.setBackground(Color.GRAY);
        searchrazorpay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(App.getInstance().option == 1)
                    return;
                App.getInstance().option = 1;
                App.getInstance().searchrazorpay.setEnabled(false);
                App.getInstance().searchrazorpay.setBackground(Color.GRAY);
                App.getInstance().searchLocal.setEnabled(true);
                App.getInstance().searchLocal.setBackground(Color.lightGray);
                App.getInstance().optionChange();
            }
        });
        options.add(searchrazorpay);
        setDataset = new JMenuItem("Load Dataset");
        setDataset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String data;
                do {
                    data = JOptionPane.showInputDialog("Enter correct full path of dataset [with extension]:\n");
                }while (data.length() <= 0);
                App.getInstance().dataset = data;
                App.getInstance().refreshInputStream();
            }
        });
        options.add(setDataset);
        downloadDataset = new JMenuItem("Download Dataset For Offline Search");
        downloadDataset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(JOptionPane.showConfirmDialog(new JFrame(), "Do you want to open the website?") == JOptionPane.YES_OPTION){
                    try {
                        Desktop.getDesktop().browse(new URI("https://raw.githubusercontent.com/Jaysmitio101/IFSC/main/Data.txt"));
                    }catch (Exception ex){
                        JOptionPane.showMessageDialog(new JFrame(), "Error in opening website!\n Error : " + e);
                    }
                }
            }
        });
        options.add(downloadDataset);
        menuBar.add(options);
        about = new JMenu("About");
        aboutdev = new JMenuItem("About Developer");
        aboutdev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(new JFrame(), "This application has been created\nby Jaysmito Mukherjee as a part of\nSchool Practical Work.\n\nThe Use of any part of this\nApplication without prior permission\nfrom the developer is\nis strictly prohibited!");
            }
        });
        about.add(aboutdev);
        devssite = new JMenuItem("Developers Site");
        devssite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(JOptionPane.showConfirmDialog(new JFrame(), "Do you want to open the website?") == JOptionPane.YES_OPTION){
                    try {
                        Desktop.getDesktop().browse(new URI("https://www.youtube.com/channel/UCvVUCzb12l-3FM740TdD-Vw"));
                    }catch (Exception ex){
                        JOptionPane.showMessageDialog(new JFrame(), "Error in opening website!\n Error : " + e);
                    }
                }
            }
        });
        about.add(devssite);
        menuBar.add(about);
        this.setJMenuBar(menuBar);

        JPanel infoPanel = new JPanel();
        infoPanel.setMinimumSize(new Dimension(500, 200));
        infoPanel.setLayout(new GridLayout(1, 1, 1 ,1));
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        infoText = new JLabel("Message: Welcome to IFSC Finder!");
        infoPanel.add(infoText);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(3, 1, 0, 0));
        ifscInputLabel = new JLabel("    Enter the IFSC code here: ");
        ifscInputLabel.setFont(new Font("Open Sans", Font.PLAIN, 17));
        controlPanel.add(ifscInputLabel);

        ifscinput = new JTextField(11);
        ifscinput.setFont(new Font("Consolas", Font.BOLD, 14));
        ifscinput.setMargin(new Insets(10 ,10, 10, 10));
        ifscinput.addKeyListener(this);
        controlPanel.add(ifscinput);

        find = new JButton("Search");
        find.setMargin(new Insets(5,5, 5, 5));
        find.addActionListener(this);
        find.setEnabled(false);
        controlPanel.add(find);

        mainPanel.add(controlPanel, BorderLayout.NORTH);

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BorderLayout());

        resultHeader = new JLabel("Result :");
        resultHeader.setFont(new Font("Open Sans", Font.BOLD, 22));
        resultPanel.add(resultHeader, BorderLayout.NORTH);
        result = new JTextArea("Make a search to see results!");
        result.setEditable(false);
        result.setFont(new Font("Consolas", Font.PLAIN, 16));

        JScrollPane scrollPane = new JScrollPane(result);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(resultPanel);

        this.add(mainPanel, BorderLayout.CENTER);
        this.add(infoPanel, BorderLayout.NORTH);
        this.setTitle("IFSC Finder --Jaysmito Mukherjee");
        this.setSize(500, 600);
        this.setResizable(false);
    }

    public void centerWindow(){
        this.setLocation( (Toolkit.getDefaultToolkit().getScreenSize().width - this.getSize().width)/2, (Toolkit.getDefaultToolkit().getScreenSize().height - this.getSize().height)/2);
    }

    public void optionChange(){
        return;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        return;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        return;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        result.setForeground(Color.BLACK);
        String text = ifscinput.getText();
        String res = "";
        for(int i = 0 ; i < text.length(); i++){
            if (Character.isLetter(text.charAt(i)) || Character.isDigit(text.charAt(i)))
                res = res + text.charAt(i);
        }
        res = res.toUpperCase();
        ifscinput.setText(res);
        if(ifscinput.getText().length() != 11){
            infoText.setForeground(Color.RED);
            infoText.setText("Invalid IFSC Code! It must be 11 characters.");
            find.setEnabled(false);
        }else{
            infoText.setForeground(Color.BLACK);
            infoText.setText("IFSC Code seems to be valid!");
            find.setEnabled(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(option == 1){
            this.searchRazopay();
        }else if(option == 0){
            this.searchLocal();
        }else{
            JOptionPane.showMessageDialog(new JFrame(), "An unknown Error Occurred. Closing Application!");
            System.exit(-1);
        }
    }

    private boolean refreshInputStream(){
        if(this.datasetInputStream == null){
            if(this.dataset.length() <= 0){
                JOptionPane.showMessageDialog(new JFrame(), "Please Load a dataset first!\n\nOptions -> Load Dataset");
                infoText.setText("Dataset not loaded!");
                infoText.setForeground(Color.RED);
                return  false;
            }
            try {
                datasetInputStream = new FileInputStream(new File(dataset));
                workBookReaderer = new WorkBookReader(datasetInputStream);
                workBookReaderer.run();
            }catch (Exception ex){
                infoText.setForeground(Color.RED);
                infoText.setText("Dataset not found!");
                return false;
            }
        }
        return true;
    }

    private void searchLocal() {
        if(option == 1)
            return;
        try{
            if(workBookReaderer == null){
                refreshInputStream();
                return;
            }
            if(workBookReaderer.readErr){
                JOptionPane.showMessageDialog(new JFrame(), "Cannot load data from Local Database.\n\nPlease try Razopay Web API mode.\n\nOr Try Loading Dataset Again!");
                result.setForeground(Color.RED);
                result.setText("Cannot load data from Local Database.\n\nPlease try Razopay Web API mode.\n");
                infoText.setText("Error");
                infoText.setForeground(Color.RED);
                return;
            }
            if(!workBookReaderer.isReady){
                JOptionPane.showMessageDialog(new JFrame(), "Loading dataset.\n\nThis may take a lot of time.");
                return;
            }
            infoText.setText("Loaging ...");
            result.setText("Searching... This may take a long time ..... ");
            BankDetails bankDetails = workBookReaderer.search(ifscinput.getText());
            if(bankDetails == null){
                result.setForeground(Color.RED);
                result.setText("There is no entry in the database with the given IFSC Code.\n\nTry using Razorpar Web API or check the IFSC Code again!");
                infoText.setText("Not Found!");
                infoText.setForeground(Color.RED);
            }else{
                String ouputString = "";
                ouputString += "Bank Name : " + bankDetails.name + "\n";
                ouputString += "IFSC Code : " + bankDetails.ifsc + "\n";
                ouputString += "Branch    : " + bankDetails.branch + "\n";
                ouputString += "Address   : " + bankDetails.address + "\n";
                ouputString += "City      : " + bankDetails.city + "\n";
                ouputString += "District  : " + bankDetails.district + "\n";
                ouputString += "State     : " + bankDetails.state + "\n";
                ouputString += "MICR      : " + bankDetails.micr + "\n";
                ouputString += "STD       : " + bankDetails.std + "\n";
                ouputString += "Contact   : " + bankDetails.contact + "\n";
                result.setText(ouputString);
                infoText.setForeground(Color.GREEN);
                infoText.setText("Successfully Searched Data!");
            }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(new JFrame(), "Cannot load data from Local Database.\n\nPlease try Razopay Web API mode.");
            result.setForeground(Color.RED);
            result.setText("Cannot load data from Local Database.\n\nPlease try Razopay Web API mode.\n"+ex);
            infoText.setText("Error : " + ex);
            infoText.setForeground(Color.RED);
        }
    }

    private void searchRazopay() {
        if(option == 0)
            return;
        try {
            infoText.setText("Loading ...");
            URL url = new URL("https://ifsc.razorpay.com/" + URLEncoder.encode(ifscinput.getText()));
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if(responseCode == 200){
                Scanner scanner = new Scanner(url.openStream());
                String jsonString = "";
                while(scanner.hasNext()){
                    jsonString = jsonString+ scanner.nextLine();
                }
                JSONObject json = (JSONObject) new JSONParser().parse(jsonString);
                String ouputString = "";
                ouputString += "Bank Name : " + json.get("BANK") + "\n";
                ouputString += "IFSC Code : " + json.get("IFSC") + "\n";
                ouputString += "Branch    : " + json.get("BRANCH") + "\n";
                ouputString += "Address   : " + json.get("ADDRESS") + "\n";
                ouputString += "City      : " + json.get("CITY") + "\n";
                ouputString += "MICR      : " + json.get("MICR") + "\n";
                try{
                    if(json.get("CONTACT").toString().length() > 0)
                    ouputString += "Contact   : " + json.get("CONTACT") + "\n";
                }catch (Exception exception){}
                if(json.get("RTGS").equals(true)){
                    ouputString +="This banks supports RTGS.\n";
                }else{
                    ouputString +="This banks doesn\'t support RTGS.\n";
                }
                result.setText(ouputString);
                infoText.setForeground(Color.GREEN);
                infoText.setText("Successfully Fetched Data!");
            }else if(responseCode == 404){
                result.setForeground(Color.RED);
                result.setText("There is no entry in the database with the given IFSC Code.\n\nTry using Local database or check the IFSC Code again!");
                infoText.setText("Not Found!");
                infoText.setForeground(Color.RED);
            }else if(responseCode == 500){
                JOptionPane.showMessageDialog(new JFrame(), "Server is having an internal error!\nPlease use Local Search or\n try again later!");
                result.setForeground(Color.RED);
                result.setText("Server is having an internal error!\nPlease use Local Search or\n try again later!");
                infoText.setText("Error");
                infoText.setForeground(Color.RED);
                return;
            }else{
                JOptionPane.showMessageDialog(new JFrame(), "Facing an unknown error!\nPlease use Local Search\n or try again later!");
                result.setForeground(Color.RED);
                result.setText("Facing an unknown error!\nPlease use Local Search\n or try again later!");
                infoText.setText("Error");
                infoText.setForeground(Color.RED);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(new JFrame(), "Cannot Connect to server!\nPlease use Local Search or\n try again later!");
            result.setForeground(Color.RED);
            result.setText("Cannot Connect to server!\nPlease use Local Search or\n try again later!");
            infoText.setText("Error");
            infoText.setForeground(Color.RED);
            return;
        }
    }
}

class WorkBookReader implements Runnable{
    public InputStream inputStream;
    public boolean isReady;
    public boolean readErr;
    private HashMap<String, BankDetails> dataset;

    public WorkBookReader(InputStream inputStream){
        this.inputStream = inputStream;
        this.isReady = false;
        this.readErr = false;
        dataset = new HashMap<String, BankDetails>();
    }

    @Override
    public void run() {
        if(!loadDataset()){
            readErr = true;
        }else {
            isReady = true;
        }
    }

    public boolean loadDataset(){
        try{
            Scanner scanner = new Scanner(inputStream);
            while(scanner.hasNextLine()){
                String[] rowData = scanner.nextLine().split("\t");
                dataset.put(rowData[1], new BankDetails(rowData[0], rowData[1], rowData[2], rowData[3], rowData[4], rowData[5], rowData[6], rowData[7], rowData[8], rowData[9]));
            }
        }catch (Exception ex){
            return false;
        }
        return true;
    }

    public BankDetails search(String ifsc){
        return dataset.get(ifsc);
    }
}

class BankDetails{
    String name;
    String address;
    String micr;
    String contact;
    String ifsc;
    String branch;
    String std;
    String city;
    String district;
    String state;

    public BankDetails(String name, String ifsc, String micr, String branch, String address, String std, String contact, String city, String district, String state){
        this.name = name;
        this.address = address;
        this.micr = micr;
        this.contact = contact;
        this.ifsc = ifsc;
        this.branch = branch;
        this.std = std;
        this.city = city;
        this.district = district;
        this.state = state;
    }
}