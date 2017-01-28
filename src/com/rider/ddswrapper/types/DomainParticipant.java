/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rider.ddswrapper.types;

import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.topic.TypeSupportImpl;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author ridernator
 */
public class DomainParticipant {

    private final com.rti.dds.domain.DomainParticipant ddsDomainParticipant;

    private final Logger logger;

    private final HashMap<String, Publisher> publishers;

    private final HashMap<String, Subscriber> subscribers;

    private final HashMap<String, Requester> requesters;

    private final HashMap<String, Replier> repliers;

    private final String name;

    public DomainParticipant(
            final com.rider.ddswrapper.configuration.DomainParticipant domainParticipantXML,
            final Logger defaultLogger,
            final boolean randomiseAppId) {
        if (domainParticipantXML.getLoggerName() == null) {
            logger = defaultLogger;
        } else {
            logger = Logger.getLogger(domainParticipantXML.getLoggerName());
        }

        name = domainParticipantXML.getDomainParticipantName();
        publishers = new HashMap<>();
        subscribers = new HashMap<>();
        requesters = new HashMap<>();
        repliers = new HashMap<>();

        DomainParticipantQos dpQos = new DomainParticipantQos();
        DomainParticipantFactory.get_instance().get_participant_qos_from_profile(dpQos, domainParticipantXML.getQoSLibrary(), domainParticipantXML.getQoSProfile());

        if (randomiseAppId) {
            logger.info("Generating randomised appId for DomainParticipant (DomainParticipant=\"" + domainParticipantXML.getDomainParticipantName() + ")");
            dpQos.wire_protocol.rtps_app_id = (int) (Math.random() * Integer.MAX_VALUE);
        }

        ddsDomainParticipant = DomainParticipantFactory.get_instance().create_participant(domainParticipantXML.getDomainId(), dpQos, null, StatusKind.STATUS_MASK_NONE);

        if (ddsDomainParticipant == null) {
            logger.error("Error creating DomainParticipant (DomainParticipant=\"" + domainParticipantXML.getDomainParticipantName() + "\",QoSLibrary=\"" + domainParticipantXML.getQoSLibrary() + "\",QoSProfile=\"" + domainParticipantXML.getQoSProfile() + "\")");
        } else {
            logger.info("Created DomainParticipant (DomainParticipant=\"" + domainParticipantXML.getDomainParticipantName() + "\",QoSLibrary=\"" + domainParticipantXML.getQoSLibrary() + "\",QoSProfile=\"" + domainParticipantXML.getQoSProfile() + "\")");

            domainParticipantXML.getPublishers().stream().forEach(publisherXML -> {
                if (publishers.containsKey(publisherXML.getPublisherName())) {
                    logger.warn("DomainParticipant (DomainParticipant=\"" + domainParticipantXML.getDomainParticipantName() + "\") contains multiple Publishers called \"" + publisherXML.getPublisherName() + "\". Using only the first one");
                } else {
                    publishers.put(publisherXML.getPublisherName(), new Publisher(this, publisherXML, logger));
                }
            });

            domainParticipantXML.getSubscribers().stream().forEach(subscriberXML -> {
                if (subscribers.containsKey(subscriberXML.getSubscriberName())) {
                    logger.warn("DomainParticipant (DomainParticipant=\"" + domainParticipantXML.getDomainParticipantName() + "\") contains multiple Subscribers called \"" + subscriberXML.getSubscriberName() + "\". Using only the first one");
                } else {
                    subscribers.put(subscriberXML.getSubscriberName(), new Subscriber(this, subscriberXML, logger));
                }
            });

            domainParticipantXML.getRequesters().stream().forEach(requesterXML -> {
                if (requesters.containsKey(requesterXML.getRequesterName())) {
                    logger.warn("DomainParticipant (DomainParticipant=\"" + domainParticipantXML.getDomainParticipantName() + "\") contains multiple Requesters called \"" + requesterXML.getRequesterName() + "\". Using only the first one");
                } else {
                    requesters.put(requesterXML.getRequesterName(), new Requester(this, requesterXML, logger));
                }
            });

            domainParticipantXML.getRepliers().stream().forEach(replierXML -> {
                if (repliers.containsKey(replierXML.getReplierName())) {
                    logger.warn("DomainParticipant (DomainParticipant=\"" + domainParticipantXML.getDomainParticipantName() + "\") contains multiple Repliers called \"" + replierXML.getReplierName() + "\". Using only the first one");
                } else {
                    repliers.put(replierXML.getReplierName(), new Replier(this, replierXML, logger));
                }
            });
        }
    }

    public void registerType(final String typeName) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final Class<?> clazz = Class.forName(typeName + "TypeSupport");
        final Method method = clazz.getDeclaredMethod("register_type", com.rti.dds.domain.DomainParticipant.class, String.class);

        method.invoke(null, ddsDomainParticipant, typeName);
    }

    public TypeSupportImpl getTypeSupport(final String typeName) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final Class<?> clazz = Class.forName(typeName + "TypeSupport");
        final Method method = clazz.getDeclaredMethod("get_instance");

        return (TypeSupportImpl) method.invoke(null);
    }

    public Publisher getPublisher(final String publisherName) {
        return publishers.get(publisherName);
    }

    public Subscriber getSubscriber(final String subscriberName) {
        return subscribers.get(subscriberName);
    }

    public Requester getRequester(final String requesterName) {
        return requesters.get(requesterName);
    }

    public Replier getReplier(final String replierName) {
        return repliers.get(replierName);
    }

    public com.rti.dds.domain.DomainParticipant getDDSDomainParticipant() {
        return ddsDomainParticipant;
    }

    public String getName() {
        return name;
    }

}
