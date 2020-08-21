package com.tfc.API.flame.event;

public class Event extends EventBase {
	private boolean isCanceled = false;
	
	public boolean isCancelable() {
		return true;
	}
	
	@Override
	public boolean isCanceled() {
		return isCanceled;
	}
	
	@Override
	public void setCanceled(boolean canceled) {
		if (isCancelable()) {
			isCanceled = canceled;
		} else {
			throw new RuntimeException(new IllegalStateException("Tried to cancel a non cancelable event: " + this.toString() + ", of class " + this.getClass()));
		}
	}
}
