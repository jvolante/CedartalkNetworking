/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cedarville.jvolante.cedartalknetworking;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jackson
 */
public class ChannelReciever extends Thread implements MessageReciever{
    private ReadableByteChannel inChannel;
    private BlockingQueue<Message> receivedMessages = new LinkedBlockingQueue<>();
    private ChannelRecieverFactory factory;
    
    private final Object recievedLock = new Object();
    
    public ChannelReciever(ReadableByteChannel channel){
        setupReciever(channel);
        factory = null;
    }
    
    public ChannelReciever(ReadableByteChannel channel, ChannelRecieverFactory fac){
        this(channel);
        factory = fac;
    }
    
    @Override
    public void run(){
        Scanner channelReader = new Scanner(Channels.newInputStream(inChannel));
        
        while(inChannel.isOpen()){
            String line = channelReader.nextLine();
            
            Message newMessage = new Message(line);
            
            try {
                receivedMessages.put(newMessage);
            } catch (InterruptedException ex) {
                Logger.getLogger(ChannelReciever.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(factory != null && receivedMessages.isEmpty()){
            factory.returnReciever(this);
        }
    }

    @Override
    public Message nextMessage() {
        Message m = null;
        try {
            m = receivedMessages.take();
        } catch (InterruptedException ex) {
            Logger.getLogger(ChannelReciever.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(!inChannel.isOpen() && receivedMessages.isEmpty() && factory != null){
            factory.returnReciever(this);
        }
        
        return m;
    }
    
    public ChannelRecieverFactory getFactory(){
        return factory;
    }
    
    public void setupReciever(ReadableByteChannel channel){
        inChannel = channel;
    }
    
    public void close() throws IOException{
        inChannel.close();
    }
}
