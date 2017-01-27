/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rider.ddswrapper.types;

import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.PublisherQos;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author ridernator
 */
public class Publisher {

    private final com.rti.dds.publication.Publisher ddsPublisher;
    private final Logger logger;
    private final HashMap<String, Writer> writers;
    private final String name;

    Publisher(final DomainParticipant domainParticipant, final com.rider.ddswrapper.configuration.Publisher publisherXML, final Logger defaultLogger) {
        if (publisherXML.getLoggerName() == null) {
            logger = defaultLogger;
        } else {
            logger = Logger.getLogger(publisherXML.getLoggerName());
        }

        name = publisherXML.getPublisherName();
        writers = new HashMap<>();
        ddsPublisher = domainParticipant.getDDSDomainParticipant().create_publisher_with_profile(publisherXML.getQoSLibrary(), publisherXML.getQoSProfile(), null, StatusKind.STATUS_MASK_NONE);

        if (ddsPublisher == null) {
            logger.error("Error creating Publisher (DomainParticipant=\"" + domainParticipant.getName() + "\",Publisher=\"" + publisherXML.getPublisherName() + "\",QoSLibrary=\"" + publisherXML.getQoSLibrary() + "\",QoSProfile=\"" + publisherXML.getQoSProfile() + "\",Partitions=\""+publisherXML.getPartitionName()+"\")");
        } else {
            final PublisherQos publisherQoS = new PublisherQos();
            ddsPublisher.get_qos(publisherQoS);
            for (final String partition : publisherXML.getPartitionName()) {
                publisherQoS.partition.name.add(partition);
            }            
            ddsPublisher.set_qos(publisherQoS);
            
            logger.info("Created Publisher (DomainParticipant=\"" + domainParticipant.getName() + "\",Publisher=\"" + publisherXML.getPublisherName() + "\",QoSLibrary=\"" + publisherXML.getQoSLibrary() + "\",QoSProfile=\"" + publisherXML.getQoSProfile() + "\",Partitions=\""+publisherXML.getPartitionName()+"\")");

            for (final com.rider.ddswrapper.configuration.Writer writerXML : publisherXML.getWriter()) {
                if (writers.containsKey(writerXML.getWriterName())) {
                    logger.warn("Publisher (DomainParticipant=\"" + domainParticipant.getName() + "\",Publisher=\"" + publisherXML.getPublisherName() + "\") contains multiple Writers called \"" + writerXML.getWriterName() + "\". Using only the first one");
                } else {
                    writers.put(writerXML.getWriterName(), new Writer(domainParticipant, this, writerXML, logger));
                }
            }
        }
    }

    public Writer getWriter(final String writerName) {
        return writers.get(writerName);
    }

    public com.rti.dds.publication.Publisher getDDSPublisher() {
        return ddsPublisher;
    }

    public String getName() {
        return name;
    }
}
