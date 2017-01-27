/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rider.ddswrapper.types;

import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_ERROR;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.DataWriter;
import com.rti.dds.topic.Topic;
import java.lang.reflect.InvocationTargetException;
import org.apache.log4j.Logger;

/**
 *
 * @author ridernator
 */
public class Writer {

    private DataWriter ddsWriter;
    private final Logger logger;
    private final Duration_t infiniteWait;
    private final DomainParticipant domainParticipant;
    private final Publisher publisher;
    private final com.rider.ddswrapper.configuration.Writer writerXML;

    Writer(final DomainParticipant domainParticipant, final Publisher publisher, final com.rider.ddswrapper.configuration.Writer writerXML, final Logger defaultLogger) {
        if (writerXML.getLoggerName() == null) {
            logger = defaultLogger;
        } else {
            logger = Logger.getLogger(writerXML.getLoggerName());
        }

        ddsWriter = null;
        infiniteWait = new Duration_t(Duration_t.DURATION_INFINITE_SEC, Duration_t.DURATION_INFINITE_NSEC);
        this.domainParticipant = domainParticipant;
        this.publisher = publisher;
        this.writerXML = writerXML;

        try {
            domainParticipant.registerType(writerXML.getTypeName());

            logger.info("Registered type (DomainParticipant=\"" + domainParticipant.getName() + "\",Publisher=\"" + publisher.getName() + "\",Writer=\"" + writerXML.getWriterName()+ "\",Type=\"" + writerXML.getTypeName() + "\")");

            Topic topic = domainParticipant.getDDSDomainParticipant().find_topic(writerXML.getTopicName(), new Duration_t(0, 0));

            if (topic == null) {
                topic = domainParticipant.getDDSDomainParticipant().create_topic_with_profile(writerXML.getTopicName(), writerXML.getTypeName(), writerXML.getQoSLibrary(), writerXML.getQoSProfile(), null, StatusKind.STATUS_MASK_NONE);

                if (topic == null) {
                    logger.error("Error creating Topic (DomainParticipant=\"" + domainParticipant.getName() + "\",Publisher=\"" + publisher.getName() + "\",Writer=\"" + writerXML.getWriterName() + "\",Topic=\"" + writerXML.getTopicName() + "\")");
                } else {
                    logger.info("Created Topic (DomainParticipant=\"" + domainParticipant.getName() + "\",Publisher=\"" + publisher.getName() + "\",Writer=\"" + writerXML.getWriterName() + "\",Topic=\"" + writerXML.getTopicName() + "\")");
                }
            } else {
                logger.info("Reusing Topic (DomainParticipant=\"" + domainParticipant.getName() + "\",Publisher=\"" + publisher.getName() + "\",Writer=\"" + writerXML.getWriterName() + "\",Topic=\"" + writerXML.getTopicName() + "\")");
            }

            if (topic != null) {
                ddsWriter = publisher.getDDSPublisher().create_datawriter_with_profile(topic, writerXML.getQoSLibrary(), writerXML.getQoSProfile(), null, StatusKind.STATUS_MASK_NONE);

                if (ddsWriter == null) {
                    logger.error("Error creating Writer (DomainParticipant=\"" + domainParticipant.getName() + "\",Publisher=\"" + publisher.getName() + "\",Writer=\"" + writerXML.getWriterName() + "\",QoSLibrary=\"" + writerXML.getQoSLibrary() + "\",QoSProfile=\"" + writerXML.getQoSProfile() + "\")");
                } else {
                    logger.info("Created Writer (DomainParticipant=\"" + domainParticipant.getName() + "\",Publisher=\"" + publisher.getName() + "\",Writer=\"" + writerXML.getWriterName() + "\",QoSLibrary=\"" + writerXML.getQoSLibrary() + "\",QoSProfile=\"" + writerXML.getQoSProfile() + "\")");
                }
            }
        } catch (final ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            logger.error("Error registering type (DomainParticipant=\"" + domainParticipant.getName() + "\",Publisher=\"" + publisher.getName() + "\",Writer=\"" + writerXML.getWriterName() + "\",Type=\"" + writerXML.getTypeName() + "\") : " + exception.toString());
        }
    }

    public boolean write(final Object data, final boolean blocking) {
        boolean returnVal = true;

        try {
            ddsWriter.write_untyped(data, InstanceHandle_t.HANDLE_NIL);

            if (blocking) {
                ddsWriter.wait_for_asynchronous_publishing(infiniteWait);
            }

            if (logger.isTraceEnabled()) {
                logger.trace("Wrote data (DomainParticipant=\"" + domainParticipant.getName() + "\",Publisher=\"" + publisher.getName() + "\",Writer=\"" + getName() + "\",QoSLibrary=\"" + writerXML.getQoSLibrary() + "\",QoSProfile=\"" + writerXML.getQoSProfile() + "\",Topic=\"" + writerXML.getTopicName() + "\",Type=\"" + writerXML.getTypeName() + "\")");
            }
        } catch (final RETCODE_ERROR exception) {
            logger.error("Error writing data (DomainParticipant=\"" + domainParticipant.getName() + "\",Publisher=\"" + publisher.getName() + "\",Writer=\"" + getName() + "\",QoSLibrary=\"" + writerXML.getQoSLibrary() + "\",QoSProfile=\"" + writerXML.getQoSProfile() + "\",Topic=\"" + writerXML.getTopicName() + "\",Type=\"" + writerXML.getTypeName() + "\") : " + exception.toString());

            returnVal = false;
        }

        return returnVal;
    }

    public boolean write(final Object data) {
        return write(data, false);
    }

    public String getName() {
        return writerXML.getWriterName();
    }

    public DataWriter getDDSWriter() {
        return ddsWriter;
    }
}
