package com.sistemaelectoral.votacion;

import com.sistemaelectoral.interfaces.SistemaElectoral._MesaVotacionDisp;
import Ice.Current;
import com.sistemaelectoral.interfaces.SistemaElectoral.VotacionException;
import com.sistemaelectoral.interfaces.SistemaElectoral.EstadisticasVotacion;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.time.Instant;

public class MesaVotacionImpl extends _MesaVotacionDisp {
    private final OkHttpClient client;
    private final Properties config;
    private final String baseUrl;
    private final String apiKey;
    private final Map<String, Integer> votosContados;

    public MesaVotacionImpl() {
        client = new OkHttpClient();
        config = new Properties();
        votosContados = new HashMap<>();
        try {
            config.load(new FileInputStream("config.votacion"));
            baseUrl = config.getProperty("supabase.url");
            apiKey = config.getProperty("supabase.key");
        } catch (IOException e) {
            throw new RuntimeException("Error loading configuration", e);
        }
    }

    @Override
    public boolean registrarVoto(String cedula, String candidato, String mesa, Current current) throws VotacionException {
        try {
            // Verificar si el votante ya votó
            Request checkRequest = new Request.Builder()
                .url(baseUrl + "/rest/v1/votos?cedula=eq." + cedula)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

            try (Response checkResponse = client.newCall(checkRequest).execute()) {
                if (!checkResponse.isSuccessful()) {
                    throw new VotacionException("Error verificando voto previo");
                }

                String checkBody = checkResponse.body().string();
                JsonArray checkArray = new Gson().fromJson(checkBody, JsonArray.class);
                if (checkArray.size() > 0) {
                    throw new VotacionException("El votante ya ejerció su derecho al voto");
                }
            }

            // Registrar el voto
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            JsonObject voto = new JsonObject();
            voto.addProperty("cedula", cedula);
            voto.addProperty("candidato", candidato);
            voto.addProperty("mesa", mesa);
            voto.addProperty("timestamp", Instant.now().toString());

            RequestBody body = RequestBody.create(voto.toString(), JSON);
            Request request = new Request.Builder()
                .url(baseUrl + "/rest/v1/votos")
                .post(body)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new VotacionException("Error registrando voto");
                }
                
                // Actualizar conteo local
                votosContados.merge(mesa, 1, Integer::sum);
                return true;
            }
        } catch (VotacionException e) {
            throw e;
        } catch (Exception e) {
            throw new VotacionException("Error en el sistema: " + e.getMessage());
        }
    }

    @Override
    public String[] obtenerResultadosMesa(String mesa, Current current) throws VotacionException {
        try {
            Request request = new Request.Builder()
                .url(baseUrl + "/rest/v1/votos?mesa=eq." + mesa + "&select=candidato,count")
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new VotacionException("Error obteniendo resultados");
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
        } catch (VotacionException e) {
            throw e;
        } catch (Exception e) {
            throw new VotacionException("Error en el sistema: " + e.getMessage());
        }
    }

    @Override
    public String[] listarMesas(Current current) throws VotacionException {
        try {
            Request request = new Request.Builder()
                .url(baseUrl + "/rest/v1/mesas?select=id")
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new VotacionException("Error listando mesas");
                }

                String responseBody = response.body().string();
                JsonArray jsonArray = new Gson().fromJson(responseBody, JsonArray.class);
                ArrayList<String> mesas = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    mesas.add(jsonArray.get(i).getAsJsonObject().get("id").getAsString());
                }
                return mesas.toArray(new String[0]);
            }
        } catch (VotacionException e) {
            throw e;
        } catch (Exception e) {
            throw new VotacionException("Error en el sistema: " + e.getMessage());
        }
    }

    @Override
    public String obtenerMesaAsignada(String cedula, Current current) throws VotacionException {
        try {
            Request request = new Request.Builder()
                .url(baseUrl + "/rest/v1/votantes?cedula=eq." + cedula + "&select=mesa")
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new VotacionException("Error obteniendo mesa asignada");
                }

                String responseBody = response.body().string();
                JsonArray jsonArray = new Gson().fromJson(responseBody, JsonArray.class);
                if (jsonArray.size() == 0) {
                    throw new VotacionException("Votante no encontrado");
                }

                return jsonArray.get(0).getAsJsonObject().get("mesa").getAsString();
            }
        } catch (VotacionException e) {
            throw e;
        } catch (Exception e) {
            throw new VotacionException("Error en el sistema: " + e.getMessage());
        }
    }

    @Override
    public boolean verificarCedula(String cedula, Current current) throws VotacionException {
        try {
            Request request = new Request.Builder()
                .url(baseUrl + "/rest/v1/votantes?cedula=eq." + cedula)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new VotacionException("Error verificando cédula");
                }

                String responseBody = response.body().string();
                JsonArray jsonArray = new Gson().fromJson(responseBody, JsonArray.class);
                return jsonArray.size() > 0;
            }
        } catch (VotacionException e) {
            throw e;
        } catch (Exception e) {
            throw new VotacionException("Error en el sistema: " + e.getMessage());
        }
    }

    @Override
    public boolean validarVotante(String cedula, String mesa, Current current) throws VotacionException {
        try {
            String mesaAsignada = obtenerMesaAsignada(cedula, current);
            return mesa.equals(mesaAsignada);
        } catch (VotacionException e) {
            throw e;
        } catch (Exception e) {
            throw new VotacionException("Error en el sistema: " + e.getMessage());
        }
    }

    @Override
    public EstadisticasVotacion obtenerEstadisticas(Current current) throws VotacionException {
        try {
            EstadisticasVotacion stats = new EstadisticasVotacion();
            stats.conteoVotos = new java.util.HashMap<>(votosContados);
            return stats;
        } catch (Exception e) {
            throw new VotacionException("Error obteniendo estadísticas: " + e.getMessage());
        }
    }
} 