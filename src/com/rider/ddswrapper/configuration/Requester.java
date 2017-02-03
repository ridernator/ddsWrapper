package com.rider.ddswrapper.configuration;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class Requester {

    @XmlElement(required = true)
    private String RequesterName;

    @XmlElement
    private String LoggerName;

    @XmlElement(required = true)
    private String RequestType;

    @XmlElement(required = true)
    private String ReplyType;

    @XmlElement(required = true)
    private String ServiceName;

    @XmlElement
    private Long TimeOut;

    public String getLoggerName() {
        return LoggerName;
    }

    public String getReplyType() {
        return ReplyType;
    }

    public String getRequestType() {
        return RequestType;
    }

    public String getRequesterName() {
        return RequesterName;
    }

    public String getServiceName() {
        return ServiceName;
    }

    public Long getTimeOut() {
        return TimeOut;
    }    
}
