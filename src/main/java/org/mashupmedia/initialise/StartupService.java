package org.mashupmedia.initialise;

import org.mashupmedia.service.LibraryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupService {
	
	@Autowired
	private LibraryManager libraryManager;
	
	@EventListener(ContextRefreshedEvent.class)
	public void contextRefreshedEvent() {
		libraryManager.registerWatchLibraryListeners();
	}
	

}
