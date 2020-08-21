package com.tfc.API.flame.event.initsteps;

import com.tfc.API.flame.event.Event;

public class RegistryStep extends Event {
	@Override
	public boolean isCancelable() {
		return false;
	}
}
