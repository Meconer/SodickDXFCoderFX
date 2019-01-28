/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UtilPkg;

import java.util.prefs.Preferences;

/**
 *
 * @author matsandersson
 */
public class SodickDxfCoderFXPreferences {

    private static SodickDxfCoderFXPreferences instance = null;
    private final Preferences prefs = Preferences.userNodeForPackage(getClass());

    private final String DEFAULT_DIRECTORY = System.getProperty("user.home");
    private final String DEFAULT_DIRECTORY_KEY = "DefaultDirectory";

    private final double DEFAULT_EXTRA_SPACE_IN_VIEWPORT = 1.1; // 10% extra space around geometry in viewport
    private final String DEFAULT_EXTRA_SPACE_IN_VIEWPORT_KEY = "DefaultExtraSpaceInViewport";
    
    private final double DEFAULT_ANGLE_CODE_ZTOP = 29.8;
    private final String DEFAULT_ANGLE_CODE_ZTOP_KEY = "DefaultAngleCodeZTop";
    
    private final double DEFAULT_ANGLE_CODE_ZBOTTOM = 27.8;
    private final String DEFAULT_ANGLE_CODE_ZBOTTOM_KEY = "DefaultAngleCodeZBottom";
    
    private final double DEFAULT_ANGLE_CODE_ANGLE = 10.0;
    private final String DEFAULT_ANGLE_CODE_ANGLE_KEY = "DefaultAngleCodeAngle";
    
    private String defaultDirectory;
    private String currentFileName;
    private double extraSpaceInViewport;
    
    private double currentAngleCodeZTop;
    private double currentAngleCodeZBottom;
    private double currentAngleCodeAngle;

    protected SodickDxfCoderFXPreferences() {
        initPrefs();
    }

    public static SodickDxfCoderFXPreferences getInstance() {
        if (instance == null) {
            instance = new SodickDxfCoderFXPreferences();
        }
        return instance;
    }

    private void initPrefs() {
        defaultDirectory = prefs.get(DEFAULT_DIRECTORY_KEY, DEFAULT_DIRECTORY);
        extraSpaceInViewport = prefs.getDouble(DEFAULT_EXTRA_SPACE_IN_VIEWPORT_KEY, DEFAULT_EXTRA_SPACE_IN_VIEWPORT);
        currentFileName = "";
        currentAngleCodeZTop = prefs.getDouble(DEFAULT_ANGLE_CODE_ZTOP_KEY, DEFAULT_ANGLE_CODE_ZTOP);
        currentAngleCodeZBottom = prefs.getDouble(DEFAULT_ANGLE_CODE_ZBOTTOM_KEY, DEFAULT_ANGLE_CODE_ZBOTTOM);
        currentAngleCodeAngle = prefs.getDouble(DEFAULT_ANGLE_CODE_ANGLE_KEY, DEFAULT_ANGLE_CODE_ANGLE);
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

    public String getCurrentFileName() {
        return currentFileName;
    }

    public void setCurrentFileName(String currentFileName) {
        this.currentFileName = currentFileName;
        System.out.println("currentFileName = " + currentFileName );
    }
    
    public double getAngleCodeZTop() {
        return currentAngleCodeZTop;
    }

    public double getCurrentAngleCodeZTop() {
        return currentAngleCodeZTop;
    }

    public void setCurrentAngleCodeZTop(double currentAngleCodeZTop) {
        this.currentAngleCodeZTop = currentAngleCodeZTop;
        prefs.putDouble(DEFAULT_ANGLE_CODE_ZTOP_KEY, currentAngleCodeZTop);
    }

    public double getCurrentAngleCodeZBottom() {
        return currentAngleCodeZBottom;
    }

    public void setCurrentAngleCodeZBottom(double currentAngleCodeZBottom) {
        this.currentAngleCodeZBottom = currentAngleCodeZBottom;
        prefs.putDouble(DEFAULT_ANGLE_CODE_ZBOTTOM_KEY, currentAngleCodeZBottom);
    }

    public double getCurrentAngleCodeAngle() {
        return currentAngleCodeAngle;
    }

    public void setCurrentAngleCodeAngle(double currentAngleCodeAngle) {
        this.currentAngleCodeAngle = currentAngleCodeAngle;
        prefs.putDouble(DEFAULT_ANGLE_CODE_ANGLE_KEY, currentAngleCodeAngle);
    }
    

}
