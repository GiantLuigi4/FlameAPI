package com.tfc.API.flamemc.entities;

import com.tfc.API.flame.annotations.ASM.Unmodifiable;

/**
 * This class is just here so that you can "directly" extend entity class
 * This whole class is just a dummy
 * It gets swapped out with a runtime compiled class
 * (Unmodifiable as it's literally not even loaded in a way that it can be changed)
 */
@Unmodifiable
public class Entity {
	public void tick() {
		//Changes depending on version
	}
	
	public final double getX() {
		//Changes depending on version
		return 0;
	}
	
	public final double getY() {
		//Changes depending on version
		return 0;
	}
	
	public final double getZ() {
		//Changes depending on version
		return 0;
	}
	
	public void move(double x, double y, double z) {
		//Changes depending on version
	}
	
	public void read(Object nbt) {
		//Changes depending on version
	}
	
	public void write(Object nbt) {
		//Changes depending on version
	}
	
	public void equipStack(Object slot, Object stack) {
		//Changes depending on version
	}
	
	public Object getEquippedStack(Object slot) {
		//Changes depending on version
		return null;
	}
}
