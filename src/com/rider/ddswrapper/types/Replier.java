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
public class Replier extends Thread {
    private final Logger logger;

    private com.rti.connext.requestreply.Replier<?, ?> ddsReplier;

    private final Duration_t timeout;

    private final String replierLoggingName;

    private boolean shouldStop;

    private ReplierAction action;

    Replier(final DomainParticipant domainParticipant,
            final com.rider.ddswrapper.configuration.Replier replierXML,
            final Logger defaultLogger) {
        if (replierXML.getLoggerName() == null) {
            logger = defaultLogger;
        } else {
            logger = Logger.getLogger(replierXML.getLoggerName());
        }

        shouldStop = false;
        action = null;
        ddsReplier = null;
        timeout = new Duration_t(1, Duration_t.DURATION_ZERO_NSEC);
        replierLoggingName = "(DomainParticipant=\"" + domainParticipant.getName() + "\",Replier=\"" + replierXML.getReplierName() + "\",RequestType=\"" + replierXML.getRequestType() + "\",ReplyType=\"" + replierXML.getReplyType() + "\",ServiceName=\"" + replierXML.getServiceName() + "\")";

        try {
            ddsReplier = new com.rti.connext.requestreply.Replier(domainParticipant.getDDSDomainParticipant(), replierXML.getServiceName(), domainParticipant.getTypeSupport(replierXML.getRequestType()), domainParticipant.getTypeSupport(replierXML.getReplyType()));

            logger.info("Created Replier " + replierLoggingName);
        } catch (final ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            logger.error("Error creating Replier " + replierLoggingName + " : " + exception.toString());
        }

        start();
    }

    @Override
    public void run() {
        final Sample request = ddsReplier.createRequestSample();

        while (!shouldStop) {
            if (ddsReplier.receiveRequest(request, timeout)) {
                if (request.getInfo().valid_data) {
                    if (action == null) {
                        logger.warn("Request received but no RequestAction set up");
                    } else {
                        final WriteSample reply = ddsReplier.createReplySample();
                        reply.setData(action.process(request.getData()));
                        ddsReplier.sendReply(reply, request.getIdentity());
                    }
                } else {
                    logger.warn("Invalid data received");
                }
            }
        }
    }

    public void shutdown() {
        shouldStop = true;
    }

    public boolean setAction(final ReplierAction<?, ?> action) {
        boolean returnVal = true;

        if (this.action == null) {
            this.action = action;
        } else {
            logger.warn("Unable to set RequestAction. One is already set");
            returnVal = false;
        }

        return returnVal;
    }

}
