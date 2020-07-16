package codedriver.framework.restful.counter;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class Item implements Delayed {

	private long time;
	
	private String name;
	
	public Item(String name, long time, TimeUnit unit) {
		this.time = System.currentTimeMillis() + (time > 0 ? unit.toMillis(time) : 0);
		this.name = name;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(Delayed o) {
		Item item = (Item) o;
		long diff = this.time - item.time;
		if(diff <= 0){
			return -1;
		} else{
			return 1;
		}
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return time - System.currentTimeMillis();
	}

}
