package com.tfc.API.flamemc.blocks;

import com.tfc.API.flamemc.Registry;

public class Block extends com.tfc.API.flamemc.EmptyClass {
	public Block(Registry.ResourceLocation name, Object properties) {
		super(name.unWrap(), properties);
	}
}
