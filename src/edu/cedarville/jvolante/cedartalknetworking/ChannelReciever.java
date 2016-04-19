/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cedarville.jvolante.cedartalknetworking;

import java.io.IOException;
import java.io.InputStream;
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
    private InputStream inChannel;
    private BlockingQueue<Message> receivedMessages = new LinkedBlockingQueue<>();
    private ChannelRecieverFactory factory;
    
    private final Object recievedLock = new Object();
    
    public ChannelReciever(InputStream channel){
        setupReciever(channel);
        factory = null;
    }
    
    public ChannelReciever(InputStream channel, ChannelRecieverFactory fac){
        this(channel);
        factory = fac;
    }
    
    @Override
    public void run(){
        Scanner channelReader = new Scanner(inChannel);
        
        try{
            while(true){
                String line = channelReader.nextLine();

                Message newMessage = new Message(line);

                try {
                    receivedMessages.put(newMessage);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ChannelReciever.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch(Exception e){
            if(factory != null){
                factory.returnReciever(this);
            }
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
        
        return m;
    }
    
    public ChannelRecieverFactory getFactory(){
        return factory;
    }
    
    public void setupReciever(InputStream channel){
        inChannel = channel;
    }
    
    public void close() throws IOException{
        inChannel.close();
    }
}
