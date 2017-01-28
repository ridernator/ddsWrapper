package com.rider.ddswrapper.configuration;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlRootElement(name = "DDSSettings")
public class DDSSettings {

    @XmlElement(required = true)
    private String LoggerName;

    @XmlElement
    private int NumberOfThreads;

    @XmlElement
    private String ThreadPoolLogger;

    @XmlElement
    private List<String> QoSFile;

    @XmlElement
    private List<DomainParticipant> DomainParticipant;

    @XmlElement
    private boolean RandomiseAppId;

    public List<DomainParticipant> getDomainParticipants() {
        if (DomainParticipant == null) {
            DomainParticipant = new ArrayList<>();
        }

        return DomainParticipant;
    }

    public String getLoggerName() {
        return LoggerName;
    }

    public int getNumberOfThreads() {
        return NumberOfThreads;
    }

    public List<String> getQoSFiles() {
        if (QoSFile == null) {
            QoSFile = new ArrayList<>();
        }

        return QoSFile;
    }

    public boolean isRandomiseAppId() {
        return RandomiseAppId;
    }

    public String getThreadPoolLogger() {
        return ThreadPoolLogger;
    }
}
