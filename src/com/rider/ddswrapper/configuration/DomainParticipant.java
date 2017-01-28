package com.rider.ddswrapper.configuration;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class DomainParticipant {

    @XmlElement(required = true)
    private String DomainParticipantName;

    @XmlElement
    private int DomainId;

    @XmlElement(required = true)
    private String QoSLibrary;

    @XmlElement(required = true)
    private String QoSProfile;

    @XmlElement
    private String LoggerName;

    @XmlElement
    private List<Publisher> Publisher;

    @XmlElement
    private List<Subscriber> Subscriber;

    @XmlElement
    private List<Requester> Requester;

    @XmlElement
    private List<Replier> Replier;

    public int getDomainId() {
        return DomainId;
    }

    public String getDomainParticipantName() {
        return DomainParticipantName;
    }

    public String getLoggerName() {
        return LoggerName;
    }

    public List<Publisher> getPublishers() {
        if (Publisher == null) {
            Publisher = new ArrayList<>();
        }

        return Publisher;
    }

    public String getQoSLibrary() {
        return QoSLibrary;
    }

    public String getQoSProfile() {
        return QoSProfile;
    }

    public List<Replier> getRepliers() {
        if (Replier == null) {
            Replier = new ArrayList<>();
        }

        return Replier;
    }

    public List<Requester> getRequesters() {
        if (Requester == null) {
            Requester = new ArrayList<>();
        }

        return Requester;
    }

    public List<Subscriber> getSubscribers() {
        if (Subscriber == null) {
            Subscriber = new ArrayList<>();
        }

        return Subscriber;
    }

}
