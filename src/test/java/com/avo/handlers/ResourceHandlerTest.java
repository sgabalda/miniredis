package com.avo.handlers;


import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class ResourceHandlerTest {

  private ResourceMap rs = new ResourceMap();
  private final CyclicBarrier gate = new CyclicBarrier(3);

  private String randString(int length){
    return UUID.randomUUID().toString().substring(0, length);
  }

  @Test
  public void testRHSetOK() throws Exception {
    ResourceHandler rh = new ResourceHandler(rs);
    assert rh.set(randString(4), randString(4));
  }


  @Test
  public void testRHSetMultithreaded() throws Exception {
    int timeout = 10;
    boolean result = false;
    Thread t1 = new Thread(){
      public void run(){
        ResourceHandler rh = new ResourceHandler(rs);
        try {
          gate.await();
          assert rh.set(randString(4), randString(4));
        } catch (Exception e) {
          assert false;
        }
      }
    };

    Thread t2 = new Thread() {
      public void run(){
        ResourceHandler rh = new ResourceHandler(rs);
        try {
          gate.await();
          assert rh.set(randString(4), randString(4));
        } catch (Exception e) {
          assert false;
        }
      }
    };
    t1.start();
    t2.start();
    gate.await();

    while (timeout > 0) {
       if (rs.size() == 2) {
         result = true;
         timeout = 0;
       }
       else {
        TimeUnit.SECONDS.sleep(1);
        timeout --;
      }
    }
    assert result;
  }

}
