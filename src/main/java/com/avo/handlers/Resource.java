package com.avo.handlers;


import com.avo.helpers.Status;

public interface Resource {


  Status set(String key, String value);

  String get(String key);

  int size();

}