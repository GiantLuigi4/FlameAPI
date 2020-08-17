package com.tfc.hacky_class_stuff.ASM;


import com.tfc.utils.TriObject;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class ClassObject extends TriObject<ClassReader, ClassNode, ClassWriter> {
	public ClassObject(ClassReader obj1, ClassNode obj2, ClassWriter obj3) {
		super(obj1, obj2, obj3);
	}
}
