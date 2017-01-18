package com.avo.helpers;

public class ExceptionUnknownStatus extends Exception{

  public ExceptionUnknownStatus() {
    super("Unknown status returned");
  }
}
