/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sodickdxfcoderui;

import java.util.prefs.Preferences;

/**
 *
 * @author matsandersson
 */
public class SodickDxfCoderPreferences {

    private static SodickDxfCoderPreferences instance = null;
    private final Preferences prefs = Preferences.userNodeForPackage(getClass());

    private final String DEFAULT_DIRECTORY = "J:\\NCDOK";
    private final String DEFAULT_DIRECTORY_KEY = "DefaultDirectory";
    private String defaultDirectory;

    protected SodickDxfCoderPreferences() {
        initPrefs();
    }

    public static SodickDxfCoderPreferences getInstance() {
        if (instance == null) {
            instance = new SodickDxfCoderPreferences();
        }
        return instance;
    }

    private void initPrefs() {
        defaultDirectory = prefs.get(DEFAULT_DIRECTORY_KEY, DEFAULT_DIRECTORY);
    }

    public String getDefaultDirectory() {
        return defaultDirectory;
    }

    public void setDefaultDirectory(String defaultDirectory) {
        this.defaultDirectory = defaultDirectory;
        prefs.put(DEFAULT_DIRECTORY_KEY, defaultDirectory);
    }

}
