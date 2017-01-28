package com.rider.ddswrapper.configuration;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class Publisher {

    @XmlElement(required = true)
    private String PublisherName;

    @XmlElement(required = true)
    private String QoSLibrary;

    @XmlElement(required = true)
    private String QoSProfile;

    @XmlElement
    private List<String> PartitionName;

    @XmlElement
    private String LoggerName;

    @XmlElement
    private List<Writer> Writer;

    public String getLoggerName() {
        return LoggerName;
    }

    public List<String> getPartitionName() {
        if (PartitionName == null) {
            PartitionName = new ArrayList<>();
        }

        return PartitionName;
    }

    public String getPublisherName() {
        return PublisherName;
    }

    public String getQoSLibrary() {
        return QoSLibrary;
    }

    public String getQoSProfile() {
        return QoSProfile;
    }

    public List<Writer> getWriter() {
        if (Writer == null) {
            Writer = new ArrayList<>();
        }

        return Writer;
    }
}
