/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cedarville.jvolante.cedartalknetworking;

import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;
import java.util.Stack;

/**
 *
 * @author Jackson
 */
public class ChannelSenderFactory {
    private Stack<ChannelSender> availableRecievers = new Stack<>();
    
    private final Object availLock = new Object();
    
    public ChannelSenderFactory(){
        this(2);
    }
    
    public ChannelSenderFactory(int initialSenders){
        for(int i = 0; i < initialSenders; i++){
            availableRecievers.push(new ChannelSender(null));
        }
    }
    
    public ChannelSender getSender(OutputStream outChannel){
        synchronized(availLock){
            if(!availableRecievers.isEmpty()){
                ChannelSender result = availableRecievers.pop();
                result.setupSender(outChannel);
                return result;
            }
        }
        
        return new ChannelSender(outChannel, this);
    }
    
    void returnSender(ChannelSender sender){
        synchronized(availLock){
            availableRecievers.push(sender);
        }
    }
}
