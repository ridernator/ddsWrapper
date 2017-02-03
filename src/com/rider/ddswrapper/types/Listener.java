/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rider.ddswrapper.types;

import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.LivelinessChangedStatus;
import com.rti.dds.subscription.RequestedDeadlineMissedStatus;
import com.rti.dds.subscription.RequestedIncompatibleQosStatus;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;
import com.rti.dds.subscription.SubscriptionMatchedStatus;
import org.apache.log4j.Logger;

/**
 *
 * @author ridernator
 */
public abstract class Listener {

    private boolean listeningForDeadlineMissed;

    private boolean listeningForIncompatibleStatus;

    private boolean listeningForLivelinessChanged;

    private boolean listeningForSampleLost;

    private boolean listeningForSampleRejected;

    private boolean listeningForSubscriptionMatched;

    public Listener(final ReaderCallback... callbacks) {
        listeningForDeadlineMissed = false;
        listeningForIncompatibleStatus = false;
        listeningForLivelinessChanged = false;
        listeningForSampleLost = false;
        listeningForSampleRejected = false;
        listeningForSubscriptionMatched = false;

        for (final ReaderCallback callback : callbacks) {
            switch (callback) {
                case DEADLINE_MISSED: {
                    listeningForDeadlineMissed = true;

                    break;
                }

                case INCOMPATIBLE_QOS: {
                    listeningForIncompatibleStatus = true;

                    break;
                }

                case LIVELINESS_CHANGED: {
                    listeningForLivelinessChanged = true;

                    break;
                }

                case SAMPLE_LOST: {
                    listeningForSampleLost = true;

                    break;
                }

                case SAMPLE_REJECTED: {
                    listeningForSampleRejected = true;

                    break;
                }

                case SUBSCRIPTION_MATCHED: {
                    listeningForSubscriptionMatched = true;

                    break;
                }

                case ALL: {
                    listeningForDeadlineMissed = true;
                    listeningForIncompatibleStatus = true;
                    listeningForLivelinessChanged = true;
                    listeningForSampleLost = true;
                    listeningForSampleRejected = true;
                    listeningForSubscriptionMatched = true;

                    break;
                }
            }
        }
    }

    public boolean isListeningForDeadlineMissed() {
        return listeningForDeadlineMissed;
    }

    public boolean isListeningForIncompatibleStatus() {
        return listeningForIncompatibleStatus;
    }

    public boolean isListeningForLivelinessChanged() {
        return listeningForLivelinessChanged;
    }

    public boolean isListeningForSampleLost() {
        return listeningForSampleLost;
    }

    public boolean isListeningForSampleRejected() {
        return listeningForSampleRejected;
    }

    public boolean isListeningForSubscriptionMatched() {
        return listeningForSubscriptionMatched;
    }

    public void setListeningForDeadlineMissed(
            final boolean listeningForDeadlineMissed) {
        this.listeningForDeadlineMissed = listeningForDeadlineMissed;
    }

    public void setListeningForIncompatibleStatus(
            final boolean listeningForIncompatibleStatus) {
        this.listeningForIncompatibleStatus = listeningForIncompatibleStatus;
    }

    public void setListeningForLivelinessChanged(
            final boolean listeningForLivelinessChanged) {
        this.listeningForLivelinessChanged = listeningForLivelinessChanged;
    }

    public void setListeningForSampleLost(final boolean listeningForSampleLost) {
        this.listeningForSampleLost = listeningForSampleLost;
    }

    public void setListeningForSampleRejected(
            final boolean listeningForSampleRejected) {
        this.listeningForSampleRejected = listeningForSampleRejected;
    }

    public void setListeningForSubscriptionMatched(
            final boolean listeningForSubscriptionMatched) {
        this.listeningForSubscriptionMatched = listeningForSubscriptionMatched;
    }

    public void deadLineMissed(final Logger logger,
                               final DataReader reader,
                               final RequestedDeadlineMissedStatus requestedDeadlineMissedStatus) {
        logger.warn("Missed " + requestedDeadlineMissedStatus.total_count_change + " deadlines. (" + requestedDeadlineMissedStatus.total_count + " total)");
    }

    public void subscriptionMatched(final Logger logger,
                                    final DataReader reader,
                                    final SubscriptionMatchedStatus subscriptionMatchedStatus) {
        logger.info("Number of matched subscriptions changed by " + subscriptionMatchedStatus.current_count_change + ". (" + subscriptionMatchedStatus.current_count + " total)");
    }

    public void sampleLost(final Logger logger,
                           final DataReader reader,
                           final SampleLostStatus sampleLostStatus) {
        logger.warn(sampleLostStatus.total_count_change + " samples lost. (" + sampleLostStatus.total_count + " total)");
    }

    public void livelinessChanged(final Logger logger,
                                  final DataReader reader,
                                  final LivelinessChangedStatus livelinessChangedStatus) {
        logger.info("Liveliness Changed (NumAlive changed by " + livelinessChangedStatus.alive_count_change + ". (" + livelinessChangedStatus.alive_count + " total)) (NotNumAlive changed by " + livelinessChangedStatus.not_alive_count_change + ". (" + livelinessChangedStatus.not_alive_count + " total))");
    }

    public void sampleRejected(final Logger logger,
                               final DataReader reader,
                               final SampleRejectedStatus sampleRejectedStatus) {
        logger.warn(sampleRejectedStatus.total_count_change + " samples rejected. (" + sampleRejectedStatus.total_count + " total)");
    }

    public void incompatibleQoS(final Logger logger,
                                final DataReader reader,
                                final RequestedIncompatibleQosStatus requestedIncompatibleQosStatus) {
        logger.warn(requestedIncompatibleQosStatus.total_count_change + " incompatible statuses. (" + requestedIncompatibleQosStatus.total_count + " total)");
    }

    public abstract void newData(final Logger logger,
                                 final Object sample);
}
