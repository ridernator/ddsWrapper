/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rider.ddswrapper;

import com.rider.ddswrapper.configuration.DDSSettings;
import com.rider.ddswrapper.types.DomainParticipant;
import com.rider.ddswrapper.types.Publisher;
import com.rider.ddswrapper.types.Reader;
import com.rider.ddswrapper.types.Replier;
import com.rider.ddswrapper.types.Requester;
import com.rider.ddswrapper.types.Subscriber;
import com.rider.ddswrapper.types.ThreadPool;
import com.rider.ddswrapper.types.Writer;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantFactoryQos;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.log4j.Logger;

/**
 *
 * @author ridernator
 */
public class DDSWrapper {

    private boolean initComplete;

    private Logger logger;

    private final HashMap<String, DomainParticipant> domainParticipants;

    private static class SingletonHolder {
        public static final DDSWrapper instance = new DDSWrapper();

    }

    public static DDSWrapper getInstance() {
        return SingletonHolder.instance;
    }

    public static void shutdown() {
        synchronized (SingletonHolder.instance) {
            if (SingletonHolder.instance.initComplete) {
                SingletonHolder.instance.initComplete = false;

                for (final DomainParticipant domainParticipant : SingletonHolder.instance.domainParticipants.values()) {
                    domainParticipant.getDDSDomainParticipant().delete_contained_entities();
                    DomainParticipantFactory.TheParticipantFactory.delete_participant(domainParticipant.getDDSDomainParticipant());
                }

                SingletonHolder.instance.domainParticipants.clear();
            } else {
                System.err.println("Cannot destroy DDSWrapper before it is initialized");
            }
        }
    }

    private DDSWrapper() {
        initComplete = false;
        domainParticipants = new HashMap<>();
    }

    public boolean startup(final String xmlFileName) {
        boolean returnVal = true;

        synchronized (SingletonHolder.instance) {

            if (initComplete) {
                logger.warn("startup() method can only be called once before shutdown(). Ignoring second invocation.");
            } else {
                try {
                    final long startTime = System.currentTimeMillis();

                    final Unmarshaller unmarshaller = JAXBContext.newInstance(DDSSettings.class).createUnmarshaller();
                    final DDSSettings ddsSettings = (DDSSettings) unmarshaller.unmarshal(new File(xmlFileName));

                    logger = Logger.getLogger(ddsSettings.getLoggerName());
                    logger.info("\"" + xmlFileName + "\" parsed successfully");

                    String threadPoolLoggerName = ddsSettings.getThreadPoolLogger();
                    if (threadPoolLoggerName == null) {
                        threadPoolLoggerName = ddsSettings.getLoggerName();
                    }

                    ThreadPool.init(ddsSettings.getNumberOfThreads(), Logger.getLogger(threadPoolLoggerName));

                    createDomainParticipants(xmlFileName, ddsSettings);

                    initComplete = true;

                    logger.trace("startup() took " + (System.currentTimeMillis() - startTime) + "ms");
                } catch (final JAXBException exception) {
                    System.err.println("Error parsing \"" + xmlFileName + "\" : " + exception.toString());
                    returnVal = false;
                }
            }
        }

        return returnVal;
    }

    public DomainParticipant getDomainParticipant(
            final String domainParticipantName) {
        DomainParticipant returnVal = null;

        if (initComplete) {
            returnVal = domainParticipants.get(domainParticipantName);
        } else {
            logger.warn("Cannot get domain participant (\"" + domainParticipantName + "\") as startup() has not been called or is not complete");
        }

        return returnVal;
    }

    public Publisher getPublisher(final String domainParticipantName,
                                  final String publisherName) {
        Publisher returnVal = null;

        if (initComplete) {
            final DomainParticipant domainParticipant = getDomainParticipant(domainParticipantName);

            if (domainParticipant == null) {
                logger.warn("Cannot get Publisher (DomainParticipant=\"" + domainParticipantName + "\",Publisher=\"" + publisherName + "\") as Domain Participant (\"" + domainParticipantName + "\") does not exist");
            } else {
                returnVal = domainParticipant.getPublisher(publisherName);
            }
        } else {
            logger.warn("Cannot get Publisher (DomainParticipant=\"" + domainParticipantName + "\",Publisher=\"" + publisherName + "\") as startup() has not been called or is not complete");
        }

        return returnVal;
    }

    public Subscriber getSubscriber(final String domainParticipantName,
                                    final String subscriberName) {
        Subscriber returnVal = null;

        if (initComplete) {
            final DomainParticipant domainParticipant = getDomainParticipant(domainParticipantName);

            if (domainParticipant == null) {
                logger.warn("Cannot get Subscriber (DomainParticipant=\"" + domainParticipantName + "\",Subscriber=\"" + subscriberName + "\") as Domain Participant (\"" + domainParticipantName + "\") does not exist");
            } else {
                returnVal = domainParticipant.getSubscriber(subscriberName);
            }
        } else {
            logger.warn("Cannot get Subscriber (DomainParticipant=\"" + domainParticipantName + "\",Subscriber=\"" + subscriberName + "\") as startup() has not been called or is not complete");
        }

        return returnVal;
    }

