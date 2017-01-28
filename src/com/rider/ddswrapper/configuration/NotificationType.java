package com.rider.ddswrapper.configuration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlEnum
public enum NotificationType {

    @XmlEnumValue("Listener")
    LISTENER,
    @XmlEnumValue("Waitset")
    WAITSET;

}
