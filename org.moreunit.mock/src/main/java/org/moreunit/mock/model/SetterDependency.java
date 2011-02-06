package org.moreunit.mock.model;

import static org.moreunit.util.Preconditions.checkNotNull;

public class SetterDependency extends Dependency
{
    public final String setterMethodName;

    public SetterDependency(String fullyQualifiedClassName, String setterMethodName)
    {
        super(fullyQualifiedClassName, dependencyNameFrom(setterMethodName));
        this.setterMethodName = setterMethodName;
    }

    private static String dependencyNameFrom(String setterMethod)
    {
        checkNotNull(setterMethod);

        if(setterMethod.length() < 4 || ! setterMethod.startsWith("set"))
        {
            throw new IllegalArgumentException(String.format("'%s' is not a setter", setterMethod));
        }

        String withoutSet = setterMethod.substring(3);
        return withoutSet.substring(0, 1).toLowerCase() + withoutSet.substring(1);
    }

}
