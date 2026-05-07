package com.example.app.dao;

import java.sql.SQLException;
import java.util.List;

public interface IDAO<T> {
    void add(T entity) throws SQLException;
    void update(T entity) throws SQLException;
    void delete(int id) throws SQLException;
    List<T> select() throws SQLException;
}