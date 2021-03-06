package com.tfc.API.flamemc.blocks;

import com.tfc.API.flamemc.Registry;
import com.tfc.API.flamemc.abstraction.CallInfo;
import entries.FlameAPI.Main;

public class Block {
	private final Object thisBlock;
	private final Registry.ResourceLocation location;
	private final BlockProperties properties;
	
	/**
	 * This instances a custom block
	 *
	 * @param location   the name of the block
	 * @param properties the block properties
	 */
	public Block(Registry.ResourceLocation location, BlockProperties properties) {
		this.location = location;
		this.properties = properties;
		this.thisBlock = null;
	}
	
	/**
	 * This instances a wrapper for a vanilla block
	 *
	 * @param thisBlock the resource location for the vanilla block
	 */
	public Block(Registry.ResourceLocation thisBlock) {
		this.thisBlock = Registry.get(Registry.RegistryType.BLOCK, thisBlock);
		this.location = thisBlock;
		this.properties = null;
	}
	
	/**
	 * this is called when you block is destroyed
	 *
	 * @param arguments Arguments are (
	 *                  com.tfc.API.flamemc.world.World world,
	 *                  com.tfc.API.flamemc.world.BlockPos pos,
	 *                  com.tfc.API.flamemc.block.BlockState state
	 *                  )
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
	 * This is the one you should override, but you can also override the onAdded
	 *
	 * @param arguments Arguments are (
	 *                  com.tfc.API.flamemc.world.World world,
	 *                  com.tfc.API.flamemc.world.BlockPos pos,
	 *                  com.tfc.API.flamemc.block.BlockState state,
	 *                  %missing%,
	 *                  %missing%
	 *                  )
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
	
	/**
	 * This is called when a block around your block changes
	 *
	 * @param arguments Arguments are (
	 *                  com.tfc.API.flamemc.block.BlockState state,
	 *                  com.tfc.API.flamemc.world.World,
	 *                  idc enough to document it rn honestly
	 *                  )
	 */
	public void onUpdated(CallInfo arguments) {
	}
	
	/**
	 * due to this not being an instance of a vanilla class, you must call this method when you register
	 *
	 * @return the instance of your Block, but in a way that it can be registered
	 */
	public final Object toRegisterable() {
		try {
			return Class.forName("Block").getConstructor(Block.class, Main.getBlockPropertiesClass()).newInstance(this, this.properties.unwrap());
		} catch (Throwable err) {
			err.printStackTrace();
		}
		return null;
	}
}
