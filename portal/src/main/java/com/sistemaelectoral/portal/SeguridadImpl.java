package com.sistemaelectoral.portal;

import com.sistemaelectoral.interfaces.SistemaElectoral._SeguridadDisp;
import Ice.Current;
import okhttp3.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.time.Instant;

public class SeguridadImpl extends _SeguridadDisp {
    private final OkHttpClient client;
    private final Properties config;
    private final Gson gson;
    private final String supabaseUrl;
    private final String supabaseKey;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    public SeguridadImpl() throws IOException {
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
    
    private String generateToken(String usuario) {
        try {
            String timestamp = String.valueOf(Instant.now().getEpochSecond());
            String data = usuario + ":" + timestamp + ":" + supabaseKey;
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
    
    @Override
    public boolean verificarPermisos(String username, String token, Current current) {
        try {
            // Verificar si el token existe y está activo
            JsonArray permisos = executeQuery(
                config.getProperty("Database.Tables.Permisos"),
                "?token=eq." + token + "&username=eq." + username + "&activo=eq.true"
            );
            
            return permisos.size() > 0;
        } catch (Exception e) {
            System.err.println("Error al verificar permisos: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String obtenerToken(String username, String password, Current current) {
        try {
            // Verificar credenciales
            JsonArray usuarios = executeQuery(
                config.getProperty("Database.Tables.Usuarios"),
                "?username=eq." + username + "&password=eq." + password
            );
            
            if (usuarios.size() == 0) {
                return "Error: Credenciales inválidas";
            }
            
            // Generar nuevo token
            String token = generateToken(username);
            if (token == null) {
                return "Error: No se pudo generar el token";
            }
            
            // Registrar el token
            JsonObject tokenData = new JsonObject();
            tokenData.addProperty("username", username);
            tokenData.addProperty("token", token);
            tokenData.addProperty("fecha_creacion", Instant.now().toString());
            
            RequestBody body = RequestBody.create(tokenData.toString(), JSON);
            Request request = createRequestBuilder("/rest/v1/" + config.getProperty("Database.Tables.Tokens"))
                .post(body)
                .build();
                
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return "Error: No se pudo registrar el token";
                }
            }
            
            return token;
        } catch (Exception e) {
            return "Error al obtener token: " + e.getMessage();
        }
    }
}