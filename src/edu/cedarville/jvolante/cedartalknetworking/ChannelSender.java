/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cedarville.jvolante.cedartalknetworking;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jackson
 */
public class ChannelSender implements MessageSender{
    private OutputStream channel;
    private ChannelSenderFactory factory;
    
    private final Object messageLock = new Object();
    
    /**
     * Constructs a new ChannelSender instance.
     * @param outChannel: Channel to send messages on.
     */
    public ChannelSender(OutputStream outChannel){
        setupSender(outChannel);
        factory = null;
    }
    
    /**
     * Constructs a new ChannelSender instance, tied to a factory.
     * @param outChannel: Channel to send messages on.
     * @param fac: Factory this object belongs to.
     */
    public ChannelSender(OutputStream outChannel, ChannelSenderFactory fac){
        this(outChannel);
        factory = fac;
    }
    
    @Override
    public void sendMessage(Message message) {
        synchronized(messageLock){
            try {
                channel.write(message.send().getBytes());
            } catch (IOException ex) {
                Logger.getLogger(ChannelSender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public final void setupSender(OutputStream outChannel){
        channel = outChannel;
    }
    
    public void close() throws IOException{
        channel.close();
    }
    
}
