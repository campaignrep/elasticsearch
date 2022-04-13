/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.elasticsearch.common.util.concurrent.ThreadContext;
import org.elasticsearch.tracing.Traceable;
import org.elasticsearch.tracing.Tracer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AuthorizationTracer {

    private static final Logger logger = LogManager.getLogger(AuthorizationTracer.class);

    private final ThreadContext threadContext;
    private final List<Tracer> tracers = new CopyOnWriteArrayList<>();

    public AuthorizationTracer(ThreadContext threadContext) {
        this.threadContext = threadContext;
    }

    public void addTracer(Tracer tracer) {
        if (tracer != null) {
            tracers.add(tracer);
        }
    }

    public Runnable startTracing(Traceable traceable) {
        for (Tracer tracer : tracers) {
            try {
                tracer.onTraceStarted(threadContext, traceable);
            } catch (Exception e) {
                assert false : e;
                logger.warn(
                    new ParameterizedMessage(
                        "authorization tracing listener [{}] failed on starting tracing of [{}][{}]",
                        tracer,
                        traceable.getSpanId(),
                        traceable.getSpanName()
                    ),
                    e
                );
            }
        }
        return () -> {
            for (Tracer tracer : tracers) {
                try {
                    tracer.onTraceStopped(traceable);
                } catch (Exception e) {
                    assert false : e;
                    logger.warn(
                        new ParameterizedMessage(
                            "authorization tracing listener [{}] failed on stopping tracing of [{}][{}]",
                            tracer,
                            traceable.getSpanId(),
                            traceable.getSpanName()
                        ),
                        e
                    );
                }
            }
        };
    }
}