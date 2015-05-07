<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<label class="new-line" for="groups"><spring:message
		code="library.video.derive-title" /></label>
<form:radiobutton path="library.videoDeriveTitle"
	id="library-videoDeriveTitle-folder" value="USE_FOLDER_NAME" />
<label for="library-videoDeriveTitle-folder"><spring:message
		code="library.video.derive-title.folder" /></label>
<br />
<form:radiobutton path="library.videoDeriveTitle"
	id="library-videoDeriveTitle-file" value="USE_FILE_NAME" />
<label for="library-videoDeriveTitle-file"><spring:message
		code="library.video.derive-title.file" /></label>
<br />
<form:radiobutton path="library.videoDeriveTitle"
	id="library-videoDeriveTitle-folder-and-file"
	value="USE_FOLDER_AND_FILE_NAME" />
<label for="library-videoDeriveTitle-folder-and-file"><spring:message
		code="library.video.derive-title.folders-and-file" /></label>
<br />



<label class="new-line"><spring:message
		code="library.video.encode-video" /></label>
<form:radiobutton path="library.encodeVideoOnDemand"
	id="library-encodeVideoOnDemand1" value="true" />
<label for="library-encodeVideoOnDemand1"
	title="<spring:message
		code="library.video.encode-video.on-demand.tip" />">
	<spring:message code="library.video.encode-video.on-demand" />
</label>

<br />
<form:radiobutton path="library.encodeVideoOnDemand"
	id="library-encodeVideoOnDemand2" value="false" />
<label for="library-encodeVideoOnDemand2"
	title="<spring:message
		code="library.video.encode-video.automatically.tip" />"><spring:message
		code="library.video.encode-video.automatically" /></label>




