<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document).ready(function() {
		listProcesses();
	});

	function listProcesses() {
		$.get(  "<c:url value="/app/encode/processes/live-update" />", function(data) {
			$("div.sub-panel").html(data);
		});
	}
</script>

<div class="sub-panel">

</div>