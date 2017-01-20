package scopelyTest;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import com.google.testing.threadtester.AnnotatedTestRunner;
import com.google.testing.threadtester.ThreadedAfter;
import com.google.testing.threadtester.ThreadedBefore;
import com.google.testing.threadtester.ThreadedMain;
import com.google.testing.threadtester.ThreadedSecondary;

import core.RedisCore;
import core.RedisCoreException;

public class RedisCoreINCRConcurrencyTest {
	
	RedisCore core;

	@ThreadedBefore
	public void before() {
		core=new RedisCore();
		core.SET("test", ""+10);
	}
	
	@ThreadedMain
	public void mainThread() throws RedisCoreException {
		core.INCR("test");
	}
	
	@ThreadedSecondary
	public void secondaryThread() throws RedisCoreException{
		core.INCR("test");
	}
	
	@ThreadedAfter
	public void after() {
		int current=Integer.parseInt(core.GET("test"));
		assertEquals(12,current);
	}
	
	@Test public void testThreading() { 
		AnnotatedTestRunner runner = new AnnotatedTestRunner(); 
		runner.runTests(this.getClass(), RedisCore.class); 
	}

}
