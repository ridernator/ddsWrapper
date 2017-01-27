/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rider.ddswrapper.types;

import com.rti.dds.infrastructure.RETCODE_ERROR;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.subscription.LivelinessChangedStatus;
import com.rti.dds.subscription.RequestedDeadlineMissedStatus;
import com.rti.dds.subscription.RequestedIncompatibleQosStatus;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;
import com.rti.dds.subscription.SubscriptionMatchedStatus;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author ridernator
 */
public class ReaderListener implements DataReaderListener {

    private final ArrayList<Listener> listeners;
    private final Logger logger;
    private final Class type;

    ReaderListener(final Class type, final Logger logger) {
        listeners = new ArrayList<>();
        this.type = type;
        this.logger = logger;
    }

    public void addListener(final Listener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeListener(final Listener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    @Override
    public void on_requested_deadline_missed(final DataReader reader, final RequestedDeadlineMissedStatus requestedDeadlineMissedStatus) {
        synchronized (listeners) {
            for (final Listener listener : listeners) {
                if (listener.isListeningForDeadlineMissed()) {
                    ThreadPool.addTask(new Runnable() {
                        @Override
                        public void run() {
                            listener.deadLineMissed(logger, reader, requestedDeadlineMissedStatus);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void on_requested_incompatible_qos(final DataReader reader, final RequestedIncompatibleQosStatus requestedIncompatibleQosStatus) {
        synchronized (listeners) {
            for (final Listener listener : listeners) {
                if (listener.isListeningForDeadlineMissed()) {
                    ThreadPool.addTask(new Runnable() {
                        @Override
                        public void run() {
                            listener.incompatibleQoS(logger, reader, requestedIncompatibleQosStatus);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void on_sample_rejected(final DataReader reader, final SampleRejectedStatus sampleRejectedStatus) {
        synchronized (listeners) {
            for (final Listener listener : listeners) {
                if (listener.isListeningForDeadlineMissed()) {
                    ThreadPool.addTask(new Runnable() {
                        @Override
                        public void run() {
                            listener.sampleRejected(logger, reader, sampleRejectedStatus);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void on_liveliness_changed(final DataReader reader, final LivelinessChangedStatus livelinessChangedStatus) {
        synchronized (listeners) {
            for (final Listener listener : listeners) {
                if (listener.isListeningForDeadlineMissed()) {
                    ThreadPool.addTask(new Runnable() {
                        @Override
                        public void run() {
                            listener.livelinessChanged(logger, reader, livelinessChangedStatus);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void on_data_available(final DataReader reader) {
        boolean dataAvailable = true;

        while (dataAvailable) {
            try {
                final Object sample = type.newInstance();
                final SampleInfo sampleInfo = new SampleInfo();
                reader.take_next_sample_untyped(sample, sampleInfo);

                if (sampleInfo.valid_data) {
                    synchronized (listeners) {
                        for (final Listener listener : listeners) {
                            ThreadPool.addTask(new Runnable() {
                                @Override
                                public void run() {
                                    listener.newData(logger, sample);
                                }
                            });
                        }
                    }
                } else {
                    logger.warn("Received invalid data");
                }
            } catch (final RETCODE_NO_DATA exception) {
                dataAvailable = false;
            } catch (final RETCODE_ERROR | InstantiationException | IllegalAccessException exception) {
                dataAvailable = false;

                logger.warn("Could not read data : " + exception.toString());
            }
        }
    }

    @Override
    public void on_sample_lost(final DataReader reader, final SampleLostStatus sampleLostStatus) {
        synchronized (listeners) {
            for (final Listener listener : listeners) {
                if (listener.isListeningForDeadlineMissed()) {
                    ThreadPool.addTask(new Runnable() {
                        @Override
                        public void run() {
                            listener.sampleLost(logger, reader, sampleLostStatus);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void on_subscription_matched(final DataReader reader, final SubscriptionMatchedStatus subscriptionMatchedStatus) {
        synchronized (listeners) {
            for (final Listener listener : listeners) {
                if (listener.isListeningForDeadlineMissed()) {
                    ThreadPool.addTask(new Runnable() {
                        @Override
                        public void run() {
                            listener.subscriptionMatched(logger, reader, subscriptionMatchedStatus);
                        }
                    });
                }
            }
        }
    }
}
