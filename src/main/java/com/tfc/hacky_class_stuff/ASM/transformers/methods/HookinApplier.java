package com.tfc.hacky_class_stuff.ASM.transformers.methods;

import com.tfc.API.flame.annotations.ASM.Hookin;
import com.tfc.API.flame.utils.logging.Logger;
import com.tfc.flame.FlameConfig;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import java.util.List;

public class HookinApplier extends MethodVisitor {
	protected final String methodCall;
	protected final Hookin.Point point;
	protected final String clazz;
	protected final String methodName;
	protected final MethodNode append;
	private final boolean itf = false;
	int insn = 0;
	
	public HookinApplier(int api, String methodCall, Hookin.Point point, String clazz, String methodName, MethodNode append) {
		super(api);
		this.methodCall = methodCall;
		this.point = point;
		this.clazz = clazz;
		this.methodName = methodName;
		this.append = append;
	}
	
	public HookinApplier(int api, MethodVisitor mv, String methodCall, Hookin.Point point, String clazz, String methodName, MethodNode append) {
		super(api, mv);
		this.methodCall = methodCall;
		this.point = point;
		this.clazz = clazz;
		this.methodName = methodName;
		this.append = append;
	}
	
	@Override
	public void visitCode() {
		FlameConfig.field.append("START!");
		super.visitCode();
		apply();
	}
	
	@Override
	public void visitEnd() {
		FlameConfig.field.append("END!");
		super.visitEnd();
	}
	
	@Override
	public void visitInsn(int opcode) {
		if (point == Hookin.Point.TOP && insn == 0) {
			apply();
		}
		insn++;
		super.visitInsn(opcode);
	}
	
	private void apply() {
		for (AbstractInsnNode node : append.instructions.toArray()) {
			if (node instanceof InvokeDynamicInsnNode) {
				super.visitInvokeDynamicInsn(
						((InvokeDynamicInsnNode) node).name,
						((InvokeDynamicInsnNode) node).desc,
						((InvokeDynamicInsnNode) node).bsm,
						((InvokeDynamicInsnNode) node).bsmArgs,
						false
				);
			} else if (node instanceof FieldInsnNode) {
				super.visitFieldInsn(
						node.getOpcode(),
						((FieldInsnNode) node).owner,
						((FieldInsnNode) node).name,
						((FieldInsnNode) node).desc
				);
			} else if (node instanceof JumpInsnNode) {
				super.visitJumpInsn(
						node.getOpcode(),
						((JumpInsnNode) node).label.getLabel()
				);
			} else if (node instanceof MethodInsnNode) {
				super.visitMethodInsn(
						node.getOpcode(),
						((MethodInsnNode) node).owner,
						((MethodInsnNode) node).name,
						((MethodInsnNode) node).desc,
						((MethodInsnNode) node).itf
				);
			} else if (node instanceof VarInsnNode) {
				super.visitVarInsn(
						node.getOpcode(),
						((VarInsnNode) node).var
				);
			} else if (node instanceof TypeInsnNode) {
				super.visitTypeInsn(
						node.getOpcode(),
						((TypeInsnNode) node).desc
				);
			} else if (node instanceof LookupSwitchInsnNode) {
				List<Integer> keysList = ((LookupSwitchInsnNode) node).keys;
				List<LabelNode> labelsList = ((LookupSwitchInsnNode) node).labels;
				int[] keys = new int[keysList.size()];
				Label[] labels = new Label[labelsList.size()];
				for (int i = 0; i < keysList.size(); i++) {
					keys[i] = keysList.get(i);
				}
				for (int i = 0; i < labelsList.size(); i++) {
					labels[i] = labelsList.get(i).getLabel();
				}
				super.visitLookupSwitchInsn(
						((LookupSwitchInsnNode) node).dflt.getLabel(),
						keys,
						labels
				);
			} else if (node instanceof IincInsnNode) {
				super.visitIincInsn(
						((IincInsnNode) node).var,
						((IincInsnNode) node).incr
				);
			} else if (node instanceof IntInsnNode) {
				super.visitIntInsn(
						node.getOpcode(),
						((IntInsnNode) node).operand
				);
			} else if (node instanceof LdcInsnNode) {
				super.visitLdcInsn(
						((LdcInsnNode) node).cst
				);
			} else if (node instanceof MultiANewArrayInsnNode) {
				super.visitMultiANewArrayInsn(
						((MultiANewArrayInsnNode) node).desc,
						((MultiANewArrayInsnNode) node).dims
				);
			} else if (node instanceof TableSwitchInsnNode) {
				List<LabelNode> labelsList = ((TableSwitchInsnNode) node).labels;
				Label[] labels = new Label[labelsList.size()];
				for (int i = 0; i < labelsList.size(); i++) {
					labels[i] = labelsList.get(i).getLabel();
				}
				super.visitTableSwitchInsn(
						((TableSwitchInsnNode) node).min,
						((TableSwitchInsnNode) node).max,
						((TableSwitchInsnNode) node).dflt.getLabel(),
						labels
				);
			}
			Logger.logLine(node);
			Logger.logLine(node.getOpcode());
			Logger.logLine(node.getClass().getName());
			Logger.logLine("");
		}
	}
}
