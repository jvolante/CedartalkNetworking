/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cedarville.jvolante.cedartalknetworking;

import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

/**
 *
 * @author Jackson
 */
public abstract class Dispatcher extends Thread{
    protected static ChannelSenderFactory senderFactory = new ChannelSenderFactory();
    protected static ChannelRecieverFactory recieverFactory = new ChannelRecieverFactory();
    
    protected final ChannelSender channelSender;
    protected final ChannelReciever channelReciever;
    
    protected boolean isGood = false;
    
    public Dispatcher(ChannelSender sender, ChannelReciever reciever){
        channelSender = sender;
        channelReciever = reciever;
        isGood = true;
    }
    
    public Dispatcher(SocketChannel sc) throws InvalidConnectionException{
        this(sc, sc);
    }
    
    public Dispatcher(ReadableByteChannel in, WritableByteChannel out) throws InvalidConnectionException{
        if(validateConnection(in, out)){
            isGood = true;
            channelSender = senderFactory.getSender(out);
            channelReciever = recieverFactory.getReciever(in);
        } else {
            throw new InvalidConnectionException();
        }
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
        }
    }
    
    protected abstract void processIncoming(Message message);
    protected abstract boolean validateConnection(ReadableByteChannel in, WritableByteChannel out);
}
