package com.sistemaelectoral.consulta;

import com.sistemaelectoral.interfaces.SistemaElectoral._ConsultaCiudadanoDisp;
import Ice.Current;
import okhttp3.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConsultaCiudadanoImpl extends _ConsultaCiudadanoDisp {
    private final OkHttpClient client;
    private final Properties config;
    private final Gson gson;
    private final String supabaseUrl;
    private final String supabaseKey;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    public ConsultaCiudadanoImpl() throws IOException {
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
    public String consultarLugarVotacion(String cedula, Current __current) {
        try {
            JsonArray result = executeQuery(
                config.getProperty("Database.Tables.CiudadanosMesaZonaAsignada"),
                "?cedula=eq." + cedula + "&select=zona_votacion"
            );
            
            if (result.size() == 0) {
                return "No se encontró lugar de votación para la cédula: " + cedula;
            }
            
            return result.get(0).getAsJsonObject().get("zona_votacion").getAsString();
        } catch (Exception e) {
            return "Error al consultar lugar de votación: " + e.getMessage();
        }
    }
    
    @Override
    public String consultarMesaVotacion(String cedula, Current __current) {
        try {
            JsonArray result = executeQuery(
                config.getProperty("Database.Tables.CiudadanosMesaZonaAsignada"),
                "?cedula=eq." + cedula + "&select=mesa_id"
            );
            
            if (result.size() == 0) {
                return "No se encontró mesa de votación para la cédula: " + cedula;
            }
            
            return result.get(0).getAsJsonObject().get("mesa_id").getAsString();
        } catch (Exception e) {
            return "Error al consultar mesa de votación: " + e.getMessage();
        }
    }
    
    @Override
    public String consultarZonaVotacion(String cedula, Current __current) {
        try {
            JsonArray result = executeQuery(
                config.getProperty("Database.Tables.CiudadanosMesaZonaAsignada"),
                "?cedula=eq." + cedula + "&select=zona_id"
            );
            
            if (result.size() == 0) {
                return "No se encontró zona de votación para la cédula: " + cedula;
            }
            
            return result.get(0).getAsJsonObject().get("zona_id").getAsString();
        } catch (Exception e) {
            return "Error al consultar zona de votación: " + e.getMessage();
        }
    }
} 