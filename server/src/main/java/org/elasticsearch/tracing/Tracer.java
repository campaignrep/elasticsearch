/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.tracing;

import org.elasticsearch.common.util.concurrent.ThreadContext;

/**
 * Represents a distributed tracing system that keeps track of the start and end of various activities in the cluster.
 */
public interface Tracer {

    /**
     * Called when the {@link Traceable} activity starts.
     */
    void onTraceStarted(ThreadContext threadContext, Traceable traceable);

    /**
     * Called when the {@link Traceable} activity ends.
     */
    void onTraceStopped(Traceable traceable);

    void onTraceEvent(Traceable traceable, String eventName);

    void onTraceException(Traceable traceable, Throwable throwable);

    void setAttribute(Traceable traceable, String key, boolean value);

    void setAttribute(Traceable traceable, String key, double value);

    void setAttribute(Traceable traceable, String key, long value);

    void setAttribute(Traceable traceable, String key, String value);
}