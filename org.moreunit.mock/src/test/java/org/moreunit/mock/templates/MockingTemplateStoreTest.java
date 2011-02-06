package org.moreunit.mock.templates;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.moreunit.mock.model.MockingTemplate;
import org.moreunit.mock.model.MockingTemplates;

public class MockingTemplateStoreTest
{
    private MockingTemplateStore templateStore;

    @Before
    public void setUp() throws Exception
    {
        templateStore = new MockingTemplateStore();

        MockingTemplate template = new MockingTemplate("template1");
        templateStore.store(template.id(), template);
    }

    @Test
    public void should_return_null_when_id_is_unknwon() throws Exception
    {
        assertThat(templateStore.get("unkown template ID")).isNull();
    }

    @Test
    public void should_return_template_when_id_is_knwon() throws Exception
    {
        // given
        MockingTemplate template = new MockingTemplate("templateID");

        // when
        templateStore.store(template.id(), template);

        // then
        assertThat(templateStore.get("templateID")).isEqualTo(template);
    }

    @Test
    public void should_not_contain_template_anymore_when_cleared() throws Exception
    {
        // given
        MockingTemplate template2 = new MockingTemplate("template2");

        // when
        templateStore.store(template2.id(), template2);

        // then
        assertThat(templateStore.get("template2")).isEqualTo(template2);
        assertThat(templateStore.get("template1")).isEqualTo(new MockingTemplate("template1"));

        // when
        templateStore.clear();

        // then
        assertThat(templateStore.get("template1")).isNull();
        assertThat(templateStore.get("template2")).isNull();
    }

    @Test
    public void should_keep_existing_templates_when_adding_new_ones() throws Exception
    {
        // when
        templateStore.store(new MockingTemplates(new MockingTemplate("templateA"), new MockingTemplate("templateB")));

        // then
        assertThat(templateStore.get("template1")).isNotNull();
        assertThat(templateStore.get("templateA")).isNotNull();
        assertThat(templateStore.get("templateB")).isNotNull();
    }
}
