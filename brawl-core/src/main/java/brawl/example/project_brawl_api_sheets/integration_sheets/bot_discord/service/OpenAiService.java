package brawl.example.project_brawl_api_sheets.integration_sheets.bot_discord.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class OpenAiService {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private final OkHttpClient client = new OkHttpClient();

    @Value("${openai.api.key}")
    private String apiKey;

    public String analyzeJsonWithAI(String jsonString) throws Exception {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", "gpt-4o-mini");

        JSONArray messages = new JSONArray();
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", "Analise o seguinte JSON e me diga o que contém:\n" + jsonString);

        messages.put(userMessage);
        jsonBody.put("messages", messages);

        RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .header("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("Erro na API OpenAI: " + response.code() + " - " + response.message());
            }
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);
            return jsonResponse
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        }
    }
}

