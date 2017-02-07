package com.rider.ddswrapper.types;

/**
 *
 * @author Ciaron Rider
 * @param <Request>
 * @param <Reply>
 */
public abstract class ReplierAction<Request, Reply> {

    public abstract Reply process(final Request request);

}
