package com.sistemaelectoral.fiscalia;

import com.sistemaelectoral.interfaces.SistemaElectoral._FiscaliaDisp;
import Ice.Current;
import okhttp3.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class FiscaliaImpl extends _FiscaliaDisp {
    private final OkHttpClient client;
    private final Properties config;
    private final String baseUrl;
    private final String apiKey;

    public FiscaliaImpl() {
        client = new OkHttpClient();
        config = new Properties();
        try {
            config.load(new FileInputStream("config.fiscalia"));
            baseUrl = config.getProperty("supabase.url");
            apiKey = config.getProperty("supabase.key");
        } catch (IOException e) {
            throw new RuntimeException("Error loading configuration", e);
        }
    }

    @Override
    public boolean verificarAntecedentes(String cedula, Current current) {
        try {
            Request request = new Request.Builder()
                .url(baseUrl + "/rest/v1/antecedentes?cedula=eq." + cedula)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                String responseBody = response.body().string();
                JsonArray jsonArray = new Gson().fromJson(responseBody, JsonArray.class);
                return jsonArray.size() == 0; // Si no hay antecedentes, retorna true
            }
        } catch (Exception e) {
            System.err.println("Error verificando antecedentes: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void reportarFraude(String mesa, Current current) {
        // Implementa aquí la lógica para reportar fraude
        System.out.println("Fraude reportado en la mesa: " + mesa);
    }
}