    public Requester getRequester(final String domainParticipantName,
                                  final String requesterName) {
        Requester returnVal = null;

        if (initComplete) {
            final DomainParticipant domainParticipant = getDomainParticipant(domainParticipantName);

            if (domainParticipant == null) {
                logger.warn("Cannot get Requester (DomainParticipant=\"" + domainParticipantName + "\",Requester=\"" + requesterName + "\") as Domain Participant (\"" + domainParticipantName + "\") does not exist");
            } else {
                returnVal = domainParticipant.getRequester(requesterName);
            }
        } else {
            logger.warn("Cannot get Requester (DomainParticipant=\"" + domainParticipantName + "\",Requester=\"" + requesterName + "\") as startup() has not been called or is not complete");
        }

        return returnVal;
    }

    public Replier getReplier(final String domainParticipantName,
                              final String replierName) {
        Replier returnVal = null;

        if (initComplete) {
            final DomainParticipant domainParticipant = getDomainParticipant(domainParticipantName);

            if (domainParticipant == null) {
                logger.warn("Cannot get Requester (DomainParticipant=\"" + domainParticipantName + "\",Requester=\"" + replierName + "\") as Domain Participant (\"" + domainParticipantName + "\") does not exist");
            } else {
                returnVal = domainParticipant.getReplier(replierName);
            }
        } else {
            logger.warn("Cannot get Requester (DomainParticipant=\"" + domainParticipantName + "\",Requester=\"" + replierName + "\") as startup() has not been called or is not complete");
        }

        return returnVal;
    }

    public Reader getReader(final String domainParticipantName,
                            final String subscriberName,
                            final String readerName) {
        Reader returnVal = null;

        if (initComplete) {
            final Subscriber subscriber = getSubscriber(domainParticipantName, subscriberName);

            if (subscriber == null) {
                logger.warn("Cannot get Reader (DomainParticipant=\"" + domainParticipantName + "\",Subscriber=\"" + subscriberName + "\",Reader=\"" + readerName + "\") as Subscriber (\"" + domainParticipantName + "\",Subscriber=\"" + subscriberName + "\") does not exist");
            } else {
                returnVal = subscriber.getReader(readerName);
            }
        } else {
            logger.warn("Cannot get Reader (DomainParticipant=\"" + domainParticipantName + "\",Subscriber=\"" + subscriberName + "\",Reader=\"" + readerName + "\") as startup() has not been called or is not complete");
        }

        return returnVal;
    }

    public Writer getWriter(final String domainParticipantName,
                            final String publisherName,
                            final String writerName) {
        Writer returnVal = null;

        if (initComplete) {
            final Publisher publisher = getPublisher(domainParticipantName, publisherName);

            if (publisher == null) {
                logger.warn("Cannot get Writer (DomainParticipant=\"" + domainParticipantName + "\",Subscriber=\"" + publisherName + "\",Writer=\"" + writerName + "\") as Publisher (\"" + domainParticipantName + "\",Publisher=\"" + publisherName + "\") does not exist");
            } else {
                returnVal = publisher.getWriter(writerName);
            }
        } else {
            logger.warn("Cannot get Writer (DomainParticipant=\"" + domainParticipantName + "\",Publisher=\"" + publisherName + "\",Writer=\"" + writerName + "\") as startup() has not been called or is not complete");
        }

        return returnVal;
    }

    private void createDomainParticipants(final String xmlFileName,
                                          final DDSSettings ddsSettings) {
        loadQoSFiles(ddsSettings.getQoSFiles());

        ddsSettings.getDomainParticipants().stream().forEach(domainParticipantXML -> {
            if (domainParticipants.containsKey(domainParticipantXML.getDomainParticipantName())) {
                logger.warn("\"" + xmlFileName + "\" contains multiple DomainParticipants called \"" + domainParticipantXML.getDomainParticipantName() + "\". Using only the first one");
            } else {
                domainParticipants.put(domainParticipantXML.getDomainParticipantName(), new DomainParticipant(domainParticipantXML, logger, ddsSettings.isRandomiseAppId()));
            }
        });
    }

    private void loadQoSFiles(final List<String> qosFiles) {
        final DomainParticipantFactoryQos factory_qos = new DomainParticipantFactoryQos();
        DomainParticipantFactory.get_instance().get_qos(factory_qos);

        qosFiles.stream().forEach(qosFile -> factory_qos.profile.url_profile.add(qosFile));

        DomainParticipantFactory.get_instance().set_qos(factory_qos);
    }

    // TODO : createDomainParticipant()
}
