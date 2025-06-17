package com.votaciones.admin;

import Admin.*;
import com.zeroc.Ice.Current;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;
import java.util.*;

public class AdminServerI implements Admin.AdminServer {
    private static final Logger logger = LoggerFactory.getLogger(AdminServerI.class);
    private static final String CANDIDATOS_FILE = "ListaCandidatos.xlsx";
    private static final String RESULTADOS_FILE = "Resultados.csv";

    @Override
    public void agregarCandidato(String nombre, String partido, String cargo, Current current) {
        try (Connection conn = AdminServerMain.getDBConnection()) {
            String sql = "INSERT INTO candidatos (documento, nombres, apellidos, partido_politico) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                // Generar un documento único para el candidato
                String documento = "CAND" + System.currentTimeMillis();
                String[] nombres = nombre.split(" ", 2);
                String nombresStr = nombres[0];
                String apellidosStr = nombres.length > 1 ? nombres[1] : "";
                
                pstmt.setString(1, documento);
                pstmt.setString(2, nombresStr);
                pstmt.setString(3, apellidosStr);
                pstmt.setString(4, partido);
                pstmt.executeUpdate();
            }
            logger.info("Candidato agregado: {}", nombre);
        } catch (SQLException e) {
            logger.error("Error al agregar candidato: {}", e.getMessage());
            throw new RuntimeException("Error al agregar candidato", e);
        }
    }

    @Override
    public void modificarCandidato(String id, String nombre, String partido, String cargo, Current current) {
        try (Connection conn = AdminServerMain.getDBConnection()) {
            String sql = "UPDATE candidatos SET nombres = ?, apellidos = ?, partido_politico = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                String[] nombres = nombre.split(" ", 2);
                String nombresStr = nombres[0];
                String apellidosStr = nombres.length > 1 ? nombres[1] : "";
                
                pstmt.setString(1, nombresStr);
                pstmt.setString(2, apellidosStr);
                pstmt.setString(3, partido);
                pstmt.setString(4, id);
                pstmt.executeUpdate();
            }
            logger.info("Candidato modificado: {}", nombre);
        } catch (SQLException e) {
            logger.error("Error al modificar candidato: {}", e.getMessage());
            throw new RuntimeException("Error al modificar candidato", e);
        }
    }

    @Override
    public void eliminarCandidato(String id, Current current) {
        try (Connection conn = AdminServerMain.getDBConnection()) {
            String sql = "DELETE FROM candidatos WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, id);
                pstmt.executeUpdate();
            }
            logger.info("Candidato eliminado: {}", id);
        } catch (SQLException e) {
            logger.error("Error al eliminar candidato: {}", e.getMessage());
            throw new RuntimeException("Error al eliminar candidato", e);
        }
    }

    @Override
    public String[] listarCandidatos(Current current) {
        List<String> candidatos = new ArrayList<>();
        try (Connection conn = AdminServerMain.getDBConnection()) {
            String sql = "SELECT id, nombres, apellidos, partido_politico FROM candidatos";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    candidatos.add(String.format("%s,%s %s,%s",
                        rs.getString("id"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("partido_politico")));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al listar candidatos: {}", e.getMessage());
            throw new RuntimeException("Error al listar candidatos", e);
        }
        return candidatos.toArray(new String[0]);
    }

    @Override
    public void procesarVotos(String zona, Current current) {
        try (Connection conn = AdminServerMain.getDBConnection()) {
            String sql = "SELECT c.nombres, c.apellidos, c.partido_politico, COUNT(v.id) as votos " +
                        "FROM votos v " +
                        "JOIN candidatos c ON v.candidato_id = c.id " +
                        "JOIN mesas_votacion m ON v.mesa_id = m.id " +
                        "WHERE m.zona = ? " +
                        "GROUP BY c.nombres, c.apellidos, c.partido_politico";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, zona);
                try (ResultSet rs = pstmt.executeQuery()) {
                    // Procesar resultados
                    while (rs.next()) {
                        logger.info("Resultados para zona {}: {} {} - {} votos",
                            zona, rs.getString("nombres"), rs.getString("apellidos"), rs.getInt("votos"));
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error al procesar votos: {}", e.getMessage());
            throw new RuntimeException("Error al procesar votos", e);
        }
    }

    @Override
    public String obtenerResultadosZona(String zona, Current current) {
        StringBuilder resultados = new StringBuilder();
        try (Connection conn = AdminServerMain.getDBConnection()) {
            String sql = "SELECT c.nombres, c.apellidos, c.partido_politico, COUNT(v.id) as votos " +
                        "FROM votos v " +
                        "JOIN candidatos c ON v.candidato_id = c.id " +
                        "JOIN mesas_votacion m ON v.mesa_id = m.id " +
                        "WHERE m.zona = ? " +
                        "GROUP BY c.nombres, c.apellidos, c.partido_politico";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, zona);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        resultados.append(String.format("%s %s,%s,%d\n",
                            rs.getString("nombres"),
                            rs.getString("apellidos"),
                            rs.getString("partido_politico"),
                            rs.getInt("votos")));
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener resultados por zona: {}", e.getMessage());
            throw new RuntimeException("Error al obtener resultados por zona", e);
        }
        return resultados.toString();
    }

    @Override
    public String obtenerResultadosGlobales(Current current) {
        StringBuilder resultados = new StringBuilder();
        try (Connection conn = AdminServerMain.getDBConnection()) {
            String sql = "SELECT c.nombres, c.apellidos, c.partido_politico, COUNT(v.id) as votos " +
                        "FROM votos v " +
                        "JOIN candidatos c ON v.candidato_id = c.id " +
                        "GROUP BY c.nombres, c.apellidos, c.partido_politico";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    resultados.append(String.format("%s %s,%s,%d\n",
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("partido_politico"),
                        rs.getInt("votos")));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener resultados globales: {}", e.getMessage());
            throw new RuntimeException("Error al obtener resultados globales", e);
        }
        return resultados.toString();
    }

    @Override
    public void generarReporteCSV(String tipoReporte, Current current) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(RESULTADOS_FILE))) {
            String[] header = {"Candidato", "Partido", "Votos", "Porcentaje"};
            writer.writeNext(header);

            String resultados = tipoReporte.equals("GLOBAL") ? 
                obtenerResultadosGlobales(current) : obtenerResultadosZona(tipoReporte, current);

            for (String linea : resultados.split("\n")) {
                if (!linea.trim().isEmpty()) {
                    String[] datos = linea.split(",");
                    writer.writeNext(datos);
                }
            }
            logger.info("Reporte CSV generado: {}", RESULTADOS_FILE);
        } catch (IOException e) {
            logger.error("Error al generar reporte CSV: {}", e.getMessage());
            throw new RuntimeException("Error al generar reporte CSV", e);
        }
    }

    @Override
    public void exportarResultadosExcel(Current current) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Resultados");
            
            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Candidato");
            headerRow.createCell(1).setCellValue("Partido");
            headerRow.createCell(2).setCellValue("Votos");
            headerRow.createCell(3).setCellValue("Porcentaje");

            // Obtener y escribir datos
            String[] candidatos = listarCandidatos(current);
            int rowNum = 1;
            for (String candidato : candidatos) {
                String[] datos = candidato.split(",");
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(datos[1]); // nombre completo
                row.createCell(1).setCellValue(datos[2]); // partido
                // Agregar votos y porcentaje
            }

            // Guardar archivo
            try (FileOutputStream fileOut = new FileOutputStream(CANDIDATOS_FILE)) {
                workbook.write(fileOut);
            }
            logger.info("Resultados exportados a Excel: {}", CANDIDATOS_FILE);
        } catch (IOException e) {
            logger.error("Error al exportar resultados a Excel: {}", e.getMessage());
            throw new RuntimeException("Error al exportar resultados a Excel", e);
        }
    }

    @Override
    public void registrarLog(String evento, String detalle, Current current) {
        try (Connection conn = AdminServerMain.getDBConnection()) {
            String sql = "INSERT INTO logs (evento, detalle, fecha) VALUES (?, ?, CURRENT_TIMESTAMP)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, evento);
                pstmt.setString(2, detalle);
                pstmt.executeUpdate();
            }
            logger.info("Log registrado: {} - {}", evento, detalle);
        } catch (SQLException e) {
            logger.error("Error al registrar log: {}", e.getMessage());
            throw new RuntimeException("Error al registrar log", e);
        }
    }

    @Override
    public String[] obtenerLogs(String fecha, Current current) {
        List<String> logs = new ArrayList<>();
        try (Connection conn = AdminServerMain.getDBConnection()) {
            String sql = "SELECT * FROM logs WHERE DATE(fecha) = ? ORDER BY fecha DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, fecha);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        logs.add(String.format("%s,%s,%s",
                            rs.getString("evento"),
                            rs.getString("detalle"),
                            rs.getTimestamp("fecha")));
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener logs: {}", e.getMessage());
            throw new RuntimeException("Error al obtener logs", e);
        }
        return logs.toArray(new String[0]);
    }

    @Override
    public boolean validarFormatoDatos(String datos, Current current) {
        // Implementar validación de formato según requerimientos
        return datos != null && !datos.trim().isEmpty();
    }

    @Override
    public boolean validarIntegridadResultados(Current current) {
        try (Connection conn = AdminServerMain.getDBConnection()) {
            // Verificar que no haya votos duplicados
            String sql = "SELECT COUNT(*) as total, COUNT(DISTINCT id) as unicos FROM votos";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    return rs.getInt("total") == rs.getInt("unicos");
                }
            }
        } catch (SQLException e) {
            logger.error("Error al validar integridad de resultados: {}", e.getMessage());
            throw new RuntimeException("Error al validar integridad de resultados", e);
        }
        return false;
    }

    // Método adicional para cargar candidatos desde Excel
    public void cargarCandidatosDesdeExcel(String rutaArchivo) {
        try (FileInputStream fis = new FileInputStream(rutaArchivo);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String nombres = getCellValueAsString(row.getCell(0));
                    String apellidos = getCellValueAsString(row.getCell(1));
                    String partido = getCellValueAsString(row.getCell(2));
                    String documento = getCellValueAsString(row.getCell(3));
                    
                    if (!nombres.isEmpty() && !partido.isEmpty()) {
                        try (Connection conn = AdminServerMain.getDBConnection()) {
                            String sql = "INSERT INTO candidatos (documento, nombres, apellidos, partido_politico) VALUES (?, ?, ?, ?)";
                            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                                pstmt.setString(1, documento.isEmpty() ? "CAND" + System.currentTimeMillis() + i : documento);
                                pstmt.setString(2, nombres);
                                pstmt.setString(3, apellidos);
                                pstmt.setString(4, partido);
                                pstmt.executeUpdate();
                            }
                        }
                        logger.info("Candidato cargado desde Excel: {} {}", nombres, apellidos);
                    }
                }
            }
            logger.info("Carga de candidatos desde Excel completada");
        } catch (Exception e) {
            logger.error("Error al cargar candidatos desde Excel: {}", e.getMessage());
            throw new RuntimeException("Error al cargar candidatos desde Excel", e);
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
} 