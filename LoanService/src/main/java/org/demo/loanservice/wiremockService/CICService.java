package org.demo.loanservice.wiremockService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.demo.loanservice.dto.CICRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CICService {

    private final WireMockServer wireMockServer;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void startWireMock() {
        if (!wireMockServer.isRunning()) {
            wireMockServer.start();
            System.out.println("WireMock server started on port " + wireMockServer.port());
        }
        setupStub();
    }

    private void setupStub() {
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/api/credit-score/cic"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                    {
                                        "cccd": "079123456789",
                                        "credit_score": 720,
                                        "credit_rating": "Tốt",
                                        "debt_status": "Không nợ xấu",
                                        "last_updated": "2025-02-04T10:30:00Z"
                                    }
                                """)
                )
        );
    }
    public Object getCreditScore(String cccd, String fullName, String dob, String phoneNumber) {
        String url = "http://localhost:8386/api/credit-score/cic";
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(new CICRequest(cccd, fullName, dob, phoneNumber)), String.class);
            return responseEntity.getBody();

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}