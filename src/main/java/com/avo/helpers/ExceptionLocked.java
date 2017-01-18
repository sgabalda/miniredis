package com.avo.helpers;

public class ExceptionLocked extends Exception {

  public ExceptionLocked() {
    super("Max number of retries reached: Locked resource");
  }

}
