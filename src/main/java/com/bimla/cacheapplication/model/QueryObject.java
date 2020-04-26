package com.bimla.cacheapplication.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class QueryObject {
  private String query;
  private List<Object> parameters;
  private String type;
}
