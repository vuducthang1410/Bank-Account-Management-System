package org.demo.accountservice.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestLoggingFilterConfig {
    @Bean
    public CustomRequestLoggingFilter requestLoggingFilter() {
        CustomRequestLoggingFilter requestLoggingFilter = new CustomRequestLoggingFilter();
        requestLoggingFilter.setIncludeClientInfo(true);
        requestLoggingFilter.setIncludeHeaders(true);
        requestLoggingFilter.setIncludePayload(true);
        requestLoggingFilter.setIncludeQueryString(true);
        requestLoggingFilter.setMaxPayloadLength(1000);
        requestLoggingFilter.setAfterMessagePrefix("DATA-REQUEST:: ");
        return requestLoggingFilter;
    }
    @Bean
    public FilterRegistrationBean<CustomRequestLoggingFilter> customRequestLoggingFilter() {
        FilterRegistrationBean<CustomRequestLoggingFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(requestLoggingFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
}
