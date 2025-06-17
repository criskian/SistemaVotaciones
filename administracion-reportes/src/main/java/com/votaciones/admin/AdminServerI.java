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
    public void agregarCandidato(String nombre, String partido, String propuestas, Current current) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new RuntimeException("El nombre del candidato es requerido");
        }
        
        try (Connection conn = AdminServerMain.getDBConnection()) {
            // Primero verificar si ya existe un candidato con el mismo nombre
            String checkSql = "SELECT COUNT(*) FROM candidatos WHERE nombre = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, nombre);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new RuntimeException("Ya existe un candidato con el nombre: " + nombre);
                    }
                }
            }

            // Si no existe, proceder con la inserción
            String sql = "INSERT INTO candidatos (nombre, partido, propuestas) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nombre);
                pstmt.setString(2, partido);
                pstmt.setString(3, propuestas);
                pstmt.executeUpdate();
            }
            logger.info("Candidato agregado: {}", nombre);
        } catch (SQLException e) {
            logger.error("Error al agregar candidato: {}", e.getMessage());
            throw new RuntimeException("Error al agregar candidato: " + e.getMessage());
        }
    }

    @Override
    public void modificarCandidato(String id, String nombre, String partido, String propuestas, Current current) {
        try (Connection conn = AdminServerMain.getDBConnection()) {
            String sql = "UPDATE candidatos SET nombre = ?, partido = ?, propuestas = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nombre);
                pstmt.setString(2, partido);
                pstmt.setString(3, propuestas);
                pstmt.setInt(4, Integer.parseInt(id));
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
                pstmt.setInt(1, Integer.parseInt(id));
                pstmt.executeUpdate();
            }
            logger.info("Candidato eliminado con ID: {}", id);
        } catch (SQLException e) {
            logger.error("Error al eliminar candidato: {}", e.getMessage());
            throw new RuntimeException("Error al eliminar candidato", e);
        }
    }

    @Override
    public String[] listarCandidatos(Current current) {
        List<String> candidatos = new ArrayList<>();
        try (Connection conn = AdminServerMain.getDBConnection()) {
            String sql = "SELECT id, nombre, partido, propuestas FROM candidatos";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    candidatos.add(String.format("%d,%s,%s,%s",
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("partido"),
                        rs.getString("propuestas")));
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
            String sql = "SELECT c.nombre, c.partido, COUNT(v.id) as votos " +
                        "FROM votos v " +
                        "JOIN candidatos c ON v.candidato_id = c.id " +
                        "JOIN mesas_votacion m ON v.mesa_id = m.id " +
                        "JOIN colegios co ON m.colegio_id = co.id " +
                        "JOIN zonas_electorales z ON co.zona_id = z.id " +
                        "WHERE z.codigo = ? " +
                        "GROUP BY c.nombre, c.partido";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, zona);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        logger.info("Resultados para zona {}: {} - {} votos",
                            zona, rs.getString("nombre"), rs.getInt("votos"));
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
            String sql = "SELECT c.nombre, c.partido, COUNT(v.id) as votos " +
                        "FROM votos v " +
                        "JOIN candidatos c ON v.candidato_id = c.id " +
                        "JOIN mesas_votacion m ON v.mesa_id = m.id " +
                        "JOIN colegios co ON m.colegio_id = co.id " +
                        "JOIN zonas_electorales z ON co.zona_id = z.id " +
                        "WHERE z.codigo = ? " +
                        "GROUP BY c.nombre, c.partido";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, zona);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        resultados.append(String.format("%s,%s,%d\n",
                            rs.getString("nombre"),
                            rs.getString("partido"),
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
            String sql = "SELECT c.nombre, c.partido, COUNT(v.id) as votos " +
                        "FROM votos v " +
                        "JOIN candidatos c ON v.candidato_id = c.id " +
                        "GROUP BY c.nombre, c.partido";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    resultados.append(String.format("%s,%s,%d\n",
                        rs.getString("nombre"),
                        rs.getString("partido"),
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
                row.createCell(0).setCellValue(datos[1]); // nombre
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
    public void registrarLog(String tipo, String mensaje, Current current) {
        try (Connection conn = AdminServerMain.getDBConnection()) {
            String sql = "INSERT INTO logs (tipo, mensaje, fecha_hora) VALUES (?, ?, CURRENT_TIMESTAMP)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, tipo);
                pstmt.setString(2, mensaje);
                pstmt.executeUpdate();
            }
            logger.info("Log registrado: {} - {}", tipo, mensaje);
        } catch (SQLException e) {
            logger.error("Error al registrar log: {}", e.getMessage());
            throw new RuntimeException("Error al registrar log", e);
        }
    }

    @Override
    public String[] obtenerLogs(String fecha, Current current) {
        List<String> logs = new ArrayList<>();
        try (Connection conn = AdminServerMain.getDBConnection()) {
            String sql = "SELECT * FROM logs WHERE DATE(fecha_hora) = ? ORDER BY fecha_hora DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, fecha);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        logs.add(String.format("%s,%s,%s",
                            rs.getString("tipo"),
                            rs.getString("mensaje"),
                            rs.getTimestamp("fecha_hora")));
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
        return datos != null && !datos.trim().isEmpty();
    }

    @Override
    public boolean validarIntegridadResultados(Current current) {
        try (Connection conn = AdminServerMain.getDBConnection()) {
            // Verificar que no haya votos duplicados
            String sql = "SELECT ciudadano_id, COUNT(*) as total " +
                        "FROM votos " +
                        "GROUP BY ciudadano_id " +
                        "HAVING COUNT(*) > 1";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    logger.error("Se encontraron votos duplicados");
                    return false;
                }
            }

            // Verificar que todos los votos correspondan a mesas activas
            sql = "SELECT COUNT(*) as total " +
                  "FROM votos v " +
                  "JOIN mesas_votacion m ON v.mesa_id = m.id " +
                  "WHERE m.activa = false";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next() && rs.getInt("total") > 0) {
                    logger.error("Se encontraron votos en mesas inactivas");
                    return false;
                }
            }

            return true;
        } catch (SQLException e) {
            logger.error("Error al validar integridad: {}", e.getMessage());
            throw new RuntimeException("Error al validar integridad", e);
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