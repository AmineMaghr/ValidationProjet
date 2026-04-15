package com.example.app.services;

import java.sql.SQLException;
import java.util.List;

public interface Iservice <T>{

    public void add(T t) throws SQLException;
    public void update (T t) throws SQLException;
    public void delete (int id) throws SQLException;
    public List<T> select()throws SQLException;
}
