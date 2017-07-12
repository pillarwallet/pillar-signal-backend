package org.glassfish.jersey.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.FeatureContext;

import javax.annotation.Priority;

import org.glassfish.jersey.logging.LoggingFeature.Verbosity;
import org.glassfish.jersey.message.MessageUtils;

import org.glassfish.jersey.logging.LoggingInterceptor;

@ConstrainedTo(RuntimeType.SERVER)
@PreMatching
@Priority(Integer.MIN_VALUE)
@SuppressWarnings("ClassWithMultipleLoggers")
public class ToshiLoggingFilter extends LoggingInterceptor implements ContainerRequestFilter, ContainerResponseFilter {

    public ToshiLoggingFilter() {
        super(Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME),
              Level.parse(LoggingFeature.DEFAULT_LOGGER_LEVEL),
              LoggingFeature.DEFAULT_VERBOSITY,
              LoggingFeature.DEFAULT_MAX_ENTITY_SIZE);
    }

    public ToshiLoggingFilter(final String levelName) {
        super(Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME),
              Level.parse(levelName),
              LoggingFeature.DEFAULT_VERBOSITY,
              LoggingFeature.DEFAULT_MAX_ENTITY_SIZE);
    }

    public ToshiLoggingFilter(final Logger logger, final Level level, final Verbosity verbosity, final int maxEntitySize) {
        super(logger, level, verbosity, maxEntitySize);
    }

    @Override
    public void filter(final ContainerRequestContext context) throws IOException {
        if (!logger.isLoggable(level)) {
            return;
        }
    }

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
            throws IOException {
        if (!logger.isLoggable(level)) {
            return;
        }

        if (responseContext.getStatus() >= 400) {

            try {

                final long id = _id.incrementAndGet();
                requestContext.setProperty(LOGGING_ID_PROPERTY, id);

                final StringBuilder b = new StringBuilder();

                printRequestLine(b, "Server responding to request with error", id, requestContext.getMethod(), requestContext.getUriInfo().getRequestUri());
                printPrefixedHeaders(b, id, REQUEST_PREFIX, requestContext.getHeaders());

                if (requestContext.hasEntity() && printEntity(verbosity, requestContext.getMediaType())) {
                    logInboundEntity(b, requestContext.getEntityStream(), MessageUtils.getCharset(requestContext.getMediaType()));
                }

                b.append("\n");

                printPrefixedHeaders(b, id, RESPONSE_PREFIX, responseContext.getStringHeaders());

                if (responseContext.hasEntity() && printEntity(verbosity, responseContext.getMediaType())) {
                    final OutputStream stream = new LoggingStream(b, responseContext.getEntityStream());
                    responseContext.setEntityStream(stream);
                    requestContext.setProperty(ENTITY_LOGGER_PROPERTY, stream);
                    // not calling log(b) here - it will be called by the interceptor
                } else {
                    log(b);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
