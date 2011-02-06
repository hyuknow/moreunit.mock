package org.moreunit.mock.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;

public class Dependencies extends ArrayList<Dependency>
{
    private static final long serialVersionUID = - 8786785084170298943L;

    private static final Pattern SETTER_PATTERN = Pattern.compile("^set[A-Z].*");

    private final IType classUnderTest;
    private final IType testCase;
    public final List<Dependency> constructorDependencies = new ArrayList<Dependency>();
    public final List<SetterDependency> setterDependencies = new ArrayList<SetterDependency>();
    public final List<Dependency> fieldDependencies = new ArrayList<Dependency>();

    public Dependencies(IType classUnderTest, IType testCase)
    {
        this.classUnderTest = classUnderTest;
        this.testCase = testCase;
    }

    public void compute() throws JavaModelException
    {
        initContructorDependencies();
        initSetterDependencies();
        initFieldDependencies();
    }

    private void initContructorDependencies() throws JavaModelException
    {
        int parameterCount = - 1;
        IMethod constructor = null;
        for (IMethod method : classUnderTest.getMethods())
        {
            if(method.isConstructor() && method.getNumberOfParameters() > parameterCount)
            {
                constructor = method;
                parameterCount = method.getNumberOfParameters();
            }
        }

        if(constructor != null)
        {
            String[] parameterTypes = constructor.getParameterTypes();
            String[] parameterNames = constructor.getParameterNames();

            for (int i = 0; i < parameterNames.length; i++)
            {
                Dependency dependency = new Dependency(resolveTypeSignature(parameterTypes[i]), parameterNames[i]);
                if(! contains(dependency))
                {
                    constructorDependencies.add(dependency);
                    add(dependency);
                }
            }
        }
    }

    private void initSetterDependencies() throws JavaModelException
    {
        for (IMethod method : classUnderTest.getMethods())
        {
            String methodName = method.getElementName();
            if(method.getNumberOfParameters() == 1 && SETTER_PATTERN.matcher(methodName).matches())
            {
                SetterDependency dependency = new SetterDependency(resolveTypeSignature(method.getParameterTypes()[0]), methodName);
                if(! contains(dependency))
                {
                    setterDependencies.add(dependency);
                    add(dependency);
                }
            }
        }
    }

    private String resolveTypeSignature(String typeSignature) throws JavaModelException
    {
        String fieldTypeString = Signature.toString(typeSignature);
        String[][] possibleFieldTypes = classUnderTest.resolveType(fieldTypeString);

        if(possibleFieldTypes.length != 0)
        {
            String[] fieldType = possibleFieldTypes[0];
            return fieldType[0] + "." + fieldType[1];
        }
        else
        {
            return fieldTypeString;
        }
    }

    private void initFieldDependencies() throws JavaModelException
    {
        for (IField field : classUnderTest.getFields())
        {
            if(isVisibleToTestCase(field) && isAssignable(field))
            {
                Dependency dependency = new Dependency(resolveTypeSignature(field.getTypeSignature()), field.getElementName());
                if(! contains(dependency))
                {
                    fieldDependencies.add(dependency);
                    add(dependency);
                }
            }
        }
    }

    private boolean isVisibleToTestCase(IMember member) throws JavaModelException
    {
        if((member.getFlags() & ClassFileConstants.AccPublic) != 0)
        {
            return true;
        }
        else if(classUnderTest.getPackageFragment().equals(testCase.getPackageFragment()))
        {
            return (member.getFlags() & ClassFileConstants.AccPrivate) == 0;
        }
        return false;
    }

    private boolean isAssignable(IField field) throws JavaModelException
    {
        return (field.getFlags() & ClassFileConstants.AccFinal) == 0;
    }
}
