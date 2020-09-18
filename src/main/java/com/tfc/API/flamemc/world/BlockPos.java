package com.tfc.API.flamemc.world;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class BlockPos {
	private static final Constructor<?> instancer;
	private static final Method x;
	private static final Method y;
	private static final Method z;
	private static final Method offset;
	
	static {
		try {
			instancer = Class.forName("BlockPos").getConstructor(int.class, int.class, int.class);
			offset = Class.forName("BlockPos").getMethod("offset", int.class, int.class, int.class);
			x = Class.forName("BlockPos").getMethod("getX");
			y = Class.forName("BlockPos").getMethod("getY");
			z = Class.forName("BlockPos").getMethod("getZ");
		} catch (NoSuchMethodException | ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private final Object thisBlockPos;
	
	private BlockPos(Object thisBlockPos) {
		this.thisBlockPos = thisBlockPos;
	}
	
	public BlockPos(int x, int y, int z) {
		try {
			this.thisBlockPos = instancer.newInstance(x, y, z);
		} catch (Throwable err) {
			throw new RuntimeException(err);
		}
	}
	
	public BlockPos offset(int x, int y, int z) {
		try {
			return new BlockPos(offset.invoke(thisBlockPos, x, y, z));
		} catch (Throwable err) {
			throw new RuntimeException(err);
		}
	}
	
	public int getX() {
		try {
			return (int) x.invoke(thisBlockPos);
		} catch (Throwable err) {
			throw new RuntimeException(err);
		}
	}
	
	public int getY() {
		try {
			return (int) y.invoke(thisBlockPos);
		} catch (Throwable err) {
			throw new RuntimeException(err);
		}
	}
	
	public int getZ() {
		try {
			return (int) z.invoke(thisBlockPos);
		} catch (Throwable err) {
			throw new RuntimeException(err);
		}
	}
	
	@Override
	public String toString() {
		return "BlockPos{" + getX() + ", " + getY() + ", " + getZ() + "}";
	}
	
	public Object unwrap() {
		return thisBlockPos;
	}
}
