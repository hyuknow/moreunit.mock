package org.moreunit.mock.templates.resolvers;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.moreunit.mock.model.Dependencies;
import org.moreunit.mock.model.Dependency;
import org.moreunit.mock.templates.MockingContext;

@RunWith(MockitoJUnitRunner.class)
public class DependencyPatternsResolverTest
{
    @Mock
    private MockingContext context;

    private Dependencies dependencies;
    private DependencyPatternsResolver resolver;

    @Before
    public void createResolver() throws Exception
    {
        dependencies = new Dependencies(null, null);
        when(context.dependenciesToMock()).thenReturn(dependencies);
        resolver = new DependencyPatternsResolver(context);
    }

    @Test
    public void should_return_unmodified_pattern_when_does_not_match() throws Exception
    {
        assertThat(resolver.resolve("does not match")).isEqualTo("does not match");
    }

    @Test
    public void should_return_empty_string_when_there_are_no_dependencies() throws Exception
    {
        assertThat(resolver.resolve("a ${dependency} b ${dependencyType}${dependency} c")).isEqualTo("");
    }

    @Test
    public void should_resolve_dependency() throws Exception
    {
        // given
        dependencies.add(new Dependency("some.where.Foo", "bar"));

        // then
        assertThat(resolver.resolve("pre ${dependency} between ${dependency} post"))
                .isEqualTo("pre bar between bar post");
    }

    @Test
    public void should_resolve_dependencyType() throws Exception
    {
        // given
        dependencies.add(new Dependency("some.where.Foo", "bar"));

        // then
        assertThat(resolver.resolve("pre ${dependencyType} between ${dependencyType} post"))
                .isEqualTo("pre ${FooType:newType(some.where.Foo)} between ${FooType:newType(some.where.Foo)} post");
    }

    @Test
    public void should_resolve_dependency_and_dependencyType() throws Exception
    {
        // given
        dependencies.add(new Dependency("some.where.Foo", "bar"));

        // then
        assertThat(resolver.resolve("a ${dependencyType} b ${dependency} c ${dependencyType} d ${dependency} e"))
                .isEqualTo("a ${FooType:newType(some.where.Foo)} b bar c ${FooType:newType(some.where.Foo)} d bar e");
    }

    @Test
    public void should_reproduce_pattern_for_each_dependency() throws Exception
    {
        // given
        dependencies.add(new Dependency("pack.age.Foo", "foo"));
        dependencies.add(new Dependency("some.where.Thing", "bar"));
        dependencies.add(new Dependency("BlobClass", "aBlob"));

        // then
        assertThat(resolver.resolve("a ${dependencyType} b ${dependency} c"))
                .isEqualTo("a ${FooType:newType(pack.age.Foo)} b foo c" +
                           "a ${ThingType:newType(some.where.Thing)} b bar c" +
                           "a ${BlobClassType:newType(BlobClass)} b aBlob c");
    }
}
