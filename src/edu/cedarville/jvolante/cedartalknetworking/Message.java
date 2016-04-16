/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cedarville.jvolante.cedartalknetworking;

import java.util.Scanner;

/**
 *
 * @author Jackson
 */
public class Message {
    private final int id;
    private final String data;
    
    public Message(String messageString){
        Scanner s = new Scanner(messageString);
        
        id = s.nextInt();
        data = s.nextLine();
    }
    
    public String send(){
        return Integer.toString(id) + " " + data;
    }
    
    public int getID(){
        return id;
    }
    
    public String getData(){
        return data;
    }
}
