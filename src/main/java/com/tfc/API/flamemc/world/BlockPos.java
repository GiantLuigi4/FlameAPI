package com.tfc.API.flamemc.world;

import com.tfc.utils.ScanningUtils;
import entries.FlameAPI.Main;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public class BlockPos {
	private static final Constructor<?> instancer;
	private static final Method x;
	private static final Method y;
	private static final Method z;
	private static final Method offset;
	
	private static final Class<?> blockPos;
	private static final Field bpX;
	private static final Field bpY;
	private static final Field bpZ;
	
	static {
		try {
			instancer = Class.forName("BlockPos").getConstructor(int.class, int.class, int.class);
			offset = Class.forName("BlockPos").getMethod("offset", int.class, int.class, int.class);
			x = Class.forName("BlockPos").getMethod("getX");
			y = Class.forName("BlockPos").getMethod("getY");
			z = Class.forName("BlockPos").getMethod("getZ");
			blockPos = Class.forName(ScanningUtils.toClassName(Main.getVec3iClass()));
			bpX = blockPos.getDeclaredField("a");
			bpX.setAccessible(true);
			bpY = blockPos.getDeclaredField("b");
			bpY.setAccessible(true);
			bpZ = blockPos.getDeclaredField("c");
			bpZ.setAccessible(true);
		} catch (NoSuchMethodException | ClassNotFoundException | NoSuchFieldException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private final Object thisBlockPos;
	
	public BlockPos(Object thisBlockPos) {
		if (thisBlockPos.getClass().getName().equals("BlockPos")) {
			this.thisBlockPos = thisBlockPos;
		} else {
			try {
				Object x = bpX.get(thisBlockPos);
				Object y = bpY.get(thisBlockPos);
				Object z = bpZ.get(thisBlockPos);
				this.thisBlockPos = new BlockPos((int) x, (int) y, (int) z).unwrap();
			} catch (Throwable err) {
				throw new RuntimeException(err);
			}
		}
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
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BlockPos blockPos = (BlockPos) o;
		return Objects.equals(thisBlockPos, blockPos.thisBlockPos);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(thisBlockPos);
	}
	
	public Object unwrap() {
		return thisBlockPos;
	}
}
