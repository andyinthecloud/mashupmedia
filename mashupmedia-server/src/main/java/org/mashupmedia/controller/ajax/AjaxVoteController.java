package org.mashupmedia.controller.ajax;

import org.mashupmedia.model.User;
import org.mashupmedia.service.VoteManager;
import org.mashupmedia.util.AdminHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/ajax/vote")
public class AjaxVoteController extends AjaxBaseController {

	public static final String MODEL_KEY_IS_SUCCESSFUL = "isSuccessful";

	@Autowired
	private VoteManager voteManager;

	@RequestMapping(value = "/like", method = RequestMethod.POST)
	public String handleLikeVote(@RequestParam("mediaItemId") Long mediaItemId, Model model) {
		User user = AdminHelper.getLoggedInUser();
		long userId = user.getId();

		boolean isSuccessful = voteManager.voteLike(mediaItemId, userId);
		model.addAttribute(MODEL_KEY_IS_SUCCESSFUL, isSuccessful);
		return "ajax/message";
	}

	@RequestMapping(value = "/dislike", method = RequestMethod.POST)
	public String handleDislikeVote(@RequestParam("mediaItemId") Long mediaItemId, Model model) {
		User user = AdminHelper.getLoggedInUser();
		long userId = user.getId();

		boolean isSuccessful = voteManager.voteDislike(mediaItemId, userId);
		model.addAttribute(MODEL_KEY_IS_SUCCESSFUL, isSuccessful);
		return "ajax/message";
	}

}
