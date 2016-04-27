package org.mashupmedia.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.encode.ProcessManager;
import org.mashupmedia.encode.ProcessQueueItem;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/encode/queue")
public class ListEncodingProcessesController extends BaseController {

	@Autowired
	private ProcessManager processManager;

	@Autowired
	private MediaManager mediaManager;

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb processBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.encoding-processes"));
		breadcrumbs.add(processBreadcrumb);
	}

	@Override
	public String getPageTitleMessageKey() {
		return "encoding-processes.title";
	}

	public String postEncodingProcessesPage(@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment, Model model) {
		String path = getPath(isFragment, "encode/list-processes");
		return path;
		
	}
	
	
	@RequestMapping(method = RequestMethod.GET)
	public String handleGetEncodingProcessesPage(
			@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment, Model model) {
		EncodingProcessesPage encodingProcessesPage = new EncodingProcessesPage();
		encodingProcessesPage.setEncodingProcesses(new ArrayList<EncodingProcess>());
		model.addAttribute("encodingProcessesPage", encodingProcessesPage);
		model.addAttribute(MashUpMediaConstants.MODEL_KEY_IS_RELOAD, false);

		String path = getPath(isFragment, "encode/queue");
		return path;
	}

	@RequestMapping(value = "/live-update", method = RequestMethod.GET)
	public String handleGetEncodingProcesses(Model model) {
		EncodingProcessesPage encodingProcessesPage = new EncodingProcessesPage();
		List<EncodingProcess> encodingProcesses = new ArrayList<EncodingProcess>();

		Iterator<ProcessQueueItem> iterator = processManager.getProcessQueueItemsIterator();
				
		while (iterator.hasNext()) {
			ProcessQueueItem processQueueItem = iterator.next();

			EncodingProcess encodingProcess = new EncodingProcess();

			MediaItem mediaItem = mediaManager.getMediaItem(processQueueItem.getMediaItemId());
			encodingProcess.setMediaItem(mediaItem);

			MediaContentType mediaContentType = processQueueItem.getMediaContentType();
			encodingProcess.setMediaContentType(mediaContentType);

			Date createdOn = processQueueItem.getCreatedOn();
			encodingProcess.setCreatedOn(createdOn);

			Date startedOn = processQueueItem.getProcessStartedOn();
			encodingProcess.setProcessStartedOn(startedOn);

			encodingProcesses.add(encodingProcess);
		}

		encodingProcessesPage.setEncodingProcesses(encodingProcesses);

		model.addAttribute("encodingProcessesPage", encodingProcessesPage);
		model.addAttribute(MashUpMediaConstants.MODEL_KEY_IS_RELOAD, true);
		return "ajax/media/list-encoding-processes";
	}

	@RequestMapping(value = "/kill-process", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody boolean handleGetKillEncodingProcesses(@RequestParam("mediaItemId") long mediaItemId,
			@RequestParam("mediaContentType") String mediaContentTypeValue, Model model) {
		MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(mediaContentTypeValue);
		boolean isKilled = processManager.killProcess(mediaItemId, mediaContentType);
		return isKilled;
	}

	@RequestMapping(value = "/move-process", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody boolean handleGetMoveProcess(@RequestParam("index") int index,
			@RequestParam("mediaItemId") long mediaItemId,
			@RequestParam("mediaContentType") String mediaContentTypeValue, Model model) {
		MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(mediaContentTypeValue);
		boolean isMoved = processManager.moveProcess(index, mediaItemId, mediaContentType);
		return isMoved;
	}

}
