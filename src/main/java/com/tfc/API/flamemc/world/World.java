package com.tfc.API.flamemc.world;

import com.tfc.API.flame.utils.logging.Logger;
import com.tfc.utils.ScanningUtils;
import entries.FlameAPI.Main;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class World {
	private static final Constructor<?> instancer;
	private static final Method getBlockState;
	private static final Method setBlockState;
	
	static {
		try {
			instancer = Class.forName("World").getConstructor(ScanningUtils.classFor(Main.getWorldClass()));
			getBlockState = Class.forName("World").getMethod("getBlockState", ScanningUtils.classFor(Main.getBlockPosClass()));
			setBlockState = Class.forName("World").getMethod("setBlockState", ScanningUtils.classFor(Main.getBlockPosClass()), ScanningUtils.classFor(Main.getBlockStateClass()));
		} catch (Throwable err) {
			throw new RuntimeException(err);
		}
	}
	
	private final Object thisWorld;
	
	public World(Object thisWorld) {
		try {
			this.thisWorld = instancer.newInstance(thisWorld);
		} catch (Throwable err) {
			throw new RuntimeException(err);
		}
	}
	
	public Object getBlockState(BlockPos pos) {
		try {
			return getBlockState.invoke(thisWorld, pos.unwrap());
		} catch (Throwable ignored) {
			Logger.logErrFull(ignored);
			return null;
		}
	}
	
	public Object setBlockState(BlockPos pos, Object state) {
		try {
			return setBlockState.invoke(thisWorld, pos.unwrap(), state);
		} catch (Throwable ignored) {
			Logger.logErrFull(ignored);
			return null;
		}
	}
	
	public Object unwrap() {
		return thisWorld;
	}
}
