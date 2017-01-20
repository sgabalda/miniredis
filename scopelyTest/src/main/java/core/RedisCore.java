package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import core.store.SetItem;
import core.store.SetItemComparator;
import core.store.StoreValue;

public class RedisCore {
	
	private Object semaphore=new Object();
	
	private static final String NIL_STRING="(nil)";
	
	private Map<String,StoreValue<Object>> store=new HashMap<String, StoreValue<Object>>();

	public String SET(String key, String value){
		return SET(key,value,-1);
	}
	public String SET(String key, String value, int expires){
		String result=null;
		synchronized(semaphore){
			StoreValue<Object> storeValue=new StoreValue<Object>();
			storeValue.setExpire(expires);
			storeValue.setValue(value);
			store.put(key,storeValue);
		}
		result="OK";
		
		return result;
	}
	
	public String GET(String key){
		String result=null;
		//only the read must be synchronized, since we save a new storeValue each time, 
		//so the object here read will be consistent
		StoreValue<?extends Object> storeValue;
		synchronized(semaphore){
			storeValue=store.get(key);
		}
		if(storeValue!=null){
			Object value=storeValue.getValue();
			if(value instanceof String && value!=null){
				if(storeValue.getExpire()!=-1){
					System.out.println("Getting expires stored: "
							+storeValue.getStoredTime()+" + "
							+storeValue.getExpire()+" < "+ System.currentTimeMillis());
					
					if(storeValue.getStoredTime()
							+storeValue.getExpire()*1000
							> System.currentTimeMillis()){
						System.out.println("yes");
						result=(String)value;
					}else{
						System.out.println("No");
					}
				}else{
					result=(String)value;
				}
			}
		}
		return result==null?"(nil)":result;
	}
	
	public int DEL(String key){
		StoreValue storeValue;
		synchronized(semaphore) {
			storeValue=store.remove(key);
		}
		if(storeValue!=null)return 1;
		return 0;
		
	}
	
	public int INCR(String key) throws RedisCoreException{
		synchronized(semaphore){
			StoreValue<Object> storeValue=store.get(key);
			if(storeValue!=null){
				Object value=storeValue.getValue();
				if(value instanceof String && value!=null){
					try{
						int current=Integer.parseInt((String)value);
						//create a new value to save to allow non blocking reads
						//this might be memory consuming, but if GETs are much more than INCR and SETs it will be a benefit
						StoreValue<Object> toSave=new StoreValue<>(storeValue);
						toSave.setExpire(storeValue.getExpire());
						toSave.setValue(String.valueOf(++current));
						store.put(key, toSave);
						return current;
					}catch(NumberFormatException e){
						throw new RedisCoreException("The number iis not an integer");
					}
				}
			}
		}
		throw new RedisCoreException("INCR to an existing or not integer with key "+key);
	}
	
	public int DBSIZE() {
		return store.size();
	}
	
	public Object ZADD(String key, double score, String member, ZADDOptions option) 
			throws RedisCoreException{
		Object result=null;
		
		StoreValue<Object> storeValue;
		synchronized(semaphore){
			storeValue=store.get(key);
			if(storeValue==null) {
				Map<String,SetItem> set=new HashMap<>();
				set.put(member,new SetItem(score,member));
				storeValue=new StoreValue<Object >();
				storeValue.setExpire(-1);
				storeValue.setValue(set);
				store.put(key,storeValue);
				result=1;
			}else {
				Object value=storeValue.getValue();
				if(value instanceof Map) {
					Map<String,SetItem> set=(Map<String,SetItem>)value;
					SetItem toadd=new SetItem(score,member);
					if(set.remove(member)!=null) {
						result=0;
					}else {
						result=1;
					}
					/*System.out.println("Adding member "+toadd.getMember()+" with score "+toadd.getScore());
					*/set.put(member,toadd);
				}else {
					throw new RedisCoreException("Adding a score and member in a non set element");
				}
				
			}
		}
		
		return result;
	}
	
	public enum ZADDOptions{
		XX, NX, CH, INCR;
		
	}
	
	public int ZCARD(String key) {
		int result=0;
		
		StoreValue<Object> storeValue;
		synchronized(semaphore){
			storeValue=store.get(key);
		}
		if(storeValue!=null) {
			Object value=storeValue.getValue();
			if(value instanceof Map) {
				return ((Map)value).size();
			}
		}
		return result;
	}
	
	public Integer ZRANK(String key, String member) {
		Integer result=null;
		StoreValue<Object> storeValue;
		synchronized(semaphore){
			storeValue=store.get(key);
		}
		if(storeValue!=null) {
			Object value=storeValue.getValue();
			if(value instanceof Map) {
				Map<String,SetItem> set=(Map<String,SetItem>)value;
				int counter=0;
				System.out.println("Searching for "+member);
				List<SetItem> sorted=new ArrayList<>(set.values());
				Collections.sort(sorted,new SetItemComparator());
				for (SetItem o:sorted) {
					/*System.out.println("On counter "+counter+
							" there is "+o.getMember()+" with score "+o.getScore());*/
					if(o.getMember().equals(member)) {
						return counter;
					}else {
						counter ++;
					}
				}
			}
		}
		return result;
	}
	
	public List<String> ZRANGE(String key, int start, int stop, boolean WITHSCORES) {
		List<String> result=new ArrayList<>();
		StoreValue<Object> storeValue;
		synchronized(semaphore){
			storeValue=store.get(key);
		}
		if(storeValue!=null) {
			Object value=storeValue.getValue();
			if(value instanceof Map) {
				Map<String,SetItem> set=(Map<String,SetItem>)value;
				List<SetItem> sorted=new ArrayList<>(set.values());
				Collections.sort(sorted,new SetItemComparator());
				if(start<sorted.size()) {
					for (int i=start; i<=stop && i<sorted.size(); i++) {
						result.add(sorted.get(i).getMember());
						if(WITHSCORES) result.add(String.valueOf(sorted.get(i).getScore()));
					}
				}
			}
		}
		return result;
	}
	

}
