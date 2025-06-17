package com.votaciones.admin;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminUI extends Application {
    private static final Logger logger = LoggerFactory.getLogger(AdminUI.class);
    private AdminServerI adminServer;
    private TextArea logArea;
    private TextField nombreField, partidoField, propuestasField, idField, zonaField;

    @Override
    public void start(Stage primaryStage) {
        adminServer = new AdminServerI();
        
        primaryStage.setTitle("Panel de Administración y Reportes - Sistema de Votaciones");
        
        // Crear el layout principal
        BorderPane root = new BorderPane();
        
        // Título principal
        Label titleLabel = new Label("ADMINISTRACIÓN Y REPORTES");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(20));
        
        // Panel de gestión de candidatos
        VBox candidatosPanel = createCandidatosPanel();
        
        // Panel de reportes
        VBox reportesPanel = createReportesPanel();
        
        // Panel de logs
        VBox logsPanel = createLogsPanel();
        
        // Layout principal con pestañas
        TabPane tabPane = new TabPane();
        
        Tab candidatosTab = new Tab("Gestión de Candidatos", candidatosPanel);
        candidatosTab.setClosable(false);
        
        Tab reportesTab = new Tab("Reportes y Resultados", reportesPanel);
        reportesTab.setClosable(false);
        
        Tab logsTab = new Tab("Logs del Sistema", logsPanel);
        logsTab.setClosable(false);
        
        tabPane.getTabs().addAll(candidatosTab, reportesTab, logsTab);
        
        root.setTop(titleLabel);
        root.setCenter(tabPane);
        
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        logMessage("Panel de administración iniciado correctamente");
    }
    
    private VBox createCandidatosPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(20));
        
        Label title = new Label("Gestión de Candidatos");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Campos de entrada
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        nombreField = new TextField();
        partidoField = new TextField();
        propuestasField = new TextField();
        idField = new TextField();
        
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(nombreField, 1, 0);
        grid.add(new Label("Partido:"), 0, 1);
        grid.add(partidoField, 1, 1);
        grid.add(new Label("Propuestas:"), 0, 2);
        grid.add(propuestasField, 1, 2);
        grid.add(new Label("ID (para modificar/eliminar):"), 0, 3);
        grid.add(idField, 1, 3);
        
        // Botones
        HBox buttons = new HBox(10);
        Button agregarBtn = new Button("Agregar Candidato");
        Button modificarBtn = new Button("Modificar Candidato");
        Button eliminarBtn = new Button("Eliminar Candidato");
        Button listarBtn = new Button("Listar Candidatos");
        
        buttons.getChildren().addAll(agregarBtn, modificarBtn, eliminarBtn, listarBtn);
        
        // Área de resultados
        TextArea resultadosArea = new TextArea();
        resultadosArea.setPrefRowCount(10);
        resultadosArea.setEditable(false);
        
        // Eventos
        agregarBtn.setOnAction(e -> {
            try {
                adminServer.agregarCandidato(nombreField.getText(), partidoField.getText(), propuestasField.getText(), null);
                logMessage("Candidato agregado: " + nombreField.getText());
                limpiarCampos();
            } catch (Exception ex) {
                logMessage("Error al agregar candidato: " + ex.getMessage());
            }
        });
        
        modificarBtn.setOnAction(e -> {
            try {
                adminServer.modificarCandidato(idField.getText(), nombreField.getText(), partidoField.getText(), propuestasField.getText(), null);
                logMessage("Candidato modificado: " + nombreField.getText());
                limpiarCampos();
            } catch (Exception ex) {
                logMessage("Error al modificar candidato: " + ex.getMessage());
            }
        });
        
        eliminarBtn.setOnAction(e -> {
            try {
                adminServer.eliminarCandidato(idField.getText(), null);
                logMessage("Candidato eliminado con ID: " + idField.getText());
                limpiarCampos();
            } catch (Exception ex) {
                logMessage("Error al eliminar candidato: " + ex.getMessage());
            }
        });
        
        listarBtn.setOnAction(e -> {
            try {
                String[] candidatos = adminServer.listarCandidatos(null);
                StringBuilder sb = new StringBuilder();
                for (String candidato : candidatos) {
                    sb.append(candidato).append("\n");
                }
                resultadosArea.setText(sb.toString());
                logMessage("Lista de candidatos actualizada");
            } catch (Exception ex) {
                logMessage("Error al listar candidatos: " + ex.getMessage());
            }
        });
        
        panel.getChildren().addAll(title, grid, buttons, new Label("Resultados:"), resultadosArea);
        return panel;
    }
    
    private VBox createReportesPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(20));
        
        Label title = new Label("Reportes y Resultados");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        zonaField = new TextField();
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Código de Zona:"), 0, 0);
        grid.add(zonaField, 1, 0);
        
        // Botones
        HBox buttons = new HBox(10);
        Button procesarBtn = new Button("Procesar Votos");
        Button resultadosZonaBtn = new Button("Resultados por Zona");
        Button resultadosGlobalesBtn = new Button("Resultados Globales");
        Button csvBtn = new Button("Generar CSV");
        Button excelBtn = new Button("Exportar Excel");
        
        buttons.getChildren().addAll(procesarBtn, resultadosZonaBtn, resultadosGlobalesBtn, csvBtn, excelBtn);
        
        // Área de resultados
        TextArea resultadosArea = new TextArea();
        resultadosArea.setPrefRowCount(15);
        resultadosArea.setEditable(false);
        
        // Eventos
        procesarBtn.setOnAction(e -> {
            try {
                adminServer.procesarVotos(zonaField.getText(), null);
                logMessage("Votos procesados para zona: " + zonaField.getText());
            } catch (Exception ex) {
                logMessage("Error al procesar votos: " + ex.getMessage());
            }
        });
        
        resultadosZonaBtn.setOnAction(e -> {
            try {
                String resultados = adminServer.obtenerResultadosZona(zonaField.getText(), null);
                resultadosArea.setText(resultados);
                logMessage("Resultados obtenidos para zona: " + zonaField.getText());
            } catch (Exception ex) {
                logMessage("Error al obtener resultados: " + ex.getMessage());
            }
        });
        
        resultadosGlobalesBtn.setOnAction(e -> {
            try {
                String resultados = adminServer.obtenerResultadosGlobales(null);
                resultadosArea.setText(resultados);
                logMessage("Resultados globales obtenidos");
            } catch (Exception ex) {
                logMessage("Error al obtener resultados globales: " + ex.getMessage());
            }
        });
        
        csvBtn.setOnAction(e -> {
            try {
                adminServer.generarReporteCSV("GLOBAL", null);
                logMessage("Reporte CSV generado exitosamente");
            } catch (Exception ex) {
                logMessage("Error al generar CSV: " + ex.getMessage());
            }
        });
        
        excelBtn.setOnAction(e -> {
            try {
                adminServer.exportarResultadosExcel(null);
                logMessage("Resultados exportados a Excel exitosamente");
            } catch (Exception ex) {
                logMessage("Error al exportar Excel: " + ex.getMessage());
            }
        });
        
        panel.getChildren().addAll(title, grid, buttons, new Label("Resultados:"), resultadosArea);
        return panel;
    }
    
    private VBox createLogsPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(20));
        
        Label title = new Label("Logs del Sistema");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Área de logs
        logArea = new TextArea();
        logArea.setPrefRowCount(20);
        logArea.setEditable(false);
        
        // Botones
        HBox buttons = new HBox(10);
        Button limpiarBtn = new Button("Limpiar Logs");
        Button validarBtn = new Button("Validar Integridad");
        
        buttons.getChildren().addAll(limpiarBtn, validarBtn);
        
        // Eventos
        limpiarBtn.setOnAction(e -> {
            logArea.clear();
            logMessage("Logs limpiados");
        });
        
        validarBtn.setOnAction(e -> {
            try {
                boolean valido = adminServer.validarIntegridadResultados(null);
                if (valido) {
                    logMessage("✅ Validación de integridad: EXITOSA");
                } else {
                    logMessage("❌ Validación de integridad: FALLIDA");
                }
            } catch (Exception ex) {
                logMessage("Error en validación: " + ex.getMessage());
            }
        });
        
        panel.getChildren().addAll(title, logArea, buttons);
        return panel;
    }
    
    private void logMessage(String message) {
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        String logEntry = "[" + timestamp + "] " + message + "\n";
        logArea.appendText(logEntry);
        logger.info(message);
    }
    
    private void limpiarCampos() {
        nombreField.clear();
        partidoField.clear();
        propuestasField.clear();
        idField.clear();
        zonaField.clear();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
} 