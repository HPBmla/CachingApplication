package com.bimla.cacheapplication.service;

import com.bimla.cacheapplication.dao.CacheApplicationDAO;
import com.bimla.cacheapplication.model.CacheStore;
import com.bimla.cacheapplication.model.QueryObject;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheApplicationService {

    private static final Logger logger =  LoggerFactory.getLogger(CacheApplicationService.class);
    @Autowired
    CacheApplicationDAO cacheApplicationDAO;

    private Map<String,CacheStore> globalMap = new ConcurrentHashMap<>();
    private Map<String, List<String>> globalTableMap = new ConcurrentHashMap<>();

    @SneakyThrows
    public List<Map<String, Object>> executeQuery(QueryObject queryObject){

        CacheStore cacheObj = new CacheStore();
        if("SELECT".equalsIgnoreCase(queryObject.getType())){
            String queryTable =  formatMapKeyFromQuery(queryObject.getQuery(),queryObject.getType(), queryObject.getParameters());
            logger.info("Formatted Query "+queryTable);
            if(globalMap.containsKey(queryTable) && globalMap.get(queryTable).isFlagValue() == true)
            {
                logger.info("READ from Cache");
                return globalMap.get(queryTable).getCacheObjectValue();
            }
            else
            {
                try {
                    List<Map<String, Object>> results = cacheApplicationDAO.selectExecution(queryObject);
                    cacheObj.setCacheObjectValue(results);
                    cacheObj.setFlagValue(true);
                    cacheObj.setMapKey(queryTable);
                    globalMap.put(queryTable, cacheObj);
                    String tableName = getTableNameFromFormatQuery(queryTable);
                    if (globalTableMap.containsKey(tableName)) {
                        globalTableMap.get(tableName).add(queryTable);
                    } else {
                        globalTableMap.put(tableName, new ArrayList<>());
                        globalTableMap.get(tableName).add(queryTable);
                    }
                }catch (Exception e){
                    if(globalMap.containsKey(queryTable)){
                        logger.info("READ from Cache");
                        return globalMap.get(queryTable).getCacheObjectValue();

                    }
                    throw new SQLException("Error in executing query");
                }
                return  cacheObj.getCacheObjectValue();
            }


        }else {
            boolean status = cacheApplicationDAO.writeExecution(queryObject);
            String tableName = formatMapKeyFromQuery(queryObject.getQuery(), queryObject.getType(), queryObject.getParameters());
            List<String> queryList =    globalTableMap.get(tableName);
            if(status && queryList != null){
                for(String qury : queryList){
                    globalMap.get(qury).setFlagValue(false);
                }
            }
           // updateObj.setFlagValue(false);

            return null;
        }

    }



    public String formatMapKeyFromQuery(String query, String queryType, List<Object> parameters)
    {

        queryType = queryType.toLowerCase();
        String[] splitQuery;
        String projections = null;
        String conditionClause = null;
        switch (queryType)
        {
            case "select":
                String[] tempValue =query.split("from",3);
                splitQuery=tempValue[1].split(" ",3);
                projections = StringUtils.substringBetween(query,"select","from").trim().toLowerCase();
                try{
                    conditionClause = query.split("where",2)[1].trim();
                }catch (IndexOutOfBoundsException e){
                    conditionClause = "";
                }
                query=splitQuery[1]+"-"+projections+"-"+conditionClause;
                for (Object o : parameters){
                    query = query + "," + o ;
                }
                break;
            case "delete":
                splitQuery =query.split("from",2);
                splitQuery=splitQuery[1].split(" ",3);
                //splitQuery=   query.split("from",2);
                query = splitQuery[1];
                break;
            case "insert":
                splitQuery=   query.split("into",2);
                splitQuery=splitQuery[1].split(" ",3);
                query = splitQuery[1];
                break;
            case "update":
                splitQuery=   query.split("update",2);
                splitQuery=splitQuery[1].split(" ",3);
                query = splitQuery[1];
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + queryType);
        }

        return query;
    }
    public String getTableNameFromFormatQuery(String formatQuery)
    {
        String tableName = formatQuery.split("-",2)[0].trim();
        return tableName;
    }

}
