package net.azureaaron.mod.utils.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;
import org.spongepowered.asm.mixin.injection.struct.MemberInfo;

/**
 * Custom Injection Point for finding first the {@code build method} of a {@link com.mojang.blaze3d.pipeline.RenderPipeline$Builder}
 * that occurs before a given {@code PUTFIELD} instruction since Mixin does not provide an easy way to do this.
 */
public class RenderPipelineInjectionPoint extends InjectionPoint {
	//The RenderPipeline class is not obfuscated so we do not need to handle mapping namespaces
	private static final String BUILD_OWNER = "com/mojang/blaze3d/pipeline/RenderPipeline$Builder";
	private static final String BUILD_NAME = "build";
	private static final String BUILD_DESC = "()Lcom/mojang/blaze3d/pipeline/RenderPipeline;";
	private final MemberInfo target;

	public RenderPipelineInjectionPoint(InjectionPointData data) {
		super(data);
		this.target = (MemberInfo) data.getTarget();
	}

	@Override
	public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes) {
		List<AbstractInsnNode> targetNodes = new ArrayList<>();
		ListIterator<AbstractInsnNode> iterator = insns.iterator();

		//Loop until we find the desired PUTSTATIC instruction
		outer: while (iterator.hasNext()) {
			AbstractInsnNode insn = iterator.next();

			if (insn.getOpcode() == Opcodes.PUTSTATIC && matchesTargetField((FieldInsnNode) insn)) {
				//Now that we found the instruction which sets the RenderPipeline field, backtrack until we reach the build method (if any)
				while (iterator.hasPrevious()) {
					insn = iterator.previous();

					//Look for the virtual build method of a RenderPipeline builder
					if (insn.getOpcode() == Opcodes.INVOKEVIRTUAL && matchesBuildMethod((MethodInsnNode) insn)) {
						targetNodes.add(insn);

						//We only want to find the first build method
						break outer;
					}
				}
			}
		}

		nodes.addAll(targetNodes);
		return !targetNodes.isEmpty();
	}

	private boolean matchesTargetField(FieldInsnNode field) {
		return field.owner.equals(target.getOwner()) && field.name.equals(target.getName()) && field.desc.equals(target.getDesc());
	}

	private static boolean matchesBuildMethod(MethodInsnNode method) {
		return method.owner.equals(BUILD_OWNER) && method.name.equals(BUILD_NAME) && method.desc.equals(BUILD_DESC);
	}
}
