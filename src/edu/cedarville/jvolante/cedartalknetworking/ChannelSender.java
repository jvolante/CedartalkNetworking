/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cedarville.jvolante.cedartalknetworking;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jackson
 */
public class ChannelSender extends Thread implements MessageSender{
    private WritableByteChannel channel;
    private BlockingQueue<Message> messages;
    private ChannelSenderFactory factory;
    
    private final Object messageLock = new Object();
    
    /**
     * Constructs a new ChannelSender instance.
     * @param outChannel: Channel to send messages on.
     */
    public ChannelSender(WritableByteChannel outChannel){
        setupSender(outChannel);
        factory = null;
    }
    
    /**
     * Constructs a new ChannelSender instance, tied to a factory.
     * @param outChannel: Channel to send messages on.
     * @param fac: Factory this object belongs to.
     */
    public ChannelSender(WritableByteChannel outChannel, ChannelSenderFactory fac){
        this(outChannel);
        factory = fac;
    }
    
    @Override
    public void sendMessage(Message message) {
        try {
            messages.put(message);
        } catch (InterruptedException ex) {
            Logger.getLogger(ChannelSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        ByteBuffer buf = ByteBuffer.allocate(2048);
        Message sending = null;
        try{
            while(channel.isOpen()){
                try {
                    sending = messages.take();
                
                    synchronized(messageLock){
                        channel.write(buf.put(sending.send().getBytes()));
                        buf.clear();
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(ChannelSender.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ChannelSender.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if(channel.isOpen()){
                    channel.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ChannelSender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public final void setupSender(WritableByteChannel outChannel){
        messages = new LinkedBlockingQueue<>();
        channel = outChannel;
    }
    
    public void close() throws IOException{
        messages.clear();
        channel.close();
    }
    
}
