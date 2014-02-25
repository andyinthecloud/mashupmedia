<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<label class="new-line" for="groups"><spring:message code="library.video.resolutions" /></label>
<form:checkboxes path="library.videoResolutions" items="${videoResolutions}" itemLabel="name" itemValue="id" cssClass="checkboxes" delimiter="<br/>" />
<br />

<label class="new-line" for="groups"><spring:message code="library.video.derive-title" /></label>
<form:radiobutton path="library.videoDeriveTitle" id="library-videoDeriveTitle-folder" value="USE_FOLDER_NAME" />
<label for="library-videoDeriveTitle-folder"><spring:message code="library.video.derive-title.folder" /></label>
<br />
<form:radiobutton path="library.videoDeriveTitle" id="library-videoDeriveTitle-file" value="USE_FILE_NAME" />
<label for="library-videoDeriveTitle-file"><spring:message code="library.video.derive-title.file" /></label>
<br />

<form:radiobutton path="library.videoDeriveTitle" id="library-videoDeriveTitle-file" value="USE_FOLDER_AND_FILE_NAME" />
<label for="library-videoDeriveTitle-file"><spring:message code="library.video.derive-title.folders-and-file" /></label>
<br />
<br />


