package com.tfc.API.flame.event.bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

//An forge like event bus
//Open to ASM for optimizations
public class EventBus {
	private final HashMap<String, ArrayList<Consumer<Object>>> consumersMap = new HashMap<>();
	
	public <A> void register(Class<A> target, Consumer<A> consumer) {
		ArrayList<Consumer<Object>> consumersList;
		if (consumersMap.containsKey(target.getName())) consumersList = consumersMap.get(target.getName());
		else consumersList = new ArrayList<>();
		consumersList.add((Consumer<Object>) consumer);
		if (!consumersMap.containsKey(target.getName())) consumersMap.put(target.getName(), consumersList);
	}
	
	public <A> void post(Class<A> target, A event) {
		if (consumersMap.containsKey(target.getName())) consumersMap.get(target.getName()).forEach(consumer -> {
			consumer.accept(event);
		});
	}
}
