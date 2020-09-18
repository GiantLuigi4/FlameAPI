package com.tfc.API.flamemc.blocks;

import com.tfc.API.flamemc.Registry;
import com.tfc.API.flamemc.abstraction.CallInfo;
import entries.FlameAPI.Main;

public class Block {
	private final Object thisBlock;
	private final Registry.ResourceLocation location;
	private final BlockProperties properties;
	
	public Block(Registry.ResourceLocation location, BlockProperties properties) {
		this.location = location;
		this.properties = properties;
		this.thisBlock = null;
	}
	
	public Block(Registry.ResourceLocation thisBlock) {
		this.thisBlock = Registry.get(Registry.RegistryType.BLOCK, thisBlock);
		this.location = thisBlock;
		this.properties = null;
	}
	
	/**
	 * Arguments are (com.tfc.flamemc.World world, com.tfc.flamemc.BlockPos pos, com.tfc.flamemc.BlockState state)
	 *
	 * @param arguments the list of arguments
	 */
	public void onRemoved(CallInfo arguments) {
		if (thisBlock != null) {
			try {
				Main.getBlock$onRemoved().invoke(thisBlock, arguments.getArgs());
			} catch (Throwable ignored) {
			}
		}
	}
	
	/**
	 * Arguments are (com.tfc.flamemc.World world, com.tfc.flamemc.BlockPos pos, com.tfc.flamemc.BlockState state, %missing%, %missing%)
	 * This is the one you should override, but you can also override the onAdded
	 *
	 * @param arguments the list of arguments
	 */
	public void onPlaced(CallInfo arguments) {
		onAdded(arguments);
	}
	
	/**
	 * Exact same as onPlaced, I just keep typing this instead of onPlaced, so I added this
	 *
	 * @param arguments the list of arguments
	 */
	public void onAdded(CallInfo arguments) {
		if (thisBlock != null) {
			try {
				Main.getBlock$onPlaced().invoke(thisBlock, arguments.getArgs());
			} catch (Throwable ignored) {
			}
		}
	}
	
	public final Object toRegisterable() {
		try {
			return Class.forName("Block").getConstructor(Block.class, Main.getBlockPropertiesClass()).newInstance(this, this.properties.unwrap());
		} catch (Throwable err) {
			err.printStackTrace();
		}
		return null;
	}
}
