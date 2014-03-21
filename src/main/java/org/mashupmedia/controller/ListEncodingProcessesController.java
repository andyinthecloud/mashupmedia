package org.mashupmedia.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mashupmedia.comparator.EncodingProcessComparator;
import org.mashupmedia.encode.ProcessContainer;
import org.mashupmedia.encode.ProcessKey;
import org.mashupmedia.encode.ProcessManager;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.EncodingProcess;
import org.mashupmedia.web.page.EncodingProcessesPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/encode/processes")
public class ListEncodingProcessesController extends BaseController {

	@Autowired
	private ProcessManager processManager;

	@Autowired
	private MediaManager mediaManager;

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb videosBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.videos"), "/app/videos");
		breadcrumbs.add(videosBreadcrumb);

		Breadcrumb processBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.encoding-processes"));
		breadcrumbs.add(processBreadcrumb);
	}

	@Override
	public String getPageTitleMessageKey() {
		return "encoding-processes.title";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String handleGetEncodingProcessesPage(Model model) {
		EncodingProcessesPage encodingProcessesPage = new EncodingProcessesPage();
		encodingProcessesPage.setEncodingProcesses(new ArrayList<EncodingProcess>());
		model.addAttribute("encodingProcessesPage", encodingProcessesPage);
		return "encode/list-processes";
	}

	@RequestMapping(value = "/live-update", method = RequestMethod.GET)
	public String handleGetEncodingProcesses(Model model) {
		EncodingProcessesPage encodingProcessesPage = new EncodingProcessesPage();
		List<EncodingProcess> encodingProcesses = new ArrayList<EncodingProcess>();

		Map<ProcessKey, ProcessContainer> processCache = processManager.getProcessCache();
		Set<ProcessKey> processkeys = processCache.keySet();

		for (Iterator<ProcessKey> iterator = processkeys.iterator(); iterator.hasNext();) {
			ProcessKey processKey = (ProcessKey) iterator.next();
			ProcessContainer processContainer = processCache.get(processKey);

			EncodingProcess encodingProcess = new EncodingProcess();

			MediaItem mediaItem = mediaManager.getMediaItem(processKey.getMediaItemId());
			encodingProcess.setMediaItem(mediaItem);

			MediaContentType mediaContentType = processKey.getMediaContentType();
			encodingProcess.setMediaContentType(mediaContentType);

			Date startedOn = processContainer.getStartedOn();
			encodingProcess.setStartedOn(startedOn);

			encodingProcesses.add(encodingProcess);
		}
		
		Collections.sort(encodingProcesses, new EncodingProcessComparator());
		encodingProcessesPage.setEncodingProcesses(encodingProcesses);

		model.addAttribute("encodingProcessesPage", encodingProcessesPage);
		return "ajax/media/list-processes.jsp";
	}
	
	@RequestMapping(value = "/kill-process/{mediaItemId}/{mediaContentType}", method = RequestMethod.GET,  produces="application/json")
	public boolean handleGetKillEncodingProcesses(@PathVariable long mediaItemId, @PathVariable String mediaContentTypeValue, Model model) {		
		MediaContentType mediaContentType = MediaItemHelper.getEncodedMediaContentType(mediaContentTypeValue);
		boolean isKilled = processManager.killProcess(mediaItemId, mediaContentType);
		return isKilled;
	}
		
	@RequestMapping(value = "/move-process/{mediaItemId}/{mediaContentType}", method = RequestMethod.GET,  consumes="application/json", produces="application/json")
	public boolean handleGetMoveProcess(@PathVariable int ranking, @PathVariable long mediaItemId, @PathVariable String mediaContentTypeValue, Model model) {		
		MediaContentType mediaContentType = MediaItemHelper.getEncodedMediaContentType(mediaContentTypeValue);
		boolean isMoved = processManager.moveProcess(ranking, mediaItemId, mediaContentType);
		return isMoved;
	}

}
