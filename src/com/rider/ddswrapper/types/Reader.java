/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rider.ddswrapper.types;

import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.StatusCondition;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.topic.Topic;
import com.rider.ddswrapper.configuration.NotificationType;
import java.lang.reflect.InvocationTargetException;
import org.apache.log4j.Logger;

/**
 *
 * @author ridernator
 */
public class Reader {

    private DataReader ddsReader;
    private final Logger logger;
    private final DomainParticipant domainParticipant;
    private final Subscriber subscriber;
    private final com.rider.ddswrapper.configuration.Reader readerXML;
    private ReaderListener readerListener;
    private WaitSet ddsWaitset;
    private final NotificationType notificationType;

    Reader(final DomainParticipant domainParticipant, final Subscriber subscriber, final com.rider.ddswrapper.configuration.Reader readerXML, final Logger defaultLogger) {
        if (readerXML.getLoggerName() == null) {
            logger = defaultLogger;
        } else {
            logger = Logger.getLogger(readerXML.getLoggerName());
        }

        ddsReader = null;
        readerListener = null;
        this.domainParticipant = domainParticipant;
        this.subscriber = subscriber;
        this.readerXML = readerXML;

        if (readerXML.getNotificationType() == null) {
            notificationType = NotificationType.LISTENER;
        } else {
            notificationType = readerXML.getNotificationType();
        }

        try {
            domainParticipant.registerType(readerXML.getTypeName());

            logger.info("Registered type (DomainParticipant=\"" + domainParticipant.getName() + "\",Subscriber=\"" + subscriber.getName() + "\",Reader=\"" + readerXML.getReaderName() + "\",Type=\"" + readerXML.getTypeName() + "\")");

            Topic topic = domainParticipant.getDDSDomainParticipant().find_topic(readerXML.getTopicName(), new Duration_t(0, 0));

            if (topic == null) {
                topic = domainParticipant.getDDSDomainParticipant().create_topic_with_profile(readerXML.getTopicName(), readerXML.getTypeName(), readerXML.getQoSLibrary(), readerXML.getQoSProfile(), null, StatusKind.STATUS_MASK_NONE);

                if (topic == null) {
                    logger.error("Error creating Topic (DomainParticipant=\"" + domainParticipant.getName() + "\",Subscriber=\"" + subscriber.getName() + "\",Reader=\"" + readerXML.getReaderName() + "\",Topic=\"" + readerXML.getTopicName() + "\")");
                } else {
                    logger.info("Created Topic (DomainParticipant=\"" + domainParticipant.getName() + "\",Subscriber=\"" + subscriber.getName() + "\",Reader=\"" + readerXML.getReaderName() + "\",Topic=\"" + readerXML.getTopicName() + "\")");
                }
            } else {
                logger.info("Reusing Topic (DomainParticipant=\"" + domainParticipant.getName() + "\",Subscriber=\"" + subscriber.getName() + "\",Reader=\"" + readerXML.getReaderName() + "\",Topic=\"" + readerXML.getTopicName() + "\")");
            }

            if (topic != null) {
                ddsReader = subscriber.getDDSSubscriber().create_datareader_with_profile(topic, readerXML.getQoSLibrary(), readerXML.getQoSProfile(), null, StatusKind.STATUS_MASK_NONE);

                if (ddsReader == null) {
                    logger.error("Error creating Reader (DomainParticipant=\"" + domainParticipant.getName() + "\",Subscriber=\"" + subscriber.getName() + "\",Reader=\"" + readerXML.getReaderName() + "\",QoSLibrary=\"" + readerXML.getQoSLibrary() + "\",QoSProfile=\"" + readerXML.getQoSProfile() + "\")");
                } else {
                    logger.info("Created Reader (DomainParticipant=\"" + domainParticipant.getName() + "\",Subscriber=\"" + subscriber.getName() + "\",Reader=\"" + readerXML.getReaderName() + "\",QoSLibrary=\"" + readerXML.getQoSLibrary() + "\",QoSProfile=\"" + readerXML.getQoSProfile() + "\")");


                    readerListener = new ReaderListener(Class.forName(readerXML.getTypeName()), logger);

                    switch (notificationType) {
                        case LISTENER: {
                            ddsReader.set_listener(readerListener, StatusKind.STATUS_MASK_ALL);

                            break;
                        }

                        case WAITSET: {
                            ddsWaitset = new WaitSet();
                            final StatusCondition statusCondition = ddsReader.get_statuscondition();
                            statusCondition.set_enabled_statuses(StatusKind.STATUS_MASK_ALL);
                            ddsWaitset.attach_condition(statusCondition);

                            final ReaderWaitset readerWaitset = new ReaderWaitset(readerListener, ddsWaitset, ddsReader);
                            readerWaitset.setName("DDSWrapper Waitset Thread (DomainParticipant=\"" + domainParticipant.getName() + "\",Subscriber=\"" + subscriber.getName() + "\",Reader=\"" + readerXML.getReaderName()+"\")");

                            break;
                        }

                        default: {
                            logger.error("Don't know how to handle NotificationType\"" + notificationType + "\" in Reader (DomainParticipant=\"" + domainParticipant.getName() + "\",Subscriber=\"" + subscriber.getName() + "\",Reader=\"" + readerXML.getReaderName() + "\",QoSLibrary=\"" + readerXML.getQoSLibrary() + "\",QoSProfile=\"" + readerXML.getQoSProfile() + "\")");

                            break;
                        }
                    }

                }
            }
        } catch (final ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            logger.error("Error registering type (DomainParticipant=\"" + domainParticipant.getName() + "\",Subscriber=\"" + subscriber.getName() + "\",Reader=\"" + readerXML.getReaderName() + "\",Type=\"" + readerXML.getTypeName() + "\") : " + exception.toString());
        }
    }

