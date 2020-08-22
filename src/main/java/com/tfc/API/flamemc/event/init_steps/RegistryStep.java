package com.tfc.API.flamemc.event.init_steps;

import com.tfc.API.flame.event.Event;

public class RegistryStep extends Event {
	@Override
	public boolean isCancelable() {
		return false;
	}
}
