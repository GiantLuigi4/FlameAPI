package com.tfc.API.flamemc.event.init_steps;

import com.tfc.API.flame.annotations.ASM.Unmodifiable;
import com.tfc.API.flame.event.Event;

@Unmodifiable
public class RegistryStep extends Event {
	@Override
	public boolean isCancelable() {
		return false;
	}
}
