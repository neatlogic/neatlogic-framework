package codedriver.framework.restful.counter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

public class DelayedQueueTest {

	public static void main(String[] args) throws InterruptedException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		DelayQueue<Item> queue = new DelayQueue<>();
		Item item1 = new Item("item1", 2, TimeUnit.SECONDS);
		queue.add(item1);
		Item item2 = new Item("item2", 4, TimeUnit.SECONDS);
		queue.add(item2);
		Item item3 = new Item("item3", 6, TimeUnit.SECONDS);
		queue.add(item3);
		Item item4 = new Item("item4", 8, TimeUnit.SECONDS);
		queue.add(item4);
		System.out.println("begin time: " + sdf.format(new Date()));
		for(int i = 0; i < 4; i++) {
			Item take = queue.take();
			System.out.println("name:" + take.getName() + "-" + sdf.format(new Date(take.getTime())));
		}
	}

}
