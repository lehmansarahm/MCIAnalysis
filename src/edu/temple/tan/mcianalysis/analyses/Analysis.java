package edu.temple.tan.mcianalysis.analyses;

/**
 * Interface to support the incorporation of additional analysis operations 
 * into the toolkit.
 *
 * @author Philip M. Coulomb
 */
public interface Analysis {

    public void beginAnalysis(String filePath, String userID, String param1, String param2);

}