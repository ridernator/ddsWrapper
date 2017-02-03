/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rider.ddswrapper;

import com.rider.ddswrapper.messages.BigMessage;
import com.rider.ddswrapper.messages.LittleMessage;
import com.rider.ddswrapper.types.Replier;
import com.rider.ddswrapper.types.Requester;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author ridernator
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        Thread.currentThread().setName("Main Thread");
        PropertyConfigurator.configure("log4j.properties");
        DDSWrapper.getInstance().startup("DDSSettings.xml");
//        Writer writer = DDSWrapper.getInstance().getWriter("DomainParticipant1", "Publisher1", "BigMessageWriter");
//        Reader reader = DDSWrapper.getInstance().getReader("DomainParticipant1", "Subscriber1", "BigMessageReader");
//
//        reader.addListener(new Listener(Callback.ALL) {
//            @Override
//            public synchronized void newData(final Logger logger, final Object sample) {
//                BigMessage data = (BigMessage) sample;
//
//                logger.info("Received data : " + data.sequenceNumber);
//            }
//        });
//
//        BigMessage data = new BigMessage();
//        data.sequenceNumber = 0;
//
//        for (int i = 0; i < 10; i++) {
//            Thread.sleep(1000);
//
//            writer.write(data);
//            data.sequenceNumber++;
//        }
//
//        for (int i = 0; i < 5000; i++) {
//            Thread.sleep(1);
//
//            writer.write(data);
//            data.sequenceNumber++;
//        }
        
        Requester requester =  DDSWrapper.getInstance().getRequester("DomainParticipant1", "Requester1");
        Replier replier =  DDSWrapper.getInstance().getReplier("DomainParticipant1", "Replier1");
        
        LittleMessage littleMessage = new LittleMessage();
        
        BigMessage bigMessage = (BigMessage)requester.request(littleMessage);
        
        System.out.println(bigMessage);
        
        Thread.sleep(10000);

        DDSWrapper.shutdown();
        
        System.exit(0);
    }
}
