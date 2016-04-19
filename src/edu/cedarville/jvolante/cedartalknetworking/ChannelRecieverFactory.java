/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cedarville.jvolante.cedartalknetworking;

import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.util.Stack;

/**
 *
 * @author Jackson
 */
public class ChannelRecieverFactory {
    private Stack<ChannelReciever> availableRecievers = new Stack<>();
    
    private final Object availLock = new Object();
    
    public ChannelRecieverFactory(){
        this(2);
    }
    
    public ChannelRecieverFactory(int initialRecievers){
        for(int i = 0; i < initialRecievers; i++){
            availableRecievers.push(new ChannelReciever(null));
        }
    }
    
    public ChannelReciever getReciever(InputStream inChannel){
        synchronized(availLock){
            if(!availableRecievers.isEmpty()){
                ChannelReciever result = availableRecievers.pop();
                result.setupReciever(inChannel);
                return result;
            }
        }
        
        return new ChannelReciever(inChannel, this);
    }
    
    void returnReciever(ChannelReciever reciever){
        synchronized(availLock){
            availableRecievers.push(reciever);
        }
    }
}
