package com.senddearswhatsappmessages.Services.Message;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service

public class MessageServiceImpli implements MessageService {
    private static final String ACCOUNT_SID = "AC5aa6462bfeb047ca531af6b1c152c1c6";
    private static final String AUTH_TOKEN = "b6747a2ad264cc76c66f0a19116d4214";

    @Override
    public void sendMessage(String message, String token, String targetPhone) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message messageX;

        for (int i=0;i<10;i++){

            messageX = Message.creator(
                            new com.twilio.type.PhoneNumber("whatsapp:+21628552642"),
                            new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                            "Love UUUUUUUUUUUUUUU " +i
                    )
                    .create();
        }

       /*b try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://graph.facebook.com/v13.0/136003749605242/messages"))
                    .header("Authorization", "Bearer EAAOZATzu1Xq8BOZBoZBKzh7CofZAOdUDfnDClAVdPVcdVRT9ao3BwKZCfk2oB8XcmpfxtWwBBpI3wj3mq7XjZCSdpguDqmmJa2I8WRmQsYK2N9xZBZBgfpiwTaCSQghl1cLZBqsilrF5kkKcKYffX3FSSRHJheZBgg79Kng3K7QZBV9L19cVFxS5hTvdb9gopUCf44Ag3tcUBytdXju4FHNVmJqSZC0M5WLS1LZC49DkZD")
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{ \"messaging_product\": \"whatsapp\", \"recipient_type\": \"individual\", \"to\": \"21629610241\", \"type\": \"text\", \"text\": { \"body\": \"I LOVE U <3\" } }"))
                    .build();
            HttpClient http = HttpClient.newHttpClient();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void sendMessagePeriod(String message, String token, LocalDateTime dateTime, String targetPhone) {

    }

}
