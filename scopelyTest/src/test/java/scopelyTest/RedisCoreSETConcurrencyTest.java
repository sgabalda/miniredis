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

public class RedisCoreSETConcurrencyTest {
	
	RedisCore core;

	@ThreadedBefore
	public void before() {
		core=new RedisCore();
		
	}
	
	@ThreadedMain
	public void mainThread() throws RedisCoreException {
		core.SET("test","not expires");
	}
	
	@ThreadedSecondary
	public void secondaryThread() throws RedisCoreException{
		core.SET("test", "expires",1);
	}
	
	@ThreadedAfter
	public void after() {
		String result=core.GET("test");
		assertEquals(true,result==null || result.equals("not expires"));
	}
	
	@Test public void testThreading() { 
		AnnotatedTestRunner runner = new AnnotatedTestRunner(); 
		runner.runTests(this.getClass(), RedisCore.class); 
	}

}
