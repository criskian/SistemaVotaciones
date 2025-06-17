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
            String checkSql = "SELECT COUNT(*) FROM candidatos WHERE nombre = ? AND partido = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, nombre);
                checkStmt.setString(2, partido);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new RuntimeException("Ya existe un candidato con el nombre y partido: " + nombre + " - " + partido);
                    }
                }
            }
            String sql = "INSERT INTO candidatos (nombre, partido, propuestas) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nombre);
                pstmt.setString(2, partido);
                pstmt.setString(3, propuestas);
                pstmt.executeUpdate();
            }
            logger.info("Candidato agregado: {} - {}", nombre, partido);
        } catch (SQLException e) {
            logger.error("Error al agregar candidato: {}", e.getMessage());
            throw new RuntimeException("Error al agregar candidato: " + e.getMessage());
        }
    }

    @Override
    public void modificarCandidato(String id, String nombre, String partido, String propuestas, Current current) {
        try (Connection conn = AdminServerMain.getDBConnection()) {
            String checkSql = "SELECT COUNT(*) FROM candidatos WHERE id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, Integer.parseInt(id));
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next() || rs.getInt(1) == 0) {
                        throw new RuntimeException("No existe un candidato con ID: " + id);
                    }
                }
            }
            String sql = "UPDATE candidatos SET nombre = ?, partido = ?, propuestas = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nombre);
                pstmt.setString(2, partido);
                pstmt.setString(3, propuestas);
                pstmt.setInt(4, Integer.parseInt(id));
                pstmt.executeUpdate();
            }
            logger.info("Candidato modificado: {} - {}", nombre, partido);
        } catch (SQLException e) {
            logger.error("Error al modificar candidato: {}", e.getMessage());
            throw new RuntimeException("Error al modificar candidato: " + e.getMessage());
        }
    }

    @Override
    public void eliminarCandidato(String id, Current current) {
        try (Connection conn = AdminServerMain.getDBConnection()) {
            String checkSql = "SELECT COUNT(*) FROM candidatos WHERE id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, Integer.parseInt(id));
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next() || rs.getInt(1) == 0) {
                        throw new RuntimeException("No existe un candidato con ID: " + id);
                    }
                }
            }
            String sql = "DELETE FROM candidatos WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, Integer.parseInt(id));
                pstmt.executeUpdate();
            }
            logger.info("Candidato eliminado con ID: {}", id);
        } catch (SQLException e) {
            logger.error("Error al eliminar candidato: {}", e.getMessage());
            throw new RuntimeException("Error al eliminar candidato: " + e.getMessage());
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
            int totalVotos = 0;
            List<String[]> filas = new ArrayList<>();
            for (String linea : resultados.split("\n")) {
                if (!linea.trim().isEmpty()) {
                    String[] datos = linea.split(",");
                    filas.add(datos);
                    totalVotos += Integer.parseInt(datos[2]);
                }
            }
            for (String[] datos : filas) {
                double porcentaje = totalVotos > 0 ? (100.0 * Integer.parseInt(datos[2]) / totalVotos) : 0.0;
                writer.writeNext(new String[]{datos[0], datos[1], datos[2], String.format("%.2f%%", porcentaje)});
            }
            writer.writeNext(new String[]{"TOTAL", "", String.valueOf(totalVotos), "100%"});
            logger.info("Reporte CSV generado: {}", RESULTADOS_FILE);
        } catch (IOException e) {
            logger.error("Error al generar reporte CSV: {}", e.getMessage());
            throw new RuntimeException("Error al generar reporte CSV", e);
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

    public void importarCandidatosDesdeExcel(String filePath, Current current) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis);
             Connection conn = AdminServerMain.getDBConnection()) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = 0;
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Saltar encabezado
                String nombre = getCellValueAsString(row.getCell(0));
                String partido = getCellValueAsString(row.getCell(1));
                String propuestas = getCellValueAsString(row.getCell(2));
                if (nombre == null || nombre.trim().isEmpty()) continue;
                String checkSql = "SELECT COUNT(*) FROM candidatos WHERE nombre = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, nombre);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) continue;
                    }
                }
                String sql = "INSERT INTO candidatos (nombre, partido, propuestas) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, nombre);
                    pstmt.setString(2, partido);
                    pstmt.setString(3, propuestas);
                    pstmt.executeUpdate();
                    rowCount++;
                }
            }
            logger.info("Candidatos importados desde Excel: {} filas", rowCount);
        } catch (Exception e) {
            logger.error("Error al importar candidatos desde Excel: {}", e.getMessage());
            throw new RuntimeException("Error al importar candidatos desde Excel: " + e.getMessage());
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

    public void generarReporteCSVCompleto(Current current) {
        try (CSVWriter writer = new CSVWriter(new FileWriter("Reporte_Completo.csv"));
             Connection conn = AdminServerMain.getDBConnection()) {
            // 1. Resultados globales
            writer.writeNext(new String[]{"=== RESULTADOS GLOBALES ==="});
            writer.writeNext(new String[]{"Candidato", "Partido", "Votos", "Porcentaje"});
            String sqlGlobal = "SELECT c.nombre, c.partido, COUNT(v.id) as votos FROM votos v JOIN candidatos c ON v.candidato_id = c.id GROUP BY c.nombre, c.partido";
            int totalVotos = 0;
            List<String[]> filas = new ArrayList<>();
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlGlobal)) {
                while (rs.next()) {
                    String[] datos = {rs.getString(1), rs.getString(2), String.valueOf(rs.getInt(3))};
                    filas.add(datos);
                    totalVotos += rs.getInt(3);
                }
            }
            for (String[] datos : filas) {
                double porcentaje = totalVotos > 0 ? (100.0 * Integer.parseInt(datos[2]) / totalVotos) : 0.0;
                writer.writeNext(new String[]{datos[0], datos[1], datos[2], String.format("%.2f%%", porcentaje)});
            }
            writer.writeNext(new String[]{"TOTAL", "", String.valueOf(totalVotos), "100%"});
            writer.writeNext(new String[]{""});
            // 2. Resultados por zona
            writer.writeNext(new String[]{"=== RESULTADOS POR ZONA ==="});
            writer.writeNext(new String[]{"Zona", "Candidato", "Partido", "Votos"});
            String sqlZona = "SELECT z.nombre, c.nombre, c.partido, COUNT(v.id) as votos FROM votos v JOIN candidatos c ON v.candidato_id = c.id JOIN mesas_votacion m ON v.mesa_id = m.id JOIN colegios co ON m.colegio_id = co.id JOIN zonas_electorales z ON co.zona_id = z.id GROUP BY z.nombre, c.nombre, c.partido ORDER BY z.nombre, c.nombre";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlZona)) {
                while (rs.next()) {
                    writer.writeNext(new String[]{rs.getString(1), rs.getString(2), rs.getString(3), String.valueOf(rs.getInt(4))});
                }
            }
            writer.writeNext(new String[]{""});
            // 3. Resultados por mesa
            writer.writeNext(new String[]{"=== RESULTADOS POR MESA ==="});
            writer.writeNext(new String[]{"Mesa", "Candidato", "Partido", "Votos"});
            String sqlMesa = "SELECT m.numero, c.nombre, c.partido, COUNT(v.id) as votos FROM votos v JOIN candidatos c ON v.candidato_id = c.id JOIN mesas_votacion m ON v.mesa_id = m.id GROUP BY m.numero, c.nombre, c.partido ORDER BY m.numero, c.nombre";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlMesa)) {
                while (rs.next()) {
                    writer.writeNext(new String[]{rs.getString(1), rs.getString(2), rs.getString(3), String.valueOf(rs.getInt(4))});
                }
            }
            writer.writeNext(new String[]{""});
            // 4. Resultados por candidato
            writer.writeNext(new String[]{"=== RESULTADOS POR CANDIDATO ==="});
            writer.writeNext(new String[]{"Candidato", "Partido", "Total Votos"});
            String sqlCandidato = "SELECT c.nombre, c.partido, COUNT(v.id) as votos FROM votos v JOIN candidatos c ON v.candidato_id = c.id GROUP BY c.nombre, c.partido ORDER BY c.nombre";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlCandidato)) {
                while (rs.next()) {
                    writer.writeNext(new String[]{rs.getString(1), rs.getString(2), String.valueOf(rs.getInt(3))});
                }
            }
        } catch (Exception e) {
            logger.error("Error al generar reporte CSV completo: {}", e.getMessage());
            throw new RuntimeException("Error al generar reporte CSV completo: " + e.getMessage());
        }
    }

    public String obtenerResultadosPorZona(Current current) {
        StringBuilder resultados = new StringBuilder();
        try (Connection conn = AdminServerMain.getDBConnection()) {
            String sql = "SELECT z.nombre, c.nombre, c.partido, COUNT(v.id) as votos FROM votos v JOIN candidatos c ON v.candidato_id = c.id JOIN mesas_votacion m ON v.mesa_id = m.id JOIN colegios co ON m.colegio_id = co.id JOIN zonas_electorales z ON co.zona_id = z.id GROUP BY z.nombre, c.nombre, c.partido ORDER BY z.nombre, c.nombre";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    resultados.append(String.format("%s,%s,%s,,%d,\n", rs.getString(2), rs.getString(3), rs.getString(1), rs.getInt(4)));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener resultados por zona: {}", e.getMessage());
            throw new RuntimeException("Error al obtener resultados por zona", e);
        }
        return resultados.toString();
    }
} 