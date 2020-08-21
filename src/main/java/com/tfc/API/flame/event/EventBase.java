package com.tfc.API.flame.event;

public abstract class EventBase {
	public abstract boolean isCanceled();
	
	public abstract void setCanceled(boolean canceled);
}
