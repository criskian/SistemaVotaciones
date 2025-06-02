package com.sistemaelectoral.portal;

import com.sistemaelectoral.interfaces.SistemaElectoral._ConsultoriaDisp;
import Ice.Current;
import okhttp3.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConsultoriaImpl extends _ConsultoriaDisp {
    private final OkHttpClient client;
    private final Properties config;
    private final Gson gson;
    private final String supabaseUrl;
    private final String supabaseKey;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    public ConsultoriaImpl() throws IOException {
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
    public String[] listarCandidatos(Current current) {
        try {
            JsonArray candidatos = executeQuery(
                config.getProperty("Database.Tables.Candidatos"),
                "?select=nombre,partido"
            );
            String[] resultado = new String[candidatos.size()];
            for (int i = 0; i < candidatos.size(); i++) {
                JsonObject candidato = candidatos.get(i).getAsJsonObject();
                resultado[i] = candidato.get("nombre").getAsString() + " - " + 
                             candidato.get("partido").getAsString();
            }
            return resultado;
        } catch (Exception e) {
            System.err.println("Error al listar candidatos: " + e.getMessage());
            return new String[]{"Error al listar candidatos"};
        }
    }
    
    @Override
    public int[] verConteoVotos(Current current) {
        try {
            JsonArray conteo = executeQuery(
                config.getProperty("Database.Tables.Resultados"),
                "?select=votos&order=candidato.asc"
            );
            int[] resultado = new int[conteo.size()];
            for (int i = 0; i < conteo.size(); i++) {
                resultado[i] = conteo.get(i).getAsJsonObject().get("votos").getAsInt();
            }
            return resultado;
        } catch (Exception e) {
            System.err.println("Error al obtener conteo de votos: " + e.getMessage());
            return new int[]{0};
        }
    }
}
