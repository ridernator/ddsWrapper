package com.rider.ddswrapper.configuration;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class Replier {

    @XmlElement(required = true)
    private String ReplierName;

    @XmlElement
    private String LoggerName;

    @XmlElement(required = true)
    private String RequestType;

    @XmlElement(required = true)
    private String ReplyType;

    @XmlElement(required = true)
    private String ServiceName;

    public String getLoggerName() {
        return LoggerName;
    }

    public String getReplierName() {
        return ReplierName;
    }

    public String getReplyType() {
        return ReplyType;
    }

    public String getRequestType() {
        return RequestType;
    }

    public String getServiceName() {
        return ServiceName;
    }
}
