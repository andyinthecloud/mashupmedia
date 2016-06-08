var jPlayerAndroidFix = (function($) {
    var fix = function(id, media, options) {
        this.playFix = false;
        this.init(id, media, options);
    };
    fix.prototype = {
        init: function(id, media, options) {
            var self = this;

            // Store the params
            this.id = id;
            this.media = media;
            this.options = options;

            // Make a jQuery selector of the id, for use by the jPlayer
            // instance.
            this.player = $(this.id);

            // Make the ready event to set the media to initiate.
            this.player.bind($.jPlayer.event.ready, function(event) {
                // Use this fix's setMedia() method.
                self.setMedia(self.media);
            });

            // Apply Android fixes
            if ($.jPlayer.platform.android) {

                // Fix playing new media immediately after setMedia.
                this.player.bind($.jPlayer.event.progress, function(event) {
                    if (self.playFixRequired) {
                        self.playFixRequired = false;

                        // Enable the controls again
                        // self.player.jPlayer('option', 'cssSelectorAncestor',
                        // self.cssSelectorAncestor);

                        // Play if required, otherwise it will wait for the
                        // normal GUI input.
                        if (self.playFix) {
                            self.playFix = false;
                            $(this).jPlayer("play");
                        }
                    }
                });
                // Fix missing ended events.
                this.player.bind($.jPlayer.event.ended, function(event) {
                    if (self.endedFix) {
                        self.endedFix = false;
                        setTimeout(function() {
                            self.setMedia(self.media);
                        }, 0);
                        // what if it was looping?
                    }
                });
                this.player.bind($.jPlayer.event.pause, function(event) {
                    if (self.endedFix) {
                        var remaining = event.jPlayer.status.duration - event.jPlayer.status.currentTime;
                        if (event.jPlayer.status.currentTime === 0 || remaining < 1) {
                            // Trigger the ended event from inside jplayer
                            // instance.
                            setTimeout(function() {
                                self.jPlayer._trigger($.jPlayer.event.ended);
                            }, 0);
                        }
                    }
                });
            }

            // Instance jPlayer
            this.player.jPlayer(this.options);

            // Store a local copy of the jPlayer instance's object
            this.jPlayer = this.player.data('jPlayer');

            // Store the real cssSelectorAncestor being used.
            this.cssSelectorAncestor = this.player.jPlayer('option', 'cssSelectorAncestor');

            // Apply Android fixes
            this.resetAndroid();

            return this;
        },
        setMedia: function(media) {
            this.media = media;

            // Apply Android fixes
            this.resetAndroid();

            // Set the media
            this.player.jPlayer("setMedia", this.media);
            return this;
        },
        play: function() {
            // Apply Android fixes
            if ($.jPlayer.platform.android && this.playFixRequired) {
                // Apply Android play fix, if it is required.
                this.playFix = true;
            } else {
                // Other browsers play it, as does Android if the fix is no
                // longer required.
                this.player.jPlayer("play");
            }
        },
        resetAndroid: function() {
            // Apply Android fixes
            if ($.jPlayer.platform.android) {
                this.playFix = false;
                this.playFixRequired = true;
                this.endedFix = true;
                // Disable the controls
                // this.player.jPlayer('option', 'cssSelectorAncestor',
                // '#NeverFoundDisabled');
            }
        }
    };
    return fix;
})(jQuery);