import java.util.UUID;

public class NBTObject {
	%nbtClass% thisNBT;
	
	public NBTObject(%nbtClass% thisNBT) {
		this.thisNBT = thisNBT;
	}
	
	public NBTObject() {
		this.thisNBT = new %nbtClass%();
	}

	public int getInt(String tag) {
		return thisNBT.%getInt%(tag);
	}

	public float getFloat(String tag) {
		return thisNBT.%getFloat%(tag);
	}

	public byte getByte(String tag) {
		return thisNBT.%getByte%(tag);
	}

	public long getLong(String tag) {
		return thisNBT.%getLong%(tag);
	}

	public short getShort(String tag) {
		return thisNBT.%getShort%(tag);
	}

	public double getDouble(String tag) {
		return thisNBT.%getDouble%(tag);
	}

	public String getString(String tag) {
		return thisNBT.%getString%(tag);
	}

	public boolean getBoolean(String tag) {
		return thisNBT.%getBoolean%(tag);
	}

	public java.util.UUID getUUID(String tag) {
		return thisNBT.%getUUID%(tag);
	}

	public byte getId() {
		return thisNBT.%getId%();
	}

	public void putInt(String tag, int value) {
		thisNBT.%putInt%(tag, value);
	}

	public void putFloat(String tag, float value) {
		thisNBT.%putFloat%(tag, value);
	}

	public void putByte(String tag, byte value) {
		thisNBT.%putByte%(tag, value);
	}

	public void putLong(String tag, long value) {
		thisNBT.%putLong%(tag, value);
	}

	public void putShort(String tag, short value) {
		thisNBT.%putShort%(tag, value);
	}

	public void putDouble(String tag, double value) {
		thisNBT.%putDouble%(tag, value);
	}

	public void putString(String tag, String value) {
		thisNBT.%putString%(tag, value);
	}

	public void putBoolean(String tag, boolean value) {
		thisNBT.%putBoolean%(tag, value);
	}

	public void putUUID(String tag, java.util.UUID value) {
		thisNBT.%putUUID%(tag, value);
	}
	
	public Object unwrap() {
		return this.thisNBT;
	}
	
	public String toString() {
		return this.thisNBT.toString();
	}
}