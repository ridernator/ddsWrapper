/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rider.ddswrapper.types;

import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.SubscriberQos;
import java.util.HashMap;
import java.util.function.Consumer;
import org.apache.log4j.Logger;

/**
 *
 * @author ridernator
 */
public class Subscriber {

    private final com.rti.dds.subscription.Subscriber ddsSubscriber;

    private final Logger logger;

    private final HashMap<String, Reader> readers;

    private final String name;

    Subscriber(final DomainParticipant domainParticipant,
               final com.rider.ddswrapper.configuration.Subscriber subscriberXML,
               final Logger defaultLogger) {
        if (subscriberXML.getLoggerName() == null) {
            logger = defaultLogger;
        } else {
            logger = Logger.getLogger(subscriberXML.getLoggerName());
        }

        name = subscriberXML.getSubscriberName();
        readers = new HashMap<>();
        ddsSubscriber = domainParticipant.getDDSDomainParticipant().create_subscriber_with_profile(subscriberXML.getQoSLibrary(), subscriberXML.getQoSProfile(), null, StatusKind.STATUS_MASK_NONE);

        if (ddsSubscriber == null) {
            logger.error("Error creating Subscriber (DomainParticipant=\"" + domainParticipant.getName() + "\",Subscriber=\"" + subscriberXML.getSubscriberName() + "\",QoSLibrary=\"" + subscriberXML.getQoSLibrary() + "\",QoSProfile=\"" + subscriberXML.getQoSProfile() + "\",Partitions=\"" + subscriberXML.getPartitionNames() + "\")");
        } else {
            final SubscriberQos subscriberQoS = new SubscriberQos();
            ddsSubscriber.get_qos(subscriberQoS);
            
            subscriberXML.getPartitionNames().stream().forEach(partition -> subscriberQoS.partition.name.add(partition));
            
            ddsSubscriber.set_qos(subscriberQoS);

            logger.info("Created Subscriber (DomainParticipant=\"" + domainParticipant.getName() + "\",Subscriber=\"" + subscriberXML.getSubscriberName() + "\",QoSLibrary=\"" + subscriberXML.getQoSLibrary() + "\",QoSProfile=\"" + subscriberXML.getQoSProfile() + "\",Partitions=\"" + subscriberXML.getPartitionNames() + "\")");

            subscriberXML.getReaders().stream().forEach(readerXML -> {
                if (readers.containsKey(readerXML.getReaderName())) {
                    logger.warn("Subscriber (DomainParticipant=\"" + domainParticipant.getName() + "\",Publisher=\"" + subscriberXML.getSubscriberName() + "\") contains multiple Readers called \"" + readerXML.getReaderName() + "\". Using only the first one");
                } else {
                    readers.put(readerXML.getReaderName(), new Reader(domainParticipant, Subscriber.this, readerXML, logger));
                }
            });
        }
    }

    public Reader getReader(final String readerName) {
        return readers.get(readerName);
    }

    public com.rti.dds.subscription.Subscriber getDDSSubscriber() {
        return ddsSubscriber;
    }

    public String getName() {
        return name;
    }
}
