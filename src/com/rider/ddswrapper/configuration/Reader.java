package com.rider.ddswrapper.configuration;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class Reader {

    @XmlElement(required = true)
    private String ReaderName;

    @XmlElement(required = true)
    private String TypeName;

    @XmlElement(required = true)
    private String TopicName;

    @XmlElement(required = true)
    private String QoSLibrary;

    @XmlElement(required = true)
    private String QoSProfile;

    @XmlElement
    private NotificationType NotificationType;

    @XmlElement
    private String LoggerName;

    public String getLoggerName() {
        return LoggerName;
    }

    public NotificationType getNotificationType() {
        return NotificationType;
    }

    public String getQoSLibrary() {
        return QoSLibrary;
    }

    public String getQoSProfile() {
        return QoSProfile;
    }

    public String getReaderName() {
        return ReaderName;
    }

    public String getTopicName() {
        return TopicName;
    }

    public String getTypeName() {
        return TypeName;
    }
}
