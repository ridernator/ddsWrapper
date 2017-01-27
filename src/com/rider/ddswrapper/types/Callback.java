/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rider.ddswrapper.types;

/**
 *
 * @author ridernator
 */
public enum Callback {
    ALL,
    DEADLINE_MISSED,
    INCOMPATIBLE_QOS,
    SAMPLE_REJECTED,
    LIVELINESS_CHANGED,
    SAMPLE_LOST,
    SUBSCRIPTION_MATCHED
}
