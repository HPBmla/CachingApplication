package com.bimla.cacheapplication.dao;

import com.bimla.cacheapplication.model.QueryObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
public class CacheApplicationDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> selectExecution(QueryObject queryObject){
        List<Map<String, Object>> resultObjectList = null;
        resultObjectList = jdbcTemplate.queryForList(queryObject.getQuery(), queryObject.getParameters().toArray());
        return resultObjectList;
    }

    public boolean writeExecution(QueryObject queryObject){
        return jdbcTemplate.execute(queryObject.getQuery(), (PreparedStatementCallback<Boolean>) ps -> {
            for(int i = 1; i <= queryObject.getParameters().size(); i++){
                ps.setObject(i, queryObject.getParameters().get(i-1));
            }
            return ps.execute();

        });

    }
}
