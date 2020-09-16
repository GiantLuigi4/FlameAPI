package com.tfc.API.flamemc.abstraction;

import com.tfc.API.flame.annotations.ASM.Unmodifiable;
import com.tfc.API.flame.utils.logging.Logger;

import java.util.HashMap;

@Unmodifiable
public class CallInfo {
	private final HashMap<String, Object> arguments;
	
	public CallInfo(HashMap<String, Object> arguments) {
		this.arguments = arguments;
	}
	
	public <A> A get(String name, Class<A> wrapper) {
		try {
			Object arg = arguments.get(name);
			return wrapper.getConstructor(arg.getClass()).newInstance(arg);
		} catch (Throwable err) {
			Logger.logLine("-------------------------------------------------------------------------------------------------------");
			Logger.logLine("INFO FOR DEVS");
			Logger.logLine("A mod tried getting an argument that isn't available: " + name + " of class " + wrapper.getName());
			Logger.logErrFullNoPrefixFull(err);
			Logger.logLine("If you see a mod name in this message, please report this to the dev of said mod");
			Logger.logLine("-------------------------------------------------------------------------------------------------------");
			return null;
		}
	}
}
