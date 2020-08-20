package com.tfc.API.flamemc;

public class Block extends com.tfc.API.flamemc.EmptyClass {
	public Block(Registry.ResourceLocation name, Object properties) {
		super(name.unWrap(), properties);
	}
}
