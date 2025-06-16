package com.votaciones.estacion.ui;

import com.votaciones.estacion.GestionMesasProxy;
import com.votaciones.estacion.lotes.GestorLotes;
import com.votaciones.estacion.MesaVotacion;
import VotingSystem.Voto;
import VotingSystem.Candidato;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;

public class EstacionVotacionUI extends JFrame {
    private static final int MESA_ID = 1; // ID de la mesa por defecto
    private static final List<String> CANDIDATOS = Arrays.asList("Candidato A", "Candidato B", "Candidato C");
    
    // Componentes UI
    private JTable votosTable;
    private DefaultTableModel tableModel;
    private JTextArea logArea;
    private JLabel estadoLabel;
    private JLabel horaLabel;
    private JLabel mesaIdLabel;
    private JProgressBar estadoBar;
    private JButton registrarVotoBtn;
    private JButton enviarLotesBtn;
    private JButton mostrarEstadisticasBtn;
    
    // Control de aplicación
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final GestionMesasProxy proxy;
    private final GestorLotes gestorLotes;
    private final MesaVotacion mesaVotacion;
    private boolean sistemaActivo = false;

    private static String zonaVotacion = "";
    private static int numeroMesa = 1;

    private int zonaIdColegio = -1;
    private int mesaId = -1;
    private int colegioId = -1;

    private static int puerto = 10010;
    private static int idMesaConfig = 1;
    private static int idZonaConfig = 1;
    private static String colegioConfig = "";
    private static int numeroMesaConfig = 1;

