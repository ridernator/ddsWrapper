package com.rider.ddswrapper.types;

import com.rti.connext.infrastructure.Sample;
import com.rti.connext.infrastructure.WriteSample;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.SampleIdentity_t;
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

    private Class<?> requestType;

    private Class<?> replyType;

    Requester(final DomainParticipant domainParticipant,
              final com.rider.ddswrapper.configuration.Requester requesterXML,
              final Logger defaultLogger) {
        if (requesterXML.getLoggerName() == null) {
            logger = defaultLogger;
        } else {
            logger = Logger.getLogger(requesterXML.getLoggerName());
        }

        name = requesterXML.getLoggerName();
        ddsRequester = null;
        requestType = null;
        replyType = null;

        try {
            requestType = Class.forName(requesterXML.getRequestType());
        } catch (final ClassNotFoundException exception) {
            logger.error("Error loading Request Type (TypeName=\"" + requesterXML.getRequestType() + "\") for Requester (DomainParticipant=\"" + domainParticipant.getName() + "\",Requester=\"" + requesterXML.getRequesterName() + "\",RequestType=\"" + requesterXML.getRequestType() + "\",ReplyType=\"" + requesterXML.getReplyType() + "\",ServiceName=\"" + requesterXML.getServiceName() + "\")");
        }
        
        try {
            replyType = Class.forName(requesterXML.getReplyType());
        } catch (ClassNotFoundException ex) {
            logger.error("Error loading Reply Type (TypeName=\"" + requesterXML.getRequestType() + "\") for Requester (DomainParticipant=\"" + domainParticipant.getName() + "\",Requester=\"" + requesterXML.getRequesterName() + "\",RequestType=\"" + requesterXML.getRequestType() + "\",ReplyType=\"" + requesterXML.getReplyType() + "\",ServiceName=\"" + requesterXML.getServiceName() + "\")");
        }

        try {
            ddsRequester = new com.rti.connext.requestreply.Requester<>(domainParticipant.getDDSDomainParticipant(), requesterXML.getServiceName(), domainParticipant.getTypeSupport(requesterXML.getRequestType()), domainParticipant.getTypeSupport(requesterXML.getReplyType()));
        } catch (final ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            logger.error("Error creating Requester (DomainParticipant=\"" + domainParticipant.getName() + "\",Requester=\"" + requesterXML.getRequesterName() + "\",RequestType=\"" + requesterXML.getRequestType() + "\",ReplyType=\"" + requesterXML.getReplyType() + "\",ServiceName=\"" + requesterXML.getServiceName() + "\") : " + exception.toString());
        }
    }

//    Object request(final Object request) {
//        final WriteSample writeSample = ddsRequester.createRequestSample(request);
//        
//        ddsRequester.sendRequest(writeSample);
//        //ddsRequester.waitForReplies(1, , writeSample.getIdentity());
//    }

    public String getName() {
        return name;
    }
}
