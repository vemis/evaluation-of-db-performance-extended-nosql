package cz.cuni.mff.java.kurinna.common.controller;

public interface ILoaderController {
    boolean isAlreadyLoaded();
    void dropCollections();
    void loadAllData(String dataPath);
    void insertSentinel();
}
