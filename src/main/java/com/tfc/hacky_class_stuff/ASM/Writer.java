package com.tfc.hacky_class_stuff.ASM;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;

//https://www.baeldung.com/java-asm#1-working-with-fields
/**
 * idk why this exists tbh
 * afraid to delete it
 */
@Deprecated
public class Writer extends ClassWriter {
	ArrayList<FieldAdder> adders = new ArrayList<>();
	
	public Writer(int flags) {
		super(flags);
	}
	
	private ClassReader reader = null;
	
	public Writer(ClassReader classReader, int flags) {
		super(classReader, flags);
		reader = classReader;
	}
	
	public void addField(String name, int access, Object defaultVal) {
		adders.add(new FieldAdder(Opcodes.ASM4, this, name, defaultVal, defaultVal.getClass().getName(), access));
	}
	
	public byte[] doTransform() {
		adders.forEach(adder -> {
			if (reader != null) {
				reader.accept(adder, 0);
			}
		});
		return this.toByteArray();
	}
}
