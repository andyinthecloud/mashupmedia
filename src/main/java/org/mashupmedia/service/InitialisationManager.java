package org.mashupmedia.service;

public interface InitialisationManager {
	public final static String DEFAULT_NAME = "Administrator";
	public final static String DEFAULT_USERNAME = "admin";
	public final static String DEFAULT_PASSWORD = "admin";

	public void initialiseApplication();

}
