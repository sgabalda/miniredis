package com.avo.handlers;

import com.avo.helpers.ExceptionLocked;
import com.avo.helpers.ExceptionUnknownStatus;
import com.avo.helpers.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/*
  This class has the logic to handle operations on resources
  Commands don't known about what kind of data structure we do have
  @ResourceHandler does.
 */
public class ResourceHandler {

  private static Logger log = LoggerFactory.getLogger(ResourceHandler.class);
  private java.util.Random rand = new Random();
  private Resource res = null;
  public  ResourceHandler(Resource rs) {
    res = rs;
  }
  private  int retry = 0;
  // should be set via conf parameter
  static final int MAXRETRIES = 10;

  public boolean set(String key, String val) throws Exception {
    Status st = res.set(key, val);
    switch (st) {
      case LOCKED:
        handleLocks(key, val);
      case UPDATED:
        return true;
      default:
        throw new ExceptionUnknownStatus();
    }
  }

  public String get(String key) throws Exception {
    String val = res.get(key);
    if (val == null) {
      return null;
    }
   return val;
  }

  public String get(String key, String val) throws Exception {
    if (val.equals(res.get(key)))
      return "OK";
    else
      return null;
  }

  public int size() {
    return res.size();
  }

  /*
    This should handle locks, which means, that should wait and retry im a while.
     Added maximum number of retries before failing for avoiding deadlocks
  */
  private void handleLocks(String key, String val) throws Exception {
    int wait = rand.nextInt(11);
    if (retry == MAXRETRIES)
      throw new ExceptionLocked();
    retry += 1;
    TimeUnit.MILLISECONDS.sleep(wait);
    set(key, val);

  }

}
