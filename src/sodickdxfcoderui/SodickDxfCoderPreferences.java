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

    private final double DEFAULT_EXTRA_SPACE_IN_VIEWPORT = 1.0; // 5% extra space around geometry in viewport
    private final String DEFAULT_EXTRA_SPACE_IN_VIEWPORT_KEY = "DefaultExtraSpaceInViewport";
    
    
    private String defaultDirectory;
    private double extraSpaceInViewport;

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
        extraSpaceInViewport = prefs.getDouble(DEFAULT_EXTRA_SPACE_IN_VIEWPORT_KEY, DEFAULT_EXTRA_SPACE_IN_VIEWPORT);
    }

    public String getDefaultDirectory() {
        return defaultDirectory;
    }

    public void setDefaultDirectory(String defaultDirectory) {
        this.defaultDirectory = defaultDirectory;
        prefs.put(DEFAULT_DIRECTORY_KEY, defaultDirectory);
    }

    public double getExtraSpaceInViewport() {
        return extraSpaceInViewport;
    }

    public void setExtraSpaceInViewport(double extraSpaceInViewport) {
        this.extraSpaceInViewport = extraSpaceInViewport;
        prefs.putDouble(DEFAULT_EXTRA_SPACE_IN_VIEWPORT_KEY, extraSpaceInViewport);
    }

}
