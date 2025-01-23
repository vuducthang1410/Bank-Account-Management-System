package org.demo.loanservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController

public class CommonController {
    @GetMapping("/sse")
    public SseEmitter streamEvent(){
        SseEmitter emitter=new SseEmitter(30000L);
        // Sử dụng một thread pool để gửi dữ liệu không đồng bộ
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // Gửi sự kiện đầu tiên
                emitter.send(SseEmitter.event().name("message").data("Hello from server!"));

                // Gửi thêm các sự kiện liên tục trong vòng 10 giây
                for (int i = 1; i <= 10; i++) {
                    TimeUnit.SECONDS.sleep(1); // Giả lập xử lý
                    emitter.send(SseEmitter.event()
                            .name("update")
                            .data("Update " + i)
                            .id(String.valueOf(i))
                            .reconnectTime(3000));
                }

                // Kết thúc luồng
//                emitter.complete();
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);
            } finally {
                executor.shutdown();
            }
        });
        return emitter;
    }
}
