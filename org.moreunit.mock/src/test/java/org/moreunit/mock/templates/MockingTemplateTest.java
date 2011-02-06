package org.moreunit.mock.templates;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;
import org.moreunit.mock.model.MockingTemplate;
import org.moreunit.mock.model.MockingTemplates;

public class MockingTemplateTest
{
    private MockingTemplates mockingTemplates = new MockingTemplates(new MockingTemplate("a template"), new MockingTemplate("another template"));

    @Test
    public void should_return_null_when_id_is_unknwon() throws Exception
    {
        assertThat(mockingTemplates.findTemplate("unknown")).isNull();
    }

    @Test
    public void should_return_template_when_id_is_knwon() throws Exception
    {
        assertThat(mockingTemplates.findTemplate("a template")).isEqualTo(new MockingTemplate("a template"));
        assertThat(mockingTemplates.findTemplate("another template")).isEqualTo(new MockingTemplate("another template"));
    }
}
