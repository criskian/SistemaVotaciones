package com.sistemaelectoral.common;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SupabaseClient {
    private static SupabaseClient instance;
    private final OkHttpClient httpClient;
    private final String supabaseUrl;
    private final String supabaseKey;
    private final Gson gson;

    private SupabaseClient() {
        // Cargar configuración
        Properties props = new Properties();
        try {
            props.load(getClass().getClassLoader().getResourceAsStream("database.config"));
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar la configuración de la base de datos", e);
        }

        this.supabaseUrl = props.getProperty("Database.Url");
        this.supabaseKey = props.getProperty("Database.AnonKey");
        
        // Configurar cliente HTTP
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
            
        this.gson = new Gson();
    }

    public static synchronized SupabaseClient getInstance() {
        if (instance == null) {
            instance = new SupabaseClient();
        }
        return instance;
    }

    public JsonObject query(String table, String query) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(supabaseUrl + "/rest/v1/" + table).newBuilder();
        if (query != null && !query.isEmpty()) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    urlBuilder.addQueryParameter(keyValue[0], keyValue[1]);
                }
            }
        }

        Request request = new Request.Builder()
            .url(urlBuilder.build())
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error en la consulta: " + response.code());
            }
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, JsonObject.class);
        }
    }

    public void insert(String table, Map<String, Object> data) throws IOException {
        String json = gson.toJson(data);
        
        RequestBody body = RequestBody.create(
            MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
            .url(supabaseUrl + "/rest/v1/" + table)
            .post(body)
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "return=minimal")
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error al insertar datos: " + response.code());
            }
        }
    }

    public void update(String table, String query, Map<String, Object> data) throws IOException {
        String json = gson.toJson(data);
        
        RequestBody body = RequestBody.create(
            MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
            .url(supabaseUrl + "/rest/v1/" + table + "?" + query)
            .patch(body)
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "return=minimal")
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error al actualizar datos: " + response.code());
            }
        }
    }

    public void delete(String table, String query) throws IOException {
        Request request = new Request.Builder()
            .url(supabaseUrl + "/rest/v1/" + table + "?" + query)
            .delete()
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error al eliminar datos: " + response.code());
            }
        }
    }
} 