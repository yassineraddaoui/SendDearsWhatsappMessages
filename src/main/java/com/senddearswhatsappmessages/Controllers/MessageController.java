package com.senddearswhatsappmessages.Controllers;

import com.senddearswhatsappmessages.Payload.request.SendMessageRequest;
import com.senddearswhatsappmessages.Services.Message.MessageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }


    @PostMapping("/send")
    void sendMessge(@RequestBody SendMessageRequest sendRequest) {
        messageService.sendMessage(sendRequest.getMessage(),
                sendRequest.getTargetPhone(),
                sendRequest.getToken());
    }


}
