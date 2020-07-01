package com.pestphp.pest.types;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ThisTypeProvider implements PhpTypeProvider4 {
    private final Set<String> PEST_TEST_FUNCTION_NAMES = new HashSet<>(Arrays.asList("it", "test"));
    private final PhpType TEST_CASE_TYPE = new PhpType().add("\\PHPUnit\\Framework\\TestCase");

    @Override
    public char getKey() {
        return '\u0221';
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement psiElement) {
        if (!(psiElement instanceof Variable)) return null;
        if (!((Variable) psiElement).getName().equals("this")) return null;

        Function closure = PsiTreeUtil.getParentOfType(psiElement, Function.class);

        if (closure == null || !closure.isClosure()) return null;

        if (!(closure.getParent() instanceof PhpExpression)) return null;

        if (!(closure.getParent().getParent() instanceof ParameterList)) return null;

        ParameterList parameterList = (ParameterList) closure.getParent().getParent();

        if (!closure.getParent().equals(parameterList.getParameter(1))) return null;

        if (!(parameterList.getParent() instanceof FunctionReferenceImpl)) return null;

        if (!PEST_TEST_FUNCTION_NAMES.contains(((FunctionReferenceImpl) parameterList.getParent()).getName())) return null;

        return TEST_CASE_TYPE;
    }

    @Nullable
    @Override
    public PhpType complete(String s, Project project) {
        return null;
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String s, Set<String> set, int i, Project project) {
        return null;
    }
}