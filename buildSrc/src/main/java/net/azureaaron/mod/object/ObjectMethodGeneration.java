package net.azureaaron.mod.object;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.azureaaron.mod.ClassReference;
import net.azureaaron.mod.FieldReference;
import net.azureaaron.mod.MethodReference;

/**
 * Record representing information about an {@link Object} method to generate in a class.
 * 
 * @param objectMethodType    The type method that we are generating.
 * @param target              The target method to replace in the class.
 * @param classReference      The reference to the class that the method will be generated in.
 * @param fieldReferences     The fields to incorporate in the generated method (e.g. what fields will be used for equals, hashCode, and toString).
 * @param superClassReference The reference to the class' superclass. Only applicable to equals and hashCode, and is only non-null if the super class should be considered by the operation.
 */
public record ObjectMethodGeneration(MethodReference target, ObjectMethodType objectMethodType, ClassReference classReference, List<FieldReference> fieldReferences, @Nullable ClassReference superClassReference) {
}
