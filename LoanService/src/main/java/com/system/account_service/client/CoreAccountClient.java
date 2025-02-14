package com.system.account_service.client;

import com.system.account_service.client.fallback.CoreAccountClientFallback;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        value = "core-account-client",
        url = "${service.core.banking.url}/accounts",
        fallback = CoreAccountClientFallback.class
)
public interface CoreAccountClient {
}
