<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">

	$(document).ready(function() {
		listProcesses();
		setInterval(function() {
			listProcesses();
		}, 5000);
		
		$("p.processes-container").on("click", "ul.items li a.delete", function(){
			var id = $(this).closest("li").attr("id")
			var mediaItemId = parseId(id, "media-id");
			var mediaContentType = id.replace(/.*content-type-/g, "");

			$.get("<c:url value="/encode/queue/kill-process" />", {
				mediaItemId : mediaItemId,
				mediaContentType : mediaContentType
			}, function(data) {
				listProcesses();
			});			
		});		
		
	});

	function listProcesses() {
		$.get("<c:url value="/encode/queue/live-update" />", function(
				data) {
			$("p.processes-container").html(data);
		});
	}
</script>


<p class="processes-container">

</p>