    static {
        try (InputStream input = EstacionVotacionUI.class.getClassLoader().getResourceAsStream("mesa.properties")) {
            Properties prop = new Properties();
            if (input != null) {
                prop.load(input);
                puerto = Integer.parseInt(prop.getProperty("puerto", "10010"));
                idMesaConfig = Integer.parseInt(prop.getProperty("id_mesa", "1"));
                idZonaConfig = Integer.parseInt(prop.getProperty("id_zona", "1"));
            }
        } catch (Exception e) {
            System.err.println("No se pudo leer mesa.properties: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("[DEBUG] Argumentos recibidos: " + java.util.Arrays.toString(args));
        if (args.length < 2) {
            JOptionPane.showMessageDialog(null, "Debes indicar el nombre del colegio y el número de mesa como argumentos.\nEjemplo: \"Colegio San José\" 101", "Faltan argumentos", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        colegioConfig = args[0];
        try {
            numeroMesaConfig = Integer.parseInt(args[1].trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El número de mesa debe ser un número entero.\nArgumento recibido: '" + args[1] + "'", "Argumento inválido", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        // Leer mesas.properties
        Properties prop = new Properties();
        String key = colegioConfig + "|" + numeroMesaConfig;
        try {
            // Solo buscar en el directorio de trabajo
            java.io.File f = new java.io.File("mesas.properties");
            if (!f.exists()) {
                JOptionPane.showMessageDialog(null, "No se encontró el archivo mesas.properties en el directorio de trabajo: " + f.getAbsolutePath(), "Archivo no encontrado", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            System.out.println("[DEBUG] Leyendo mesas.properties desde: " + f.getAbsolutePath());
            try (InputStream input = new FileInputStream(f)) {
                prop.load(input);
            }
            String value = prop.getProperty(key);
            if (value == null) {
                System.out.println("[DEBUG] Claves disponibles en mesas.properties:");
                for (Object k : prop.keySet()) {
                    System.out.println("  - '" + k + "'");
                }
                JOptionPane.showMessageDialog(null, "No se encontró configuración para: " + key + "\nRevisa la consola para ver las claves disponibles.", "Configuración no encontrada", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            String[] parts = value.split(",");
            if (parts.length != 3) {
                JOptionPane.showMessageDialog(null, "Configuración inválida para: " + key, "Error de configuración", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            idMesaConfig = Integer.parseInt(parts[0].trim());
            puerto = Integer.parseInt(parts[1].trim());
            idZonaConfig = Integer.parseInt(parts[2].trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error leyendo mesas.properties: " + e.getMessage(), "Error de configuración", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error al establecer Look and Feel: " + e.getMessage());
        }
        SwingUtilities.invokeLater(() -> {
            try {
                EstacionVotacionUI app = new EstacionVotacionUI();
                app.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Error al iniciar la aplicación:\n" + e.getMessage(),
                    "Error Crítico",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }

    public EstacionVotacionUI() {
        super("Estación de Votación Local - Mesa: " + idMesaConfig + " | Zona: " + idZonaConfig + " | Colegio: " + colegioConfig + " | Número: " + numeroMesaConfig);
        if (!validarColegioYMesaPorId(idMesaConfig, idZonaConfig)) {
            JOptionPane.showMessageDialog(null,
                "La mesa o la zona no existen o no están relacionadas correctamente en la base de datos central.",
                "Error de configuración",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        activarMesaEnBD();
        
        // Inicializar componentes
        proxy = new GestionMesasProxy();
        gestorLotes = new GestorLotes();
        mesaVotacion = new MesaVotacion(numeroMesaConfig, CANDIDATOS);
        
        initializeUI();
        startServices();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                desactivarMesaEnBD();
                shutdown();
            }
        });
        
        mostrarMensajeInicio();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        add(createPanelSuperior(), BorderLayout.NORTH);
        add(createPanelCentral(), BorderLayout.CENTER);
        add(createPanelInferior(), BorderLayout.SOUTH);
        
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Configurar ventana como aplicación independiente
        setAlwaysOnTop(true);
        toFront();
        requestFocus();
        
        // Después de 2 segundos, quitar always on top
        Timer timer = new Timer(2000, e -> setAlwaysOnTop(false));
        timer.setRepeats(false);
        timer.start();
    }

    private JPanel createPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(15, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Estación de Votación Local"));
        panel.setBackground(new Color(240, 248, 255));
        
        // Panel de estado
        JPanel statusPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        statusPanel.setBorder(BorderFactory.createTitledBorder("Estado del Sistema"));
        
        mesaIdLabel = new JLabel("Mesa ID: " + numeroMesaConfig);
        JLabel zonaLabel = new JLabel("Zona: " + colegioConfig);
        estadoLabel = new JLabel("Estado: CONECTADO");
        horaLabel = new JLabel("Hora: --:--:--");
        
        Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 14);
        mesaIdLabel.setFont(labelFont);
        zonaLabel.setFont(labelFont);
        estadoLabel.setFont(labelFont);
        horaLabel.setFont(labelFont);
        
        estadoLabel.setForeground(new Color(0, 100, 0));
        
        statusPanel.add(mesaIdLabel);
        statusPanel.add(zonaLabel);
        statusPanel.add(estadoLabel);
        statusPanel.add(horaLabel);
        
        // Panel de controles
        JPanel controlsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Controles"));
        
        registrarVotoBtn = createStyledButton("Registrar Voto", "[1]");
        enviarLotesBtn = createStyledButton("Enviar Lotes Pendientes", "[2]");
        mostrarEstadisticasBtn = createStyledButton("Mostrar Estadísticas", "[3]");
        
        registrarVotoBtn.addActionListener(e -> registrarVoto());
        enviarLotesBtn.addActionListener(e -> enviarLotesPendientes());
        mostrarEstadisticasBtn.addActionListener(e -> mostrarEstadisticas());
        
        controlsPanel.add(registrarVotoBtn);
        controlsPanel.add(enviarLotesBtn);
        controlsPanel.add(mostrarEstadisticasBtn);
        
        panel.add(statusPanel, BorderLayout.WEST);
        panel.add(controlsPanel, BorderLayout.EAST);
        
        return panel;
    }

    private JButton createStyledButton(String text, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        button.setPreferredSize(new Dimension(200, 40));
        return button;
    }

    private JPanel createPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Registro de Votos"));
        
        String[] columnas = {
            "ID Voto", "Cédula", "Candidato", "Hora", "Estado"
        };
        
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        votosTable = new JTable(tableModel);
        votosTable.setFillsViewportHeight(true);
        votosTable.setRowHeight(25);
        
        // Configurar renderizador para filas alternadas
        votosTable.setDefaultRenderer(Object.class, new AlternatingRowRenderer());
        
        JScrollPane scrollPane = new JScrollPane(votosTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Log del Sistema"));
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(800, 150));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private void registrarVoto() {
        String cedula = JOptionPane.showInputDialog(this, "Ingrese la cédula del votante:");
        if (cedula == null || cedula.trim().isEmpty()) {
            return;
        }

        // Validar que la cédula esté asignada a la zona del colegio
        if (!validarCiudadanoEnZona(cedula, zonaIdColegio)) {
            JOptionPane.showMessageDialog(this,
                "El ciudadano no está habilitado para votar en la zona de este colegio.",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar contra el sistema central (por si ya votó)
        boolean puedeVotar = proxy.verificarEstadoZona(cedula, String.valueOf(zonaIdColegio));
        if (!puedeVotar) {
            JOptionPane.showMessageDialog(this,
                "El votante no puede votar (ya votó o no está habilitado)",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Registrar el voto en el lote local, incluyendo zona y mesa
        String candidato = seleccionarCandidato();
        if (candidato == null) return;

        Voto voto = new Voto();
        voto.idVotante = cedula;
        voto.idCandidato = Integer.parseInt(candidato);
        
        // Registrar el voto en el sistema central primero
        boolean registroExitoso = proxy.registrarVoto(cedula, voto.idCandidato, idMesaConfig, String.valueOf(zonaIdColegio));
        if (!registroExitoso) {
            JOptionPane.showMessageDialog(this,
                "Error al registrar el voto en el sistema central.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Si el registro fue exitoso, agregar al lote local
        gestorLotes.agregarVoto(voto);
        actualizarTablaVotos();
        JOptionPane.showMessageDialog(this, "Voto registrado correctamente.");
    }

    private boolean validarCiudadanoEnZona(String cedula, int zonaId) {
        try {
            System.out.println("[DEBUG] Validando ciudadano: cédula=" + cedula + ", zona_id=" + zonaId);
            String url = "jdbc:postgresql://localhost:5432/sistema_votaciones";
            String user = "postgres";
            String pass = "postgres";
            try (java.sql.Connection conn = java.sql.DriverManager.getConnection(url, user, pass)) {
                String sql = "SELECT c.id FROM ciudadanos c JOIN asignaciones_ciudadanos a ON c.id = a.ciudadano_id WHERE c.documento = ? AND a.zona_id = ? AND a.estado = 'ACTIVA'";
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, cedula);
                    stmt.setInt(2, zonaId);
                    java.sql.ResultSet rs = stmt.executeQuery();
                    boolean found = rs.next();
                    System.out.println("[DEBUG] Resultado validación: " + found);
                    return found;
                }
            }
        } catch (Exception e) {
            System.err.println("Error validando ciudadano en zona: " + e.getMessage());
            return false;
        }
    }

    private void enviarLotesPendientes() {
        try {
            // Aquí deberías recolectar los votos pendientes y enviarlos usando el nuevo método
            gestorLotes.enviarLotesPendientes(proxy, numeroMesaConfig, colegioConfig);
            agregarLog("Lotes pendientes enviados correctamente");
        } catch (Exception e) {
            agregarLog("Error al enviar lotes: " + e.getMessage());
        }
    }

    private void mostrarEstadisticas() {
        StringBuilder stats = new StringBuilder();
        stats.append("Estadísticas de la Mesa ").append(numeroMesaConfig).append("\n\n");
        
        // Mostrar votos por candidato
        stats.append("Votos por candidato:\n");
        mesaVotacion.getResumenVotos().forEach((candidato, votos) -> 
            stats.append(String.format("- %s: %d votos\n", candidato, votos))
        );
        
        JOptionPane.showMessageDialog(this,
            stats.toString(),
            "Estadísticas",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void actualizarTablaVotos() {
        // Implementar la actualización de la tabla con los votos registrados
        // Esto dependerá de cómo esté implementada la clase MesaVotacion
    }

    private void agregarLog(String mensaje) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        logArea.append(String.format("[%s] %s%n", timestamp, mensaje));
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void mostrarMensajeInicio() {
        agregarLog("Sistema iniciado - Zona: " + colegioConfig + " | Mesa: " + numeroMesaConfig);
        agregarLog("Conectado al servidor de gestión de mesas");
    }

    private void startServices() {
        // Programar actualización de hora
        scheduler.scheduleAtFixedRate(this::actualizarHora, 0, 1, TimeUnit.SECONDS);
        
        // Programar verificación de estado
        scheduler.scheduleAtFixedRate(this::verificarEstado, 0, 5, TimeUnit.SECONDS);
    }

    private void actualizarHora() {
        SwingUtilities.invokeLater(() -> {
            String hora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            horaLabel.setText("Hora: " + hora);
        });
    }

    private void verificarEstado() {
        // Implementar verificación de estado del sistema
    }

    private void shutdown() {
        try {
            scheduler.shutdown();
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            proxy.close();
            agregarLog("Sistema cerrado correctamente");
        } catch (Exception e) {
            agregarLog("Error al cerrar el sistema: " + e.getMessage());
        }
    }

    private static class AlternatingRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
            }
            return c;
        }
    }

    private boolean validarColegioYMesaPorId(int idMesa, int idZonaConfig) {
        try {
            String url = "jdbc:postgresql://localhost:5432/sistema_votaciones";
            String user = "postgres";
            String pass = "postgres";
            try (java.sql.Connection conn = java.sql.DriverManager.getConnection(url, user, pass)) {
                // Validar mesa y obtener colegio_id
                String sqlMesa = "SELECT colegio_id FROM mesas_votacion WHERE id = ?";
                int colegioIdLocal = -1;
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sqlMesa)) {
                    stmt.setInt(1, idMesa);
                    java.sql.ResultSet rs = stmt.executeQuery();
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(null,
                            "No se encontró la mesa con id: '" + idMesa + "' en la base de datos.",
                            "Error de mesa",
                            JOptionPane.ERROR_MESSAGE);
                        System.out.println("Mesa no encontrada: id=" + idMesa);
                        return false;
                    }
                    colegioIdLocal = rs.getInt("colegio_id");
                    System.out.println("Mesa encontrada: id=" + idMesa + ", colegio_id=" + colegioIdLocal);
                }
                // Validar que el colegio pertenezca a la zona y obtener zona_id real
                String sqlColegio = "SELECT zona_id FROM colegios WHERE id = ?";
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sqlColegio)) {
                    stmt.setInt(1, colegioIdLocal);
                    java.sql.ResultSet rs = stmt.executeQuery();
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(null,
                            "No se encontró el colegio con id: '" + colegioIdLocal + "' en la base de datos.",
                            "Error de colegio",
                            JOptionPane.ERROR_MESSAGE);
                        System.out.println("Colegio no encontrado: id=" + colegioIdLocal);
                        return false;
                    }
                    int zonaIdBD = rs.getInt("zona_id");
                    zonaIdColegio = zonaIdBD; // Asignar el zona_id real para validación
                    if (zonaIdBD != idZonaConfig) {
                        JOptionPane.showMessageDialog(null,
                            "La zona de la mesa no coincide con la zona configurada.",
                            "Error de zona",
                            JOptionPane.ERROR_MESSAGE);
                        System.out.println("Zona de colegio: " + zonaIdBD + " vs zona configurada: " + idZonaConfig);
                        return false;
                    }
                    System.out.println("Colegio encontrado: id=" + colegioIdLocal + ", zona_id=" + zonaIdBD);
                }
                return true;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Error validando mesa y zona: " + e.getMessage(),
                "Error de validación",
                JOptionPane.ERROR_MESSAGE);
            System.err.println("Error validando mesa y zona: " + e.getMessage());
            return false;
        }
    }

    private String seleccionarCandidato() {
        try {
            java.util.List<Candidato> candidatos = proxy.listarCandidatos();
            if (candidatos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay candidatos disponibles.", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            String[] nombres = candidatos.stream().map(c -> c.nombre + " (" + c.partido + ")").toArray(String[]::new);
            String seleccion = (String) JOptionPane.showInputDialog(this, "Seleccione el candidato:", "Registro de Voto", JOptionPane.QUESTION_MESSAGE, null, nombres, nombres[0]);
            if (seleccion == null) return null;
            // Buscar el candidato seleccionado
            for (Candidato c : candidatos) {
                if (seleccion.startsWith(c.nombre)) {
                    return String.valueOf(c.id);
                }
            }
            return null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al obtener candidatos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void activarMesaEnBD() {
        try {
            String url = "jdbc:postgresql://localhost:5432/sistema_votaciones";
            String user = "postgres";
            String pass = "postgres";
            try (java.sql.Connection conn = java.sql.DriverManager.getConnection(url, user, pass)) {
                String sql = "UPDATE mesas_votacion SET estado = 'ACTIVA', ultima_actualizacion = NOW() WHERE id = ?";
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, idMesaConfig);
                    int updated = stmt.executeUpdate();
                    System.out.println("[DEBUG] Mesa " + idMesaConfig + " activada. Filas actualizadas: " + updated);
                }
            }
        } catch (Exception e) {
            System.err.println("Error activando mesa en BD: " + e.getMessage());
        }
    }

    private void desactivarMesaEnBD() {
        try {
            String url = "jdbc:postgresql://localhost:5432/sistema_votaciones";
            String user = "postgres";
            String pass = "postgres";
            try (java.sql.Connection conn = java.sql.DriverManager.getConnection(url, user, pass)) {
                String sql = "UPDATE mesas_votacion SET estado = 'INACTIVA', ultima_actualizacion = NOW() WHERE id = ?";
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, idMesaConfig);
                    int updated = stmt.executeUpdate();
                    System.out.println("[DEBUG] Mesa " + idMesaConfig + " desactivada. Filas actualizadas: " + updated);
                }
            }
        } catch (Exception e) {
            System.err.println("Error desactivando mesa en BD: " + e.getMessage());
        }
    }
} 