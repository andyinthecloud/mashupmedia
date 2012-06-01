<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document)
			.ready(
					function() {

						$
								.get(
										"<c:url value="/app/ajax/music/random-albums" />",
										function(data) {
											$("div.panel div.content").html(
													data);
										});



						$("#category-menu-home")
								.click(
										function() {
											$
													.get(
															"<c:url value="/app/ajax/music/random-albums" />",
															function(data) {
																$(
																		"div.panel div.content")
																		.html(
																				data);
															});
										});

						$("#category-menu-albums")
								.click(
										function() {
											$
													.get(
															"<c:url value="/app/ajax/music/albums" />",
															function(data) {
																$(
																		"div.panel div.content")
																		.html(
																				data);
															});
										});

						$("#category-menu-artists")
								.click(
										function() {
											$
													.get(
															"<c:url value="/app/ajax/music/artists" />",
															function(data) {
																$(
																		"div.panel div.content")
																		.html(
																				data);
															});
										});

					});
</script>

<div class="sub-panel">

	<ul class="category-menu">
		<li><a id="category-menu-home" href="javascript:void(0);"><spring:message
					code="music.menu.random-albums" /></a></li>
		<li><a id="category-menu-albums" href="javascript:void(0);"><spring:message
					code="music.menu.albums" /></a></li>
		<li><a id="category-menu-artists" href="javascript:void(0);"><spring:message
					code="music.menu.artists" /></a></li>
	</ul>

	<div class="content">
		<tiles:insertAttribute name="body" />
	</div>

	<div style="clear: both;">&nbsp;</div>


</div>

