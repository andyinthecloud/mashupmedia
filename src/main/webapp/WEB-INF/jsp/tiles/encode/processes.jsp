<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">

	$(document).ready(function() {
		listProcesses();
		setInterval(function() {
			listProcesses();
		}, 5000);
		
		$("div.sub-panel").on("click", "ul.items li a.dustbin", function(){
			var id = $(this).closest("li").attr("id")
			var mediaItemId = parseId(id, "media-id");
			var mediaContentType = id.replace(/.*content-type-/g, "");

			$.get("<c:url value="/app/encode/processes/kill-process" />", {
				mediaItemId : mediaItemId,
				mediaContentType : mediaContentType
			}, function(data) {
				listProcesses();
			});			
		});		
		
	});

	function listProcesses() {
		$.get("<c:url value="/app/encode/processes/live-update" />", function(
				data) {
			$("div.sub-panel").html(data);
		});
	}
</script>

<div class="sub-panel"></div>