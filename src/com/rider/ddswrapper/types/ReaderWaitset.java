/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rider.ddswrapper.types;

import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.LivelinessChangedStatus;
import com.rti.dds.subscription.RequestedDeadlineMissedStatus;
import com.rti.dds.subscription.RequestedIncompatibleQosStatus;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;
import com.rti.dds.subscription.SubscriptionMatchedStatus;

/**
 *
 * @author ridernator
 */
public class ReaderWaitset extends Thread {

    private final ReaderListener readerListener;
    private final WaitSet waitset;
    private final DataReader reader;

    public ReaderWaitset(final ReaderListener readerListener, final WaitSet waitset, final DataReader reader) {
        this.waitset = waitset;
        this.readerListener = readerListener;
        this.reader = reader;
        
        start();
    }

    @Override
    public void run() {
        final Duration_t infiniteWait = new Duration_t(Duration_t.DURATION_INFINITE_SEC, Duration_t.DURATION_INFINITE_NSEC);

        while (true) {
            final ConditionSeq conditionSequence = new ConditionSeq();

            waitset.wait(conditionSequence, infiniteWait);

            final int mask = reader.get_status_changes();

            if ((mask & StatusKind.DATA_AVAILABLE_STATUS) == StatusKind.DATA_AVAILABLE_STATUS) {
                readerListener.on_data_available(reader);
            }

            if ((mask & StatusKind.REQUESTED_DEADLINE_MISSED_STATUS) == StatusKind.REQUESTED_DEADLINE_MISSED_STATUS) {
                final RequestedDeadlineMissedStatus requestedDeadlineMissedStatus = new RequestedDeadlineMissedStatus();
                reader.get_requested_deadline_missed_status(requestedDeadlineMissedStatus);

                readerListener.on_requested_deadline_missed(reader, requestedDeadlineMissedStatus);
            }

            if ((mask & StatusKind.REQUESTED_INCOMPATIBLE_QOS_STATUS) == StatusKind.REQUESTED_INCOMPATIBLE_QOS_STATUS) {
                final RequestedIncompatibleQosStatus requestedIncompatibleQosStatus = new RequestedIncompatibleQosStatus();
                reader.get_requested_incompatible_qos_status(requestedIncompatibleQosStatus);

                readerListener.on_requested_incompatible_qos(reader, requestedIncompatibleQosStatus);
            }

            if ((mask & StatusKind.SAMPLE_REJECTED_STATUS) == StatusKind.SAMPLE_REJECTED_STATUS) {
                final SampleRejectedStatus sampleRejectedStatus = new SampleRejectedStatus();
                reader.get_sample_rejected_status(sampleRejectedStatus);

                readerListener.on_sample_rejected(reader, sampleRejectedStatus);
            }

            if ((mask & StatusKind.LIVELINESS_CHANGED_STATUS) == StatusKind.LIVELINESS_CHANGED_STATUS) {
                final LivelinessChangedStatus livelinessChangedStatus = new LivelinessChangedStatus();
                reader.get_liveliness_changed_status(livelinessChangedStatus);

                readerListener.on_liveliness_changed(reader, livelinessChangedStatus);
            }

            if ((mask & StatusKind.SAMPLE_LOST_STATUS) == StatusKind.SAMPLE_LOST_STATUS) {
                final SampleLostStatus sampleLostStatus = new SampleLostStatus();
                reader.get_sample_lost_status(sampleLostStatus);

                readerListener.on_sample_lost(reader, sampleLostStatus);
            }

            if ((mask & StatusKind.SUBSCRIPTION_MATCHED_STATUS) == StatusKind.SUBSCRIPTION_MATCHED_STATUS) {
                final SubscriptionMatchedStatus subscriptionMatchedStatus = new SubscriptionMatchedStatus();
                reader.get_subscription_matched_status(subscriptionMatchedStatus);

                readerListener.on_subscription_matched(reader, subscriptionMatchedStatus);
            }
        }
    }
}
