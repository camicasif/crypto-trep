package edu.upb.crypto.trep.DataBase;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.upb.crypto.trep.DataBase.models.Candidato;
import edu.upb.crypto.trep.DataBase.models.Votante;
import lombok.extern.slf4j.Slf4j;
import edu.upb.crypto.trep.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Functions {
    private static final int MAX_ROWS_PER_BLOQUE = 5;

    public static void initializer(){
        createVotanteTable();
        createCandidatosTable();
        createInitialBloqueTable();
        createVotoTable();
    }


    // Create Votante table
    public static void createVotanteTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Votante (" +
                "codigo TEXT PRIMARY KEY, " +
                "llave_privada TEXT)";

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.execute();
            log.info("Created table: Votante");
        } catch (SQLException e) {
            log.error("Error creating table Votante", e);
        }
    }

    public static boolean isVotoValido(String codigoVotante, String codigoCandidato) {
        // Verificar si el votante existe
        if (!votanteExists(codigoVotante)) {
            log.error("El votante con código " + codigoVotante + " no existe.");
            return false;
        }

        // Verificar si el candidato existe
        if (!candidatoExists(codigoCandidato)) {
            log.error("El candidato con ID " + codigoCandidato + " no existe.");
            return false;
        }

        // Si ambos existen, el voto es válido
        log.info("El voto es válido: votante=" + codigoVotante + ", candidato=" + codigoCandidato);
        return true;
    }

    public static boolean insertVoto(String id, String codigoVotante, String codigoCandidato, long timestamp) {
        // Verificar si el votante existe
        if (!votanteExists(codigoVotante)) {
            log.error("El votante con código " + codigoVotante + " no existe.");
            return false;
        }

        // Verificar si el candidato existe
        if (!candidatoExists(codigoCandidato)) {
            log.error("El candidato con ID " + codigoCandidato + " no existe.");
            return false;
        }

        // Si ambos existen, proceder con la inserción
        String sql = "INSERT INTO Voto (id, codigo_votante, codigo_candidato, timestamp) VALUES (?, ?, ?, ?)";

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            // Asignar los valores a los parámetros de la consulta
            statement.setString(1, id);
            statement.setString(2, codigoVotante);
            statement.setString(3, codigoCandidato);
            statement.setLong(4, timestamp);

            // Ejecutar la inserción
            int rowsInserted = statement.executeUpdate();

            // Verificar si la inserción fue exitosa
            if (rowsInserted > 0) {
                log.info("Se insertó un nuevo voto correctamente.");
                return true;
            } else {
                log.warn("No se pudo insertar el voto.");
                return false;
            }
        } catch (SQLException e) {
            log.error("Error al insertar en la tabla Voto", e);
            return false;
        }
    }

    // Método para verificar si un votante existe
    private static boolean votanteExists(String codigoVotante) {
        String sql = "SELECT codigo FROM Votante WHERE codigo = ?";

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, codigoVotante);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next(); // Retorna true si el votante existe
            }
        } catch (SQLException e) {
            log.error("Error verificando existencia del votante", e);
            return false;
        }
    }

    // Método para verificar si un candidato existe
    private static boolean candidatoExists(String codigoCandidato) {
        String sql = "SELECT id FROM Candidatos WHERE id = ?";

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, codigoCandidato);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next(); // Retorna true si el candidato existe
            }
        } catch (SQLException e) {
            log.error("Error verificando existencia del candidato", e);
            return false;
        }
    }

    public static void createVotoTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Voto (" +
                "id TEXT PRIMARY KEY, " +
                "codigo_votante TEXT, " +
                "codigo_candidato TEXT, " +
                "timestamp BIGINT)";  // Usamos BIGINT para almacenar el timestamp

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.execute();
            log.info("Created table: Voto");  // Corregido el nombre de la tabla en el log
        } catch (SQLException e) {
            log.error("Error creating table Voto", e);  // Corregido el nombre de la tabla en el log
        }
    }

    public static int getCantidadVotos(String idCandidato) {
        log.info("Obteniendo cantidad de votos para el candidato con ID: {}", idCandidato);

        String sql = "SELECT COUNT(*) FROM Votos WHERE id_candidato = ?";
        int cantidadVotos = 0;

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, idCandidato);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    cantidadVotos = rs.getInt(1);
                }
            }

            log.info("Cantidad de votos para el candidato {}: {}", idCandidato, cantidadVotos);

        } catch (SQLException e) {
            log.error("Error al obtener la cantidad de votos. Error SQL: {}", e.getSQLState(), e);
        }

        return cantidadVotos;
    }


    // Insert data into Votante table
    public static String insertVotante(String codigo, String llavePrivada) {
        log.info("Insertando/actualizando votante - Código: {}, Llave: {}", codigo, llavePrivada);

        // Usamos INSERT OR REPLACE para sobrescribir si existe
        String sql = "INSERT OR REPLACE INTO Votante (codigo, llave_privada) VALUES (?, ?)";

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, codigo);
            statement.setString(2, llavePrivada);

            int affectedRows = statement.executeUpdate();
            log.info("Filas afectadas: {}", affectedRows);

            return llavePrivada;
        } catch (SQLException e) {
            log.error("Error al insertar/actualizar votante. Error SQL: {}", e.getSQLState(), e);
            return null;
        }
    }

    public static boolean deleteVotante(String codigo) {
        System.out.println("borrarVotante");
        String sql = "DELETE FROM Votante WHERE codigo = ?";

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, codigo);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                log.info("Votante eliminado correctamente: Código=" + codigo);
                return true;
            } else {
                log.warn("No se encontró el votante con Código=" + codigo);
                return false;
            }
        } catch (SQLException e) {
            log.error("Error eliminando votante", e);
            return false;
        }
    }



    public static void createCandidatosTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Candidatos (" +
                "id TEXT PRIMARY KEY, " +
                "nombre TEXT)";

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.execute();
            log.info("Created table: Candidatos");
        } catch (SQLException e) {
            log.error("Error creating table Candidatos", e);
        }
    }

    public static void insertCandidato(String id, String nombre) {
        log.info("Insertando/actualizando candidato - ID: {}, Nombre: {}", id, nombre);

        // Versión con INSERT OR REPLACE (SQLite)
        String sql = "INSERT OR REPLACE INTO Candidatos (id, nombre) VALUES (?, ?)";

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, id);
            statement.setString(2, nombre);

            int affectedRows = statement.executeUpdate();
            log.info("Filas afectadas en Candidatos: {}", affectedRows);

        } catch (SQLException e) {
            log.error("Error al insertar/actualizar candidato. Error SQL: {}", e.getSQLState(), e);
        }
    }

    public static boolean deleteCandidato(String id) {
        System.out.println("BorrarCandidato");
        String sql = "DELETE FROM Candidatos WHERE id = ?";

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, id);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                log.info("Candidato eliminado correctamente: ID=" + id);
                return true;
            } else {
                log.warn("No se encontró el candidato con ID=" + id);
                return false;
            }
        } catch (SQLException e) {
            log.error("Error eliminando candidato", e);
            return false;
        }
    }


    public static void createInitialBloqueTable() {
        String nextBlock = "Bloque_0001";

        String sql = "CREATE TABLE IF NOT EXISTS " + nextBlock + " (" +
                "id TEXT PRIMARY KEY, " +
                "codigo_votante TEXT, " +
                "codigo_candidato TEXT, " +
                "hash TEXT, " +
                "ref_anterior_bloque TEXT)";

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.execute();
            log.info("Created table: " + nextBlock);
        } catch (SQLException e) {
            log.error("Error creating table " + nextBlock, e);
        }
    }

    public static void createBloqueTable() {
        String latestBlock = getLatestBlockTable();

        if (!isTableFull(latestBlock)) {
            log.info("Current " + latestBlock +" table is not full. No new table created.");
            return;
        }

        String nextBlock = getNextBlockName(latestBlock);

        String sql = "CREATE TABLE IF NOT EXISTS " + nextBlock + " (" +
                "id TEXT PRIMARY KEY, " +
                "codigo_votante TEXT, " +
                "codigo_candidato TEXT, " +
                "hash TEXT, " +
                "ref_anterior_bloque TEXT)";

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.execute();
            log.info("Created table: " + nextBlock);
        } catch (SQLException e) {
            log.error("Error creating table " + nextBlock, e);
        }
    }

    public static void insertBloqueData(String id, String codigoVotante, String codigoCandidato) {
        String tableName = getLatestBlockTable();

        if (isTableFull(tableName)) {
            createBloqueTable();
            tableName = getLatestBlockTable(); // Update to the new table name
        }

        String hash = Utils.getSHA256(id + codigoVotante + codigoCandidato);

        String refAnteriorBloque = getPreviousBlockHash(tableName);

        String sql = "INSERT INTO " + tableName + " (id, codigo_votante, codigo_candidato, hash, ref_anterior_bloque) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.setString(2, codigoVotante);
            statement.setString(3, codigoCandidato);
            statement.setString(4, hash);
            statement.setString(5, refAnteriorBloque);
            statement.executeUpdate();
            log.info("Inserted data into " + tableName);
        } catch (SQLException e) {
            log.error("Error inserting data into " + tableName, e);
        }
    }

    public static boolean deleteBloque(String tableName) {
        if (!tableName.matches("Bloque_\\d{4}")) {
            log.error("Nombre de tabla de bloque no válido: " + tableName);
            return false;
        }

        String sql = "DROP TABLE IF EXISTS " + tableName;

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.executeUpdate();
            log.info("Bloque eliminado correctamente: " + tableName);
            return true;
        } catch (SQLException e) {
            log.error("Error eliminando bloque", e);
            return false;
        }
    }

    private static boolean isTableFull(String tableName) {
        String sql = "SELECT COUNT(*) AS row_count FROM " + tableName;

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            if (rs.next()) {
                int rowCount = rs.getInt("row_count");
                return rowCount >= MAX_ROWS_PER_BLOQUE;
            }
        } catch (SQLException e) {
            log.error("Error checking table row count: ", e);
        }

        return false;
    }

    private static String getPreviousBlockHash(String currentTableName) {
        String previousTableName = getPreviousTableName(currentTableName);
        if (previousTableName == null) {
            return "";
        }

        String query = "SELECT hash FROM " + previousTableName + " ORDER BY id DESC LIMIT 1";

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            if (rs.next()) {
                return rs.getString("hash");
            }
        } catch (SQLException e) {
            log.error("Error retrieving previous block hash: ", e);
        }

        return "";
    }

    private static String getPreviousTableName(String currentTableName) {
        try {
            int currentBlockNumber = Integer.parseInt(currentTableName.replace("Bloque_", ""));
            if (currentBlockNumber <= 1) {
                return null;
            }
            return String.format("Bloque_%04d", currentBlockNumber - 1);
        } catch (NumberFormatException e) {
            log.error("Invalid table name format: " + currentTableName, e);
            return null;
        }
    }

    private static String getLatestBlockTable() {
        String latestBlock = "Bloque_0001"; // Default if none exist
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name LIKE 'Bloque_%' ORDER BY name DESC LIMIT 1";

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                latestBlock = resultSet.getString("name");
            }
        } catch (SQLException e) {
            log.error("Error retrieving latest block table", e);
        }

        return latestBlock;
    }

    private static String getNextBlockName(String latestBlock) {
        int lastNumber = Integer.parseInt(latestBlock.replace("Bloque_", ""));
        return String.format("Bloque_%04d", lastNumber + 1);
    }

    public static String getConcatenatedTableData(String tableName) {
        StringBuilder concatenatedData = new StringBuilder();
        String query = "SELECT id, codigo_votante, codigo_candidato, hash, ref_anterior_bloque FROM " + tableName;

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                concatenatedData.append(rs.getString("id"))
                        .append(rs.getString("codigo_votante"))
                        .append(rs.getString("codigo_candidato"))
                        .append(rs.getString("hash"))
                        .append(rs.getString("ref_anterior_bloque"));
            }
        } catch (SQLException e) {
            log.error("Error retrieving table data: ", e);
        }

        return concatenatedData.toString();
    }

    public static String getLlavePrivada(String codigoVotante) {
        String sql = "SELECT llave_privada FROM Votante WHERE codigo = ?";
        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, codigoVotante);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("llave_privada");
            }
        } catch (SQLException e) {
            log.error("Error retrieving llave_privada", e);
        }
        return null;
    }


    public static List<Votante> getAllVotantes() {
        System.out.println("ListaVotantes");
        List<Votante> votantes = new ArrayList<>();
        String sql = "SELECT codigo, llave_privada FROM Votante";

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                String codigo = rs.getString("codigo");
                String llavePrivada = rs.getString("llave_privada");
                Votante votante = new Votante(codigo, llavePrivada);
                votantes.add(votante);
            }
        } catch (SQLException e) {
            log.error("Error retrieving Votantes", e);
        }

        return votantes;
    }

    public static List<Candidato> getAllCandidatos() {
        System.out.println("ListaCandidatos");
        List<Candidato> candidatos = new ArrayList<>();
        String sql = "SELECT id, nombre FROM Candidatos";

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                String nombre = rs.getString("nombre");
                Candidato candidato = new Candidato(id, nombre);
                candidatos.add(candidato);
            }
        } catch (SQLException e) {
            log.error("Error retrieving Candidatos", e);
        }

        return candidatos;
    }

    public static JsonArray getAllBloques() {
        System.out.println("getAllBloques");
        JsonArray bloques = new JsonArray();
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name LIKE 'Bloque_%'";

        try (Connection con = DataBase.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                String tableName = rs.getString("name");
                JsonObject bloque = new JsonObject();
                bloque.addProperty("tableName", tableName);

                JsonArray rows = new JsonArray();
                String rowSql = "SELECT * FROM " + tableName;
                try (PreparedStatement rowStatement = con.prepareStatement(rowSql);
                     ResultSet rowRs = rowStatement.executeQuery()) {

                    while (rowRs.next()) {
                        JsonObject row = new JsonObject();
                        row.addProperty("id", rowRs.getString("id"));
                        row.addProperty("codigo_votante", rowRs.getString("codigo_votante"));
                        row.addProperty("codigo_candidato", rowRs.getString("codigo_candidato"));
                        row.addProperty("hash", rowRs.getString("hash"));
                        row.addProperty("ref_anterior_bloque", rowRs.getString("ref_anterior_bloque"));
                        rows.add(row);
                    }
                }
                bloque.add("rows", rows);
                bloques.add(bloque);
            }
        } catch (SQLException e) {
            log.error("Error retrieving Bloques", e);
        }

        return bloques;
    }
}
