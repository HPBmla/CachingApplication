package com.bimla.cacheapplication.controller;

import com.bimla.cacheapplication.model.QueryObject;
import com.bimla.cacheapplication.service.CacheApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/query")
public class CacheApplicationController {

    @Autowired
    CacheApplicationService cacheApplicationService;

    @PostMapping(value = "/mysql")
    public List<Map<String, Object>> executeQuery(@RequestBody QueryObject queryObject){
        return cacheApplicationService.executeQuery(queryObject);
    }

}
