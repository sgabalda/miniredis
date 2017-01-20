package scopelyTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import core.RedisCore;
import core.RedisCoreException;


public class RedisCoreTest {
	
	private RedisCore core;
	
	@Before
	public void setUpTest(){
		core=new RedisCore();
	}

	@Test
	public void testSETStringString() {
		for(int i=0; i<50; i++){
			String result=core.SET("key"+i, i+"oeoe"+i);
			assertEquals(result, "OK");
		}
		
		for(int i=0; i<50; i++){
			String result=core.GET("key"+i);
			assertEquals(result, i+"oeoe"+i);
		}
		
	}

	@Test
	public void testSETStringStringInt() throws Exception {
		for(int i=1; i<5; i++){
			String result=core.SET("key"+i, i+"oeoe"+i, i);
			assertEquals(result, "OK");
		}
		
		for(int i=1; i<5; i++){
			String result=core.GET("key"+i);
			assertEquals(result, i+"oeoe"+i);
		}

		Thread.sleep(5000);
		
		for(int i=1; i<5; i++){
			String result=core.GET("key"+i);
			assertNull(result);
		}
		
	}
	
	@Test
	public void testDEL(){
		for(int i=1; i<5; i++){
			String result=core.SET("key"+i, i+"oeoe"+i);
			assertEquals(result, "OK");
		}
		for(int i=1; i<3; i++){
			int result=core.DEL("key"+i);
			assertEquals(1,result);
		}
		for(int i=1; i<3; i++){
			String result=core.GET("key"+i);
			assertNull(result);
		}
		for(int i=10; i<15; i++){
			int result=core.DEL("key"+i);
			assertEquals(result, 0);
		}
		for(int i=3; i<5; i++){
			String result=core.GET("key"+i);
			assertEquals(i+"oeoe"+i,result);
		}
	}
	
	@Test
	public void testINCR() throws Exception{
		for(int i=1; i<3; i++){
			String result=core.SET("key"+i, String.valueOf(i*10));
			assertEquals(result, "OK");
		}
		for(int i=1; i<3; i++){
			int result=core.INCR("key"+i);
			assertEquals(result, i*10+1);
		}
		for(int i=3; i<5; i++){
			try {
				core.INCR("key"+i);
			}catch(Exception e) {
				assertTrue(e instanceof RedisCoreException);
			}
			
		}
		
	}
	@Test
	public void testDBSIZE() throws Exception{
		for(int i=1; i<3; i++){
			core.SET("key"+i, String.valueOf(i*10));
		}
		assertEquals(2,core.DBSIZE());
		
	}
	
	@Test
	public void testZADD() throws Exception{
		assertEquals(1,core.ZADD("test1", 1, "value1", null));
		assertEquals(1,core.ZADD("test1", 1, "value2", null));
		assertEquals(0,core.ZADD("test1", 1, "value1", null));
		core.SET("key", "blah");
		try {
			core.ZADD("key", 1, "value1", null);
			assertEquals("","blah");
		}catch(RedisCoreException e) {
			
		}
		
		
	}
	@Test
	public void testZCARD() throws Exception{
		assertEquals(0,core.ZCARD("key"));
		core.SET("key", "blah");
		assertEquals(0,core.ZCARD("key"));
		core.DEL("key");
		core.ZADD("key", 1, "value1", null);
		assertEquals(1,core.ZCARD("key"));
		core.ZADD("key", 1, "value2", null);
		assertEquals(2,core.ZCARD("key"));
		core.ZADD("key", 1, "value2", null);
		assertEquals(2,core.ZCARD("key"));
		core.DEL("key");
		assertEquals(0,core.ZCARD("key"));
	}
	
	@Test
	public void testZRANK() throws Exception{
		assertNull(core.ZRANK("key","member1"));
		core.SET("key", "blah");
		assertNull(core.ZRANK("key","member1"));
		core.DEL("key");
		core.ZADD("key", 1, "member2", null);
		assertNull(core.ZRANK("key","member1"));
		core.ZADD("key", 2, "member1", null);
		assertEquals(1,core.ZRANK("key","member1").intValue());
		core.ZADD("key", 3, "member2", null);
		assertEquals(0,core.ZRANK("key","member1").intValue());
		core.ZADD("key", 2, "member2", null);
		assertEquals(0,core.ZRANK("key","member1").intValue());
		core.ZADD("key", 2, "member0", null);
		assertEquals(1,core.ZRANK("key","member1").intValue());
	}
	
	@Test
	public void testZRANGE() throws Exception{
		assertEquals(0,core.ZRANGE("key", 1, 2, false).size());
		core.SET("key", "blah");
		assertEquals(0,core.ZRANGE("key", 1, 2, false).size());
		core.DEL("key");
		core.ZADD("key", 1, "member1", null);
		assertEquals("member1",core.ZRANGE("key", 0, 0, false).get(0));
		assertEquals("1.0",core.ZRANGE("key", 0, 1, true).get(1));
		core.ZADD("key", 2, "member2", null);
		assertEquals("member1",core.ZRANGE("key", 0, 2, false).get(0));
		assertEquals("1.0",core.ZRANGE("key", 0, 1, true).get(1));
		assertEquals("member2",core.ZRANGE("key", 0, 2, false).get(1));
		assertEquals("2.0",core.ZRANGE("key", 0, 3, true).get(3));
		assertEquals("2.0",core.ZRANGE("key", 1, 2, true).get(1));
	}
}
