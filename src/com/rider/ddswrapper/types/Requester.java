package com.rider.ddswrapper.types;

import com.rti.connext.infrastructure.Sample;
import com.rti.connext.infrastructure.WriteSample;
import com.rti.dds.infrastructure.Duration_t;
import java.lang.reflect.InvocationTargetException;
import org.apache.log4j.Logger;

/**
 *
 * @author Ciaron Rider
 */
public class Requester {
    private static final Duration_t DEFAULT_TIMEOUT = Duration_t.from_seconds(1);

    private final Logger logger;
    
    private com.rti.connext.requestreply.Requester<?, ?> ddsRequester;
    
    private final Duration_t timeout;
    
    private final String requesterLoggingName;
    
    Requester(final DomainParticipant domainParticipant,
              final com.rider.ddswrapper.configuration.Requester requesterXML,
              final Logger defaultLogger) {
        if (requesterXML.getLoggerName() == null) {
            logger = defaultLogger;
        } else {
            logger = Logger.getLogger(requesterXML.getLoggerName());
        }
        
        if (requesterXML.getTimeOut() == null) {
            timeout = DEFAULT_TIMEOUT;
        } else {
            timeout = Duration_t.from_millis(requesterXML.getTimeOut());
        }
        
        ddsRequester = null;
        requesterLoggingName = "(DomainParticipant=\"" + domainParticipant.getName() + "\",Requester=\"" + requesterXML.getRequesterName() + "\",RequestType=\"" + requesterXML.getRequestType() + "\",ReplyType=\"" + requesterXML.getReplyType() + "\",ServiceName=\"" + requesterXML.getServiceName() + "\")";
        
        try {
            ddsRequester = new com.rti.connext.requestreply.Requester<>(domainParticipant.getDDSDomainParticipant(), requesterXML.getServiceName(), domainParticipant.getTypeSupport(requesterXML.getRequestType()), domainParticipant.getTypeSupport(requesterXML.getReplyType()));
            
            logger.info("Created Requester " + requesterLoggingName);
        } catch (final ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            logger.error("Error creating Requester " + requesterLoggingName + " : " + exception.toString());
        }
    }
    
    public Object request(final Object request) {
        Object reply = null;
        
        final Sample replySample = ddsRequester.createReplySample();
        final WriteSample writeSample = ddsRequester.createRequestSample();
        writeSample.setData(request);
        
        ddsRequester.sendRequest(writeSample);
        
        if (ddsRequester.receiveReply(replySample, timeout)) {
            if (replySample.getInfo().valid_data) {
                reply = replySample.getData();
            } else {
                logger.warn("Invalid data received");
            }
        } else {
            logger.warn("Timeout waiting for reply in Requester " + requesterLoggingName);
        }
        
        return reply;
    }
}
