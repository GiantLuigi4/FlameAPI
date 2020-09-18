package com.tfc.API.flamemc.abstraction;

import com.tfc.API.flame.annotations.ASM.Unmodifiable;
import com.tfc.API.flame.utils.logging.Logger;

import java.util.HashMap;

/**
 * I can't exactly do much without this, so yeah
 */
@Unmodifiable
public class CallInfo {
	private final HashMap<String, Object> arguments;
	
	public CallInfo(HashMap<String, Object> arguments) {
		this.arguments = arguments;
	}
	
	public CallInfo(String[] names, Object[] arguments) {
		this.arguments = new HashMap<>();
		for (int i = 0; i < names.length; i++) this.arguments.put(names[i], arguments[i]);
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
			Logger.logLine("The highest mod name, if any are present, ist the one you  should contact.");
			Logger.logLine("-------------------------------------------------------------------------------------------------------");
			return null;
		}
	}
	
	public Object[] getArgs() {
		return arguments.values().toArray();
	}
}
