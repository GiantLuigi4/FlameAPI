package com.tfc.API.flamemc.world;

import com.tfc.utils.ScanningUtils;
import entries.FlameAPI.Main;

import java.lang.reflect.Method;

public class World {
	private static final Method getBlockState;
	private static final Method setBlockState;
	
	static {
		try {
			getBlockState = Class.forName("World").getMethod("getBlockState", BlockPos.class);
			setBlockState = Class.forName("World").getMethod("setBlockState", BlockPos.class, ScanningUtils.classFor(Main.getBlockStateClass()));
		} catch (Throwable err) {
			throw new RuntimeException(err);
		}
	}
	
	private final Object thisWorld;
	
	public World(Object thisWorld) {
		this.thisWorld = thisWorld;
	}
	
	public Object getBlockState(BlockPos pos) {
		try {
			return getBlockState.invoke(thisWorld, pos);
		} catch (Throwable ignored) {
			return null;
		}
	}
	
	public Object setBlockState(BlockPos pos, Object state) {
		try {
			return setBlockState.invoke(thisWorld, pos, state);
		} catch (Throwable ignored) {
			return null;
		}
	}
}
