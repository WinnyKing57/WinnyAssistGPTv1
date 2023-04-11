package com.example.winnyassistant;

import android.content.Context;

import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.TextInput;

import java.io.IOException;
import java.util.UUID;

public class ChatGPT {
    private String sessionId;
    private String languageCode;
    private SessionsClient sessionsClient;
    private SessionName sessionName;
    private Context context;

    public ChatGPT(String apiKey, Context context) throws IOException {
        this.sessionId = UUID.randomUUID().toString();
        this.languageCode = "fr-FR";
        this.sessionsClient = SessionsClient.create();
        this.sessionName = SessionName.ofProjectSessionName("projet-wag-mcn", sessionId);
        this.context = context;
    }

    public void addMessage(String message) {
        QueryInput input = QueryInput.newBuilder()
                .setText(TextInput.newBuilder().setText(message).setLanguageCode(languageCode))
                .build();

        try {
            DetectIntentResponse response = sessionsClient.detectIntent(sessionName, input);
            QueryResult queryResult = response.getQueryResult();
            String responseText = queryResult.getFulfillmentText();
            generateResponse(responseText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateResponse(String responseText) {
        ((MainActivity) context).runOnUiThread(() -> ((MainActivity) context).getResponseAdapter().add(responseText));
    }
}
