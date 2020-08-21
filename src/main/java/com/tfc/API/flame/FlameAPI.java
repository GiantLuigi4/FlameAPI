package com.tfc.API.flame;

import com.tfc.API.flame.annotations.ASM.Unmodifiable;
import com.tfc.API.flame.event.bus.EventBus;

@Unmodifiable
public class FlameAPI {
	public static final FlameAPI instance = new FlameAPI();
	
	public final EventBus bus = new EventBus();
	
	private FlameAPI() {
	}
}
