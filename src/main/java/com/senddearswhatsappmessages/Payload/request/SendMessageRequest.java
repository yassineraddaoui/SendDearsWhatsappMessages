package com.senddearswhatsappmessages.Payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageRequest {
    private String message;
    private String targetPhone;
    private LocalDateTime dateTime;
    private String token;

    public SendMessageRequest(String message, String targetPhone,String token) {
        this.message = message;
        this.targetPhone = targetPhone;
        this.token=token;
    }


}
