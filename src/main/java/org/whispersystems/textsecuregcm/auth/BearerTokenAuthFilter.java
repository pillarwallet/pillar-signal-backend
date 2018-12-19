package org.whispersystems.textsecuregcm.auth;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.basic.BasicCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.dropwizard.simpleauth.AuthFilter;
import org.whispersystems.dropwizard.simpleauth.AuthSecurityContext;
import org.whispersystems.textsecuregcm.storage.Account;

import javax.annotation.Priority;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;

@Priority(Priorities.AUTHENTICATION)
public class BearerTokenAuthFilter<P> extends AuthFilter<BasicCredentials, P> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BearerTokenAuthFilter.class);
    String prefix;

    private BearerTokenAuthFilter() {
        this.prefix = "Bearer";
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        final String header = requestContext.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        try {
            if (header != null) {
                final int space = header.indexOf(' ');
                if (space > 0) {
                    final String method = header.substring(0, space);
                    if (prefix.equalsIgnoreCase(method)) {
                        try {
                            Optional<Account> principal = ((AccountAuthenticator) authenticator).authenticate(header.substring(space + 1));
                            if (principal.isPresent()) {
                                requestContext.setSecurityContext(new AuthSecurityContext<Account>(principal.get(), false));
                                return;
                            }
                            LOGGER.error("Failed to authenticate user");
                        } catch (AuthenticationException e) {
                            LOGGER.warn("Error authenticating user from JWT", e);
                        }
                        throw new InternalServerErrorException();
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Error decoding Bearer token", e);
        }

        throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
    }

    public static class Builder<P> extends AuthFilter.AuthFilterBuilder<BasicCredentials, P, BearerTokenAuthFilter<P>> {
        @Override
        protected BearerTokenAuthFilter<P> newInstance() {
            return new BearerTokenAuthFilter<>();
        }
    }
}
