%nbtClass% thisNBT;

public CompoundNBT(Object thisNBT) {
	this.thisNBT = thisNBT;
}

public int getInt(java.lang.String tag) {
	return thisNBT.%getInt%(tag);
}

public float getFloat(java.lang.String tag) {
	return thisNBT.%geFloat%(tag);
}

public byte getByte(java.lang.String tag) {
	return thisNBT.%getByte%(tag);
}

public long getLong(java.lang.String tag) {
	return thisNBT.%getLong%(tag);
}

public short getShort(java.lang.String tag) {
	return thisNBT.%getShort%(tag);
}

public double getDouble(java.lang.String tag) {
	return thisNBT.%getDouble%(tag);
}

public java.lang.String getString(java.lang.String tag) {
	return thisNBT.%getString%(tag);
}

public boolean getBoolean(java.lang.String tag) {
	return thisNBT.%getBoolean%(tag);
}

public java.util.UUID getUUID(java.lang.String tag) {
	return thisNBT.%getUUID%(tag);
}

public byte getId() {
	return thisNBT.%getId%();
}

public void putInt(java.lang.String tag, int value) {
	thisNBT.%putInt%(tag, value);
}

public void putFloat(java.lang.String tag, float value) {
	thisNBT.%putFloat%(tag, value);
}

public void putByte(java.lang.String tag, byte value) {
	thisNBT.%putByte%(tag, value);
}

public void putLong(java.lang.String tag, long value) {
	thisNBT.%putLong%(tag, value);
}

public void putShort(java.lang.String tag, short value) {
	thisNBT.%putShort%(tag, value);
}

public void putDouble(java.lang.String tag, double value) {
    thisNBT.%putDouble%(tag, value);
}

public void putString(java.lang.String tag, java.lang.String value) {
	thisNBT.%putString%(tag, value);
}

public void putBoolean(java.lang.String tag, boolean value) {
	thisNBT.%putBoolean%(tag, value);
}

public void putBoolean(java.lang.String tag, java.util.UUID value) {
	thisNBT.%putUUID%(tag, value);
}