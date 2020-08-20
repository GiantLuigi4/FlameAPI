package com.tfc.hacky_class_stuff.ASM.API;

import com.tfc.API.flame.annotations.ASM.Hookin;
import org.objectweb.asm.tree.MethodNode;

public class InstructionData {
	public final String call;
	public final Hookin.Point point;
	public final String method;
	public final MethodNode node;
	
	public InstructionData(String call, Hookin.Point point, String method, MethodNode node) {
		this.call = call;
		this.point = point;
		this.method = method;
		this.node = node;
	}
	
	@Override
	public String toString() {
		return "InstructionData{" +
				"call='" + call + '\'' +
				", point=" + point +
				", method='" + method + '\'' +
				", node=" + node +
				'}';
	}
}
