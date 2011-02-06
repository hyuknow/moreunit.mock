package org.moreunit.mock.templates.resolvers;

import org.moreunit.mock.model.Dependency;
import org.moreunit.mock.templates.MockingContext;
import org.moreunit.mock.templates.PatternResolver;

public class DependencyPatternsResolver implements PatternResolver
{
    private final MockingContext context;

    public DependencyPatternsResolver(MockingContext context)
    {
        this.context = context;
    }

    public String resolve(String codePattern)
    {
        if(codePattern.indexOf("${dependencyType}") == - 1 && codePattern.indexOf("${dependency}") == - 1)
        {
            return codePattern;
        }

        StringBuilder buffer = new StringBuilder();
        for (Dependency d : context.dependenciesToMock())
        {
            String resolvedTypePattern = String.format("\\${%sType:newType(%s)}", d.simpleClassName, d.fullyQualifiedClassName);
            buffer.append(codePattern.replaceAll("\\$\\{dependencyType\\}", resolvedTypePattern).replaceAll("\\$\\{dependency\\}", d.name));
        }
        return buffer.toString();
    }
}
