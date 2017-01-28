package com.rider.ddswrapper.configuration;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class Subscriber {

    @XmlElement(required = true)
    private String SubscriberName;

    @XmlElement(required = true)
    private String QoSLibrary;

    @XmlElement(required = true)
    private String QoSProfile;

    @XmlElement
    private List<String> PartitionName;

    @XmlElement
    private String LoggerName;

    @XmlElement
    private List<Reader> Reader;

    public String getLoggerName() {
        return LoggerName;
    }

    public List<String> getPartitionNames() {
        if (PartitionName == null) {
            PartitionName = new ArrayList<>();
        }

        return PartitionName;
    }

    public String getQoSLibrary() {
        return QoSLibrary;
    }

    public String getQoSProfile() {
        return QoSProfile;
    }

    public List<Reader> getReaders() {
        if (Reader == null) {
            Reader = new ArrayList<>();
        }

        return Reader;
    }

    public String getSubscriberName() {
        return SubscriberName;
    }
}
