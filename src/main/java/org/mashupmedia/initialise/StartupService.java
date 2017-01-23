package org.mashupmedia.initialise;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.service.LibraryWatchManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupService {

	@Autowired
	private LibraryWatchManager libraryWatchManager;
		

	@EventListener
	public void handleContextRefresh(ContextRefreshedEvent event) {
		ApplicationContext applicationContext =  (ApplicationContext) event.getSource();
		String id = StringUtils.trimToEmpty(applicationContext.getId());
		if (id.endsWith("dispatcher")) {
			libraryWatchManager.registerWatchLibraryListeners();	
		}
	}

}
