/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cedarville.jvolante.cedartalknetworking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
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
    
    protected OutputStream out = null;
    protected InputStream in = null;
    
    public Dispatcher(ChannelSender sender, ChannelReciever reciever){
        channelSender = sender;
        channelReciever = reciever;
        isGood = true;
    }
    
    public Dispatcher(Socket sc) throws InvalidConnectionException, IOException{
        this(sc.getInputStream(), sc.getOutputStream());
    }
    
    public Dispatcher(InputStream in, OutputStream out) throws InvalidConnectionException{
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
            try{
                out.write(m.send().getBytes());
            } catch (IOException ex) {
                Logger.getLogger(ChannelSender.class.getName()).log(Level.SEVERE, null, ex);
                
                try {
                    out.close();
                } catch (IOException e) {
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
        
        channelReciever.start();
    }
    
    protected abstract void processIncoming(Message message);
    protected abstract boolean validateConnection();
}
