<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">

    $(document).ready(function() {

        // Unbind declared event handlers
        $("div.dynamic-content").off("click", "#remote a.arrow-show-hide");

        $(".swipebox").swipebox();

        $("div.dynamic-content").on("click", "#remote a.arrow-show-hide", function() {
            var arrowImageSource = "<c:url value="/images/arrow-down.png" />";
            var teaserOnClassName = "teaser-on";
            var teaserOffClassName = "teaser-off";

            var containerElement = $(this).closest("#remote");
            if ($(containerElement).hasClass(teaserOnClassName)) {
                $(containerElement).removeClass(teaserOnClassName);
                $(containerElement).addClass(teaserOffClassName);
                arrowImageSource = "<c:url value="/images/arrow-up.png" />";
            } else if ($(containerElement).hasClass(teaserOffClassName)) {
                $(containerElement).removeClass(teaserOffClassName);
                $(containerElement).addClass(teaserOnClassName);
            }
            $(this).find("img").attr("src", arrowImageSource);
        });

    });

    function displayRemoteArtistInformation(data) {
        if (data.error) { 
            return; 
        }
        
        var introduction = "";
        if (data.remoteImages.length > 0) {
            var firstArtistImage = data.remoteImages[0];
            introduction = "<a class=\"swipebox artist-profile\" href=\"" + mashupMedia.contextUrl + firstArtistImage.imageUrl +"\"><img alt=\"image\" src=\"" + mashupMedia.contextUrl + firstArtistImage.thumbUrl + "\"></a>";
        }
        
        introduction += data.introduction;
        
        $("#remote div.profile").html(introduction);        

        $.each(data.remoteImages, function(index, remoteImage) {
            if (index > 0) {
        		$("#remote ul.images").append("<li class=\"box\"><a class=\"swipebox\" href=\"" + mashupMedia.contextUrl + remoteImage.imageUrl +"\"><img alt=\"image\" src=\"" + mashupMedia.contextUrl + remoteImage.thumbUrl + "\"></a></li>");
        	}
		});

        $("#remote div.disclaimer").show();
    }
</script>


