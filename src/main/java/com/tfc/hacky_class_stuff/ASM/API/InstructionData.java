package com.tfc.hacky_class_stuff.ASM.API;

import com.tfc.API.flame.Hookin;

public class InstructionData {
	public final String call;
	public final Hookin.Point point;
	public final String method;
	
	public InstructionData(String call, Hookin.Point point, String method) {
		this.call = call;
		this.point = point;
		this.method = method;
	}
	
	@Override
	public String toString() {
		return "InstructionData{" +
				"call='" + call + '\'' +
				", point=" + point +
				", method='" + method + '\'' +
				'}';
	}
}
