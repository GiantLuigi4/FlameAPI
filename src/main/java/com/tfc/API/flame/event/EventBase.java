package com.tfc.API.flame.event;

import com.tfc.API.flame.annotations.ASM.Unmodifiable;

@Unmodifiable
public abstract class EventBase {
	public abstract boolean isCanceled();
	
	public abstract void setCanceled(boolean canceled);
}
