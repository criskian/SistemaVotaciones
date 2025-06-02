package com.sistemaelectoral.portal;

import com.sistemaelectoral.interfaces.SistemaElectoral._AccesoDatosDisp;
import Ice.Current;
import okhttp3.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.time.Instant;

public class AccesoDatosImpl extends _AccesoDatosDisp {
    private final OkHttpClient client;
    private final Properties config;
    private final Gson gson;
    private final String supabaseUrl;
    private final String supabaseKey;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    public AccesoDatosImpl() throws IOException {
        // Cargar configuración
        config = new Properties();
        config.load(new FileInputStream("config/config.properties"));
        
        // Inicializar cliente HTTP
        client = new OkHttpClient();
        gson = new Gson();
        supabaseUrl = config.getProperty("Supabase.URL");
        supabaseKey = config.getProperty("Supabase.Key");
    }
    
    private Request.Builder createRequestBuilder(String path) {
        return new Request.Builder()
            .url(supabaseUrl + path)
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey);
    }
    
    private JsonArray executeQuery(String table, String query) throws IOException {
        Request request = createRequestBuilder("/rest/v1/" + table + query)
            .get()
            .build();
            
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, JsonArray.class);
        }
    }
    
    @Override
    public boolean registrarVoto(String cedula, String candidato, Current current) {
        try {
            // Crear objeto de voto
            JsonObject voto = new JsonObject();
            voto.addProperty("cedula", cedula);
            voto.addProperty("candidato", candidato);
            voto.addProperty("fecha_registro", Instant.now().toString());
            
            // Registrar voto
            RequestBody body = RequestBody.create(voto.toString(), JSON);
            Request request = createRequestBuilder("/rest/v1/" + config.getProperty("Database.Tables.Votos"))
                .post(body)
                .build();
                
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error al registrar voto: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String[] obtenerResultados(Current current) {
        try {
            JsonArray resultados = executeQuery(
                config.getProperty("Database.Tables.Resultados"),
                "?select=candidato,votos"
            );
            String[] resultado = new String[resultados.size()];
            for (int i = 0; i < resultados.size(); i++) {
                JsonObject res = resultados.get(i).getAsJsonObject();
                resultado[i] = res.get("candidato").getAsString() + ": " + 
                             res.get("votos").getAsString();
            }
            return resultado;
        } catch (Exception e) {
            System.err.println("Error al obtener resultados: " + e.getMessage());
            return new String[]{"Error al obtener resultados"};
        }
    }
}