    public String getName() {
        return readerXML.getReaderName();
    }

    public DataReader getDDSReader() {
        return ddsReader;
    }

    public boolean addListener(final Listener listener) {
        boolean returnVal = true;

        if (readerListener == null) {
            logger.warn("Cannot add Listener to Reader (DomainParticipant=\"" + domainParticipant.getName() + "\",Subscriber=\"" + subscriber.getName() + "\",Reader=\"" + readerXML.getReaderName() + "\",QoSLibrary=\"" + readerXML.getQoSLibrary() + "\",QoSProfile=\"" + readerXML.getQoSProfile() + "\") as it is not configured");
        } else {
            readerListener.addListener(listener);

            if (logger.isTraceEnabled()) {
                logger.trace("Added Listener to Reader (DomainParticipant=\"" + domainParticipant.getName() + "\",Subscriber=\"" + subscriber.getName() + "\",Reader=\"" + readerXML.getReaderName() + "\",QoSLibrary=\"" + readerXML.getQoSLibrary() + "\",QoSProfile=\"" + readerXML.getQoSProfile() + "\")");
            }
        }

        return returnVal;
    }

    public boolean removeListener(final Listener listener) {
        boolean returnVal = true;

        if (readerListener == null) {
            logger.warn("Cannot remove Listener to Reader (DomainParticipant=\"" + domainParticipant.getName() + "\",Subscriber=\"" + subscriber.getName() + "\",Reader=\"" + readerXML.getReaderName() + "\",QoSLibrary=\"" + readerXML.getQoSLibrary() + "\",QoSProfile=\"" + readerXML.getQoSProfile() + "\") as it is not configured");
        } else {
            readerListener.removeListener(listener);

            if (logger.isTraceEnabled()) {
                logger.trace("Removed Listener to Reader (DomainParticipant=\"" + domainParticipant.getName() + "\",Subscriber=\"" + subscriber.getName() + "\",Reader=\"" + readerXML.getReaderName() + "\",QoSLibrary=\"" + readerXML.getQoSLibrary() + "\",QoSProfile=\"" + readerXML.getQoSProfile() + "\")");
            }
        }

        return returnVal;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }   
}
