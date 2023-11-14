package com.senddearswhatsappmessages.Services.Message;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

public interface MessageService {
    void sendMessage(String message,String token,String targetPhone);
    void sendMessagePeriod(String message, String token, LocalDateTime dateTime,String targetPhone);

}
