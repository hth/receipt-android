package com.receiptofi.receipts.model;

/**
 * User: hitender
 * Date: 7/14/15 9:26 AM
 */
public class ApkVersionModel {
    private int major;
    private int minor;
    private int patch;
    private int build;

    public ApkVersionModel(int major, int minor, int patch, int build) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.build = build;
    }

    public ApkVersionModel(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public int getBuild() {
        return build;
    }

    public String version() {
        return major + "." + minor + "." + patch;
    }

    @Override
    public String toString() {
        return "ApkVersionModel{" +
                "major=" + major +
                ", minor=" + minor +
                ", patch=" + patch +
                ", build=" + build +
                '}';
    }
}
