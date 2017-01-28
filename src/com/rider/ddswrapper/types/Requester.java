package com.rider.ddswrapper.types;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author Ciaron Rider
 */
public class Requester {
    private final Logger logger;
    private final String name;
    private com.rti.connext.requestreply.Requester ddsRequester;

    Requester(final DomainParticipant domainParticipant,
              final com.rider.ddswrapper.configuration.Requester requesterXML,
              final Logger defaultLogger) {
        if (requesterXML.getLoggerName() == null) {
            logger = defaultLogger;
        } else {
            logger = Logger.getLogger(requesterXML.getLoggerName());
        }
        
        name = requesterXML.getLoggerName();
        
        try {
            ddsRequester = new com.rti.connext.requestreply.Requester<>(domainParticipant.getDDSDomainParticipant(), requesterXML.getServiceName(), domainParticipant.getTypeSupport(requesterXML.getRequestType()), domainParticipant.getTypeSupport(requesterXML.getReplyType()));
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Requester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            java.util.logging.Logger.getLogger(Requester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Requester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            java.util.logging.Logger.getLogger(Requester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            java.util.logging.Logger.getLogger(Requester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
