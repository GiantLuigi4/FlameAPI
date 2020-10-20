package com.tfc.API.flamemc.data;

import java.util.UUID;

//Refer to nbt_class.java for the code for this
public class CompoundNBT {
	public native int getInt(String tag);
	
	public native float getFloat(String tag);
	
	public native byte getByte(String tag);
	
	public native long getLong(String tag);
	
	public native short getShort(String tag);
	
	public native double getDouble(String tag);
	
	public native String getString(String tag);
	
	public native boolean getBoolean(String tag);
	
	public native UUID getUUID(String tag);
	
	public native byte getId();
	
	public native void putInt(String tag, int value);
	
	public native void putFloat(String tag, float value);
	
	public native void putByte(String tag, byte value);
	
	public native void putLong(String tag, long value);
	
	public native void putShort(String tag, short value);
	
	public native void putDouble(String tag, double value);
	
	public native void putString(String tag, String value);
	
	public native void putBoolean(String tag, boolean value);
	
	public native void putUUID(String tag, UUID value);
	
	public native Object unwrap();
}
