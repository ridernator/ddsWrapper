package com.rider.ddswrapper.configuration;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class Writer {

    @XmlElement(required = true)
    private String WriterName;

    @XmlElement(required = true)
    private String TypeName;

    @XmlElement(required = true)
    private String TopicName;

    @XmlElement(required = true)
    private String QoSLibrary;

    @XmlElement(required = true)
    private String QoSProfile;

    @XmlElement
    private String LoggerName;

    public String getLoggerName() {
        return LoggerName;
    }

    public String getQoSLibrary() {
        return QoSLibrary;
    }

    public String getQoSProfile() {
        return QoSProfile;
    }

    public String getTopicName() {
        return TopicName;
    }

    public String getTypeName() {
        return TypeName;
    }

    public String getWriterName() {
        return WriterName;
    }
}
