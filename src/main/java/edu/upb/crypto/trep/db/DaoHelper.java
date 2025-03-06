package edu.upb.crypto.trep.db;

import java.net.ConnectException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by :MC4
 * Autor      :Ricardo Laredo
 * Email      :rlaredo@mc4.com.bo
 * Date       :21-11-18
 * Project    :sfe
 * Package    :bo.com.mc4.dbintegration.dao
 * Copyright  : MC4
 */
public class DaoHelper<T>  {

    public DaoHelper() {
        super();
    }

    public interface QueryParameters {
        void setParameters(PreparedStatement pst) throws SQLException;
    }

    public interface ResultReader<T> {
        T getResult(ResultSet result) throws SQLException;
    }

    public interface ResultProcedureReader<T> {
        T getResult(CallableStatement callableStatement) throws SQLException;
    }

    public List<T> executeQuery(String query, ResultReader<T> reader) throws ConnectException, SQLException {
        return executeQuery(query, null, reader);
    }

    public List<T> executeQuery(String query, QueryParameters params, ResultReader<T> reader)
            throws ConnectException, SQLException {
        Connection conn;
        PreparedStatement st = null;
        try {
            conn = ConnectionDB.instance.getConnection();
        } catch (Exception ex) {

            throw new ConnectException("No se logro crear conexion a la base de datos");
        }
        st = conn.prepareStatement(query);

        try {
            if (params != null) {
                params.setParameters(st);
            }
            boolean status = st.execute();
            if (status) {
                List<T> results = new ArrayList<T>();
                try (ResultSet result = st.getResultSet()) {
                    while (result.next()) {
                        T value = reader.getResult(result);
                        if (value != null) {
                            results.add(value);
                        }
                    }
                    st.close();
                    return results;
                }
            } else {
                st.close();
            }
        } catch (SQLException e) {
            st.close();
            throw e;
        } catch (Exception e) {
            st.close();
            throw e;
        } finally {
            if (st != null) st.close();
            if (!conn.isClosed()) {
                conn.close();
            }
        }
        return new ArrayList<T>();
    }

    protected void insert(String query, QueryParameters params, Model model) throws Exception {
        Connection conn = null;
        try {
            conn = ConnectionDB.instance.getConnection();
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
        try (PreparedStatement st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            if (params != null) {
                params.setParameters(st);
            }
            if (st.executeUpdate() > 0) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        model.setId(rs.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (!conn.isClosed()) {
                conn.close();
            }
        }
    }

    /**
     * Metodo actualizar
     *
     * @param query
     * @param params
     * @throws Exception
     */
    protected void update(String query, QueryParameters params) throws ConnectException, SQLException {
        Connection conn;
        PreparedStatement st = null;
        try {
            conn = ConnectionDB.instance.getConnection();
        } catch (Exception ex) {
            throw new ConnectException("No se logro crear conexion a la base de datos");
        }
        st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        try {
            if (params != null) {
                params.setParameters(st);
            }
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            if (st != null) st.close();
            throw e;
        } catch (Exception e) {
            if (st != null) st.close();
            throw e;
        } finally {
            if (st != null) st.close();
            if (!conn.isClosed()) {
                conn.close();
            }
        }
    }

    int executeQueryCount(String query, QueryParameters params)
            throws ConnectException, SQLException {
        Connection conn;
        try {
            conn = ConnectionDB.instance.getConnection();
        } catch (Exception ex) {
            throw new ConnectException("No se logro crear conexion a la base de datos");
        }
        try (PreparedStatement st = conn.prepareStatement(query)) {
            if (params != null) {
                params.setParameters(st);
            }
            boolean status = st.execute();
            if (status) {
                int cantRows = -1;
                try (ResultSet result = st.getResultSet()) {
                    if (result.next()) {
                        cantRows = result.getInt(1);
                    }
                    return cantRows;
                }
            } else {
                st.close();
            }
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        } finally {
            if (!conn.isClosed()) {
                conn.close();
            }
        }
        return -1;
    }

    /**
     * Metodo para llamar a un procedimiento almacenado
     *
     * @param query
     * @param params
     * @throws Exception
     */
    protected T executeProcedureStore(String query, QueryParameters params, ResultProcedureReader<T> reader) throws Exception {
        Connection conn;
        CallableStatement st = null;
        try {
            conn = ConnectionDB.instance.getConnection();
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
        st = conn.prepareCall(query);
        T value = null;
        try {
            if (params != null) {
                params.setParameters(st);
            }

            if (st.execute()) {
                value = reader.getResult(st);
            }
            st.close();
            return value;
        } catch (
                SQLException e) {
            st.close();
            throw new SQLException(e);
        } catch (
                Exception e) {
            st.close();
            throw new Exception(e);
        } finally {
            if (st != null) st.close();
            if (!conn.isClosed()) {
                conn.close();
            }
        }
    }

}
