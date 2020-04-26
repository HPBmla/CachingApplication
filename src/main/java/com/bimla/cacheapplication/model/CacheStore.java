package com.bimla.cacheapplication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheStore {

  private List<Map<String, Object>> cacheObjectValue;
  private boolean flagValue = false;
  private String mapKey;


}
