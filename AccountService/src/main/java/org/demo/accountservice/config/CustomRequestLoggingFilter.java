package org.demo.accountservice.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

public class CustomRequestLoggingFilter extends AbstractRequestLoggingFilter {
    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {

    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        logger.info(message);
    }
}
