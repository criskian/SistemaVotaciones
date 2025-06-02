package com.sistemaelectoral.consulta;

import com.sistemaelectoral.interfaces.SistemaElectoral.Consulta;
import com.sistemaelectoral.interfaces.SistemaElectoral.StringSeq;
import com.zeroc.Ice.Current;
import okhttp3.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.ArrayList;

public class ConsultaImpl implements Consulta {
    private final OkHttpClient client;
    private final Properties config;
    private final String baseUrl;
    private final String apiKey;

    public ConsultaImpl() {
        client = new OkHttpClient();
        config = new Properties();
        try {
            config.load(new FileInputStream("config.consulta"));
            baseUrl = config.getProperty("supabase.url");
            apiKey = config.getProperty("supabase.key");
        } catch (IOException e) {
            throw new RuntimeException("Error loading configuration", e);
        }
    }

    @Override
    public String[] consultarResultados(Current current) {
        try {
            Request request = new Request.Builder()
                .url(baseUrl + "/rest/v1/votos?select=candidato,count")
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                String responseBody = response.body().string();
                JsonArray jsonArray = new Gson().fromJson(responseBody, JsonArray.class);
                ArrayList<String> resultados = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject resultado = jsonArray.get(i).getAsJsonObject();
                    resultados.add(resultado.get("candidato").getAsString() + ": " + 
                                 resultado.get("count").getAsString());
                }
                return resultados.toArray(new String[0]);
            }
        } catch (Exception e) {
            System.err.println("Error consultando resultados: " + e.getMessage());
            return new String[0];
        }
    }

    @Override
    public String[] consultarCandidatos(Current current) {
        try {
            Request request = new Request.Builder()
                .url(baseUrl + "/rest/v1/candidatos?select=nombre")
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                String responseBody = response.body().string();
                JsonArray jsonArray = new Gson().fromJson(responseBody, JsonArray.class);
                ArrayList<String> candidatos = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    candidatos.add(jsonArray.get(i).getAsJsonObject().get("nombre").getAsString());
                }
                return candidatos.toArray(new String[0]);
            }
        } catch (Exception e) {
            System.err.println("Error consultando candidatos: " + e.getMessage());
            return new String[0];
        }
    }

    @Override
    public String consultarMesaVotante(String cedula, Current current) {
        try {
            Request request = new Request.Builder()
                .url(baseUrl + "/rest/v1/votantes?cedula=eq." + cedula + "&select=mesa")
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                String responseBody = response.body().string();
                JsonArray jsonArray = new Gson().fromJson(responseBody, JsonArray.class);
                if (jsonArray.size() == 0) {
                    return "No encontrado";
                }

                return jsonArray.get(0).getAsJsonObject().get("mesa").getAsString();
            }
        } catch (Exception e) {
            System.err.println("Error consultando mesa del votante: " + e.getMessage());
            return "Error";
        }
    }

    @Override
    public String[] ice_ids(Current current) {
        return new String[]{"::Ice::Object", "::SistemaElectoral::Consulta"};
    }

    @Override
    public String ice_id(Current current) {
        return "::SistemaElectoral::Consulta";
    }

    @Override
    public boolean ice_isA(String s, Current current) {
        return java.util.Arrays.binarySearch(ice_ids(current), s) >= 0;
    }

    @Override
    public void ice_ping(Current current) {}
} 