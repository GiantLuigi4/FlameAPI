package com.tfc.hacky_class_stuff.ASM;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.util.ArrayList;

//https://www.baeldung.com/java-asm
public class Writer extends ClassWriter {
	ArrayList<FieldAdder> adders = new ArrayList<>();
	
	public Writer(int flags) {
		super(flags);
	}
	
	public Writer(ClassReader classReader, int flags) {
		super(classReader, flags);
	}
	
	public void addField(String name, int access, Object defaultVal) {
		adders.add(new FieldAdder(4, this, name, defaultVal, defaultVal.getClass().getName(), access));
	}
	
	public byte[] doTransform() {
		adders.forEach(adder -> adder.visitField(0, "", "", "asm", null).visitEnd());
		return this.toByteArray();
	}
}
