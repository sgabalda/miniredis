package com.avo.helpers;

public class ExceptionKeyNullOrEmpty extends Exception{

   public ExceptionKeyNullOrEmpty() {
     super("Key was null or empty");
   }
}
