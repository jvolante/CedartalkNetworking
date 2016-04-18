/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cedarville.jvolante.cedartalknetworking;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jackson
 */
public abstract class Dispatcher extends Thread{
    protected static ChannelSenderFactory senderFactory = new ChannelSenderFactory();
    protected static ChannelRecieverFactory recieverFactory = new ChannelRecieverFactory();
    
    protected ChannelSender channelSender;
    protected ChannelReciever channelReciever;
    
    protected boolean isGood = false;
    
    protected WritableByteChannel out = null;
    protected ReadableByteChannel in = null;
    
    public Dispatcher(ChannelSender sender, ChannelReciever reciever){
        channelSender = sender;
        channelReciever = reciever;
        isGood = true;
    }
    
    public Dispatcher(SocketChannel sc) throws InvalidConnectionException{
        this(sc, sc);
    }
    
    public Dispatcher(ReadableByteChannel in, WritableByteChannel out) throws InvalidConnectionException{
        this.in = in;
        this.out = out;
    }
    
    public boolean isGood(){
        return isGood;
    }
    
    @Override
    public final void run(){
        if(isGood()){
            while(true){
                Message m = channelReciever.nextMessage();

                processIncoming(m);
            }
        }
    }
    
    public void sendMessage(Message m){
        if(isGood()){
            channelSender.sendMessage(m);
        } else if(out != null){
            ByteBuffer buf = ByteBuffer.allocate(2048);
            try{
                out.write(buf.put(m.send().getBytes()));
                buf.clear();
            } catch (IOException ex) {
                Logger.getLogger(ChannelSender.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if(out.isOpen()){
                        out.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ChannelSender.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void close() throws IOException{
        channelSender.close();
        channelReciever.close();
    }
    
    protected void onGoodConnection(){
        isGood = true;
        channelSender = senderFactory.getSender(out);
        channelReciever = recieverFactory.getReciever(in);
        
        channelSender.start();
        channelReciever.start();
    }
    
    protected abstract void processIncoming(Message message);
    protected abstract boolean validateConnection();
}
