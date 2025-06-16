package VotingSystem;

import com.zeroc.Ice.Current;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.List;
import java.io.InputStream;
import java.util.Properties;

public class ProxyCacheDBCiudadI implements ProxyCacheDBCiudad {
    private final DataCacheProxy dataCacheProxy;

    public ProxyCacheDBCiudadI() {
        try {
            // Cargar configuración desde el classpath
            Properties props = new Properties();
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    throw new RuntimeException("No se pudo encontrar config.properties");
                }
                props.load(input);
            }

            // Configuración de HikariCP
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.user"));
            config.setPassword(props.getProperty("db.password"));
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("maximumPoolSize", "10")));
            config.setMinimumIdle(Integer.parseInt(props.getProperty("minimumIdle", "5")));
            config.setIdleTimeout(Long.parseLong(props.getProperty("idleTimeout", "300000")));
            config.setConnectionTimeout(Long.parseLong(props.getProperty("connectionTimeout", "20000")));

            HikariDataSource ds = new HikariDataSource(config);
            this.dataCacheProxy = new DataCacheProxy(ds);
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar el proxy de base de datos", e);
        }
    }

    @Override
    public Votante ConsultarVotantePorCedula(String cedula, Current current) {
        Votante v = dataCacheProxy.consultarVotantePorCedula(cedula);
        if (v == null) {
            // Puedes lanzar una excepción o devolver un objeto vacío
            return new Votante("", "No encontrado", "", 0, 0, 0);
        }
        return v;
    }

    @Override
    public Candidato[] ConsultarCandidatos(Current current) {
        List<Candidato> lista = dataCacheProxy.consultarCandidatos();
        return lista.toArray(new Candidato[0]);
    }

    @Override
    public Zona[] GetZonasVotacion(Current current) {
        List<Zona> lista = dataCacheProxy.getZonasVotacion();
        return lista.toArray(new Zona[0]);
    }

    @Override
    public Zona ZonaMesaAsignada(String cedula, Current current) {
        Zona z = dataCacheProxy.zonaMesaAsignada(cedula);
        if (z == null) {
            return new Zona(0, "No asignada", "");
        }
        return z;
    }

    @Override
    public int IDZonaVotacion(String cedula, Current current) {
        return dataCacheProxy.idZonaVotacion(cedula);
    }

    @Override
    public int GetConteoVotos(int mesaId, Current current) {
        return dataCacheProxy.getConteoVotos(mesaId);
    }

    @Override
    public boolean AgregarVoto(Voto voto, Current current) {
        return dataCacheProxy.agregarVoto(voto);
    }

    @Override
    public boolean AgregarSospechoso(String cedula, String motivo, Current current) {
        return dataCacheProxy.agregarSospechoso(cedula, motivo);
    }

    @Override
    public boolean RegistrarLogs(LogEntry log, Current current) {
        return dataCacheProxy.registrarLogs(log);
    }
} 