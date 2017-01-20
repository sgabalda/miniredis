package core.store;

public class StoreValue<T>{
	private long storedTime;
	private long expire;
	private T value;
	
	public StoreValue(StoreValue initial){
		this.storedTime=initial.getStoredTime();
	}
	public StoreValue(){
		this.storedTime=System.currentTimeMillis();
	}
	public long getExpire() {
		return expire;
	}
	public void setExpire(long expire) {
		this.expire = expire;
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	public long getStoredTime() {
		return storedTime;
	}
	
	
}
