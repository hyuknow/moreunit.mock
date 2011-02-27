package org.moreunit.mock.preferences;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Preferences
{
    private static final Set<Preference< ? >> ALL_PREFERENCES = new HashSet<Preference< ? >>();

    public static final Preference<String> MOCKING_TEMPLATE = reg(new StringPreference("mocking_template", "org.moreunit.mock.mockitoWithAnnotationsAndJUnitRunner"));

    private static <T> Preference<T> reg(Preference<T> preference)
    {
        ALL_PREFERENCES.add(preference);
        return preference;
    }

    private final PreferenceStoreManager storeManager;

    @Inject
    public Preferences(PreferenceStoreManager storeManager)
    {
        this.storeManager = storeManager;
        registerDefaultValues();
    }

    private void registerDefaultValues()
    {
        IPreferenceStore workspaceStore = storeManager.getWorkspaceStore();
        for (Preference< ? > preference : ALL_PREFERENCES)
        {
            preference.registerDefaultValue(workspaceStore);
        }
    }

    private IPreferenceStore store(IJavaProject project, boolean forWriting)
    {
        return storeManager.getStore(project, forWriting);
    }

    public void setMockingTemplate(IJavaProject project, String templateId)
    {
        store(project, true).setValue(MOCKING_TEMPLATE.name, templateId);
    }

    public String getMockingTemplate(IJavaProject project)
    {
        return store(project, false).getString(MOCKING_TEMPLATE.name);
    }

    public void setSpecificSettings(IJavaProject project, boolean projectHasSpecificSettings)
    {
        storeManager.setSpecificSettings(project, projectHasSpecificSettings);
    }

    public boolean hasSpecificSettings(IJavaProject project)
    {
        return storeManager.hasSpecificSettings(project);
    }
}
