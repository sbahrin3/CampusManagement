
var windowFocus = true;
var username;
var chatHeartbeatCount = 0;
var minChatHeartbeat = 1000;
var maxChatHeartbeat = 33000;
var chatHeartbeatTime = minChatHeartbeat;
var originalTitle;
var blinkOrder = 0;

var chatboxFocus = new Array();
var newMessages = new Array();
var newMessagesWin = new Array();
var chatBoxes = new Array();


_jq(document).ready(function(){
	originalTitle = document.title;
	//startChatSession(); <-- this method removed

	
	_jq([window, document]).blur(function(){
		windowFocus = false;
	}).focus(function(){
		windowFocus = true;
		document.title = originalTitle;
	});
	
});

function restructureChatBoxes() {
	align = 0;
	for (x in chatBoxes) {
		chatboxtitle = chatBoxes[x];

		if (_jq("#chatbox_"+chatboxtitle).css('display') != 'none') {
			if (align == 0) {
				//_jq("#chatbox_"+chatboxtitle).css('right', '20px');
				_jq("#chatbox_"+chatboxtitle).css('left', '200px');
			} else {
				width = (align)*(225+7)+20;
				_jq("#chatbox_"+chatboxtitle).css('right', width+'px');
			}
			align++;
		}
	}
}

function chatWith(chatuser) {
	createChatBox(chatuser);
	_jq("#chatbox_"+chatuser+" .chatboxtextarea").focus();
}

function createChatBox(chatboxtitle,minimizeChatBox) {
	
	
	if (_jq("#chatbox_"+chatboxtitle).length > 0) {

		if (_jq("#chatbox_"+chatboxtitle).css('display') == 'none') {
			_jq("#chatbox_"+chatboxtitle).css('display','block');
			restructureChatBoxes();
		}
		_jq("#chatbox_"+chatboxtitle+" .chatboxtextarea").focus();
		
		return;
	}

	_jq(" <div />" ).attr("id","chatbox_"+chatboxtitle)
	.addClass("chatbox")
	.html('<div class="chatboxhead"><div class="chatboxtitle">'+chatboxtitle+'</div><div class="chatboxoptions"><a href="javascript:void(0)" onclick="javascript:toggleChatBoxGrowth(\''+chatboxtitle+'\')">-</a> <a href="javascript:void(0)" onclick="javascript:closeChatBox(\''+chatboxtitle+'\')">X</a></div><br clear="all"/></div><div class="chatboxcontent"></div><div class="chatboxinput"><textarea class="chatboxtextarea" onkeydown="javascript:return checkChatBoxInputKey(event,this,\''+chatboxtitle+'\');"></textarea></div>')
	.appendTo(_jq( "body" ));
	
	_jq("#chatbox_"+chatboxtitle).css('bottom', '0px');
	
	chatBoxeslength = 0;

	for (x in chatBoxes) {
		if (_jq("#chatbox_"+chatBoxes[x]).css('display') != 'none') {
			chatBoxeslength++;
		}
	}
	
	if (chatBoxeslength == 0) {
		_jq("#chatbox_"+chatboxtitle).css('right', '20px');
	} else {
		width = (chatBoxeslength)*(225+7)+20;
		_jq("#chatbox_"+chatboxtitle).css('right', width+'px');
	}
	
	alert('ok');
	
	chatBoxes.push(chatboxtitle);

	if (minimizeChatBox == 1) {
		minimizedChatBoxes = new Array();

		if (_jq.cookie('chatbox_minimized')) {
			minimizedChatBoxes = _jq.cookie('chatbox_minimized').split(/\|/);
		}
		minimize = 0;
		for (j=0;j<minimizedChatBoxes.length;j++) {
			if (minimizedChatBoxes[j] == chatboxtitle) {
				minimize = 1;
			}
		}

		if (minimize == 1) {
			_jq('#chatbox_'+chatboxtitle+' .chatboxcontent').css('display','none');
			_jq('#chatbox_'+chatboxtitle+' .chatboxinput').css('display','none');
		}
	}
	
	

	chatboxFocus[chatboxtitle] = false;

	_jq("#chatbox_"+chatboxtitle+" .chatboxtextarea").blur(function(){
		chatboxFocus[chatboxtitle] = false;
		_jq("#chatbox_"+chatboxtitle+" .chatboxtextarea").removeClass('chatboxtextareaselected');
	}).focus(function(){
		chatboxFocus[chatboxtitle] = true;
		newMessages[chatboxtitle] = false;
		_jq('#chatbox_'+chatboxtitle+' .chatboxhead').removeClass('chatboxblink');
		_jq("#chatbox_"+chatboxtitle+" .chatboxtextarea").addClass('chatboxtextareaselected');
	});

	_jq("#chatbox_"+chatboxtitle).click(function() {
		if (_jq('#chatbox_'+chatboxtitle+' .chatboxcontent').css('display') != 'none') {
			_jq("#chatbox_"+chatboxtitle+" .chatboxtextarea").focus();
		}
	});

	_jq("#chatbox_"+chatboxtitle).show();
	
		
	
	
}


function chatHeartbeat(){

	var itemsfound = 0;
	
	if (windowFocus == false) {
 
		var blinkNumber = 0;
		var titleChanged = 0;
		for (x in newMessagesWin) {
			if (newMessagesWin[x] == true) {
				++blinkNumber;
				if (blinkNumber >= blinkOrder) {
					document.title = x+' says...';
					titleChanged = 1;
					break;	
				}
			}
		}
		
		if (titleChanged == 0) {
			document.title = originalTitle;
			blinkOrder = 0;
		} else {
			++blinkOrder;
		}

	} else {
		for (x in newMessagesWin) {
			newMessagesWin[x] = false;
		}
	}

	for (x in newMessages) {
		if (newMessages[x] == true) {
			if (chatboxFocus[x] == false) {
				//FIXME: add toggle all or none policy, otherwise it looks funny
				_jq('#chatbox_'+x+' .chatboxhead').toggleClass('chatboxblink');
			}
		}
	}
	

	//REMOVED AN AJAX CODE HERE, LOOK INTO ORIGINAL CODE FOR REFERENCE
	//$.ajax({ ----
		
}

function closeChatBox(chatboxtitle) {
	_jq('#chatbox_'+chatboxtitle).css('display','none');
	restructureChatBoxes();

	_jq.post("chat.php?action=closechat", { chatbox: chatboxtitle} , function(data){	
	});

}

function toggleChatBoxGrowth(chatboxtitle) {
	if (_jq('#chatbox_'+chatboxtitle+' .chatboxcontent').css('display') == 'none') {  
		
		var minimizedChatBoxes = new Array();
		
		if (_jq.cookie('chatbox_minimized')) {
			minimizedChatBoxes = _jq.cookie('chatbox_minimized').split(/\|/);
		}

		var newCookie = '';

		for (i=0;i<minimizedChatBoxes.length;i++) {
			if (minimizedChatBoxes[i] != chatboxtitle) {
				newCookie += chatboxtitle+'|';
			}
		}

		newCookie = newCookie.slice(0, -1)


		_jq.cookie('chatbox_minimized', newCookie);
		_jq('#chatbox_'+chatboxtitle+' .chatboxcontent').css('display','block');
		_jq('#chatbox_'+chatboxtitle+' .chatboxinput').css('display','block');
		_jq("#chatbox_"+chatboxtitle+" .chatboxcontent").scrollTop(_jq("#chatbox_"+chatboxtitle+" .chatboxcontent")[0].scrollHeight);
	} else {
		
		var newCookie = chatboxtitle;

		if (_jq.cookie('chatbox_minimized')) {
			newCookie += '|'+_jq.cookie('chatbox_minimized');
		}


		_jq.cookie('chatbox_minimized',newCookie);
		_jq('#chatbox_'+chatboxtitle+' .chatboxcontent').css('display','none');
		_jq('#chatbox_'+chatboxtitle+' .chatboxinput').css('display','none');
	}
	
}

function checkChatBoxInputKey(event,chatboxtextarea,chatboxtitle) {
	 
	if(event.keyCode == 13 && event.shiftKey == 0)  {
		message = _jq(chatboxtextarea).val();
		message = message.replace(/^\s+|\s+$/g,"");

		_jq(chatboxtextarea).val('');
		_jq(chatboxtextarea).focus();
		_jq(chatboxtextarea).css('height','44px');
		if (message != '') {
			_jq.post("chat.php?action=sendchat", {to: chatboxtitle, message: message} , function(data){
				message = message.replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/\"/g,"&quot;");
				_jq("#chatbox_"+chatboxtitle+" .chatboxcontent").append('<div class="chatboxmessage"><span class="chatboxmessagefrom">'+username+':&nbsp;&nbsp;</span><span class="chatboxmessagecontent">'+message+'</span></div>');
				_jq("#chatbox_"+chatboxtitle+" .chatboxcontent").scrollTop(_jq("#chatbox_"+chatboxtitle+" .chatboxcontent")[0].scrollHeight);
			});
		}
		chatHeartbeatTime = minChatHeartbeat;
		chatHeartbeatCount = 1;

		return false;
	}

	var adjustedHeight = chatboxtextarea.clientHeight;
	var maxHeight = 94;

	if (maxHeight > adjustedHeight) {
		adjustedHeight = Math.max(chatboxtextarea.scrollHeight, adjustedHeight);
		if (maxHeight)
			adjustedHeight = Math.min(maxHeight, adjustedHeight);
		if (adjustedHeight > chatboxtextarea.clientHeight)
			_jq(chatboxtextarea).css('height',adjustedHeight+8 +'px');
	} else {
		_jq(chatboxtextarea).css('overflow','auto');
	}
	 
}

//REMOVED A FUNCTION HERE
//function startChatSession(){   ---


/**
 * Cookie plugin
 *
 * Copyright (c) 2006 Klaus Hartl (stilbuero.de)
 * Dual licensed under the MIT and GPL licenses:
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl.html
 *
 */

jQuery.cookie = function(name, value, options) {
    if (typeof value != 'undefined') { // name and value given, set cookie
        options = options || {};
        if (value === null) {
            value = '';
            options.expires = -1;
        }
        var expires = '';
        if (options.expires && (typeof options.expires == 'number' || options.expires.toUTCString)) {
            var date;
            if (typeof options.expires == 'number') {
                date = new Date();
                date.setTime(date.getTime() + (options.expires * 24 * 60 * 60 * 1000));
            } else {
                date = options.expires;
            }
            expires = '; expires=' + date.toUTCString(); // use expires attribute, max-age is not supported by IE
        }
        // CAUTION: Needed to parenthesize options.path and options.domain
        // in the following expressions, otherwise they evaluate to undefined
        // in the packed version for some reason...
        var path = options.path ? '; path=' + (options.path) : '';
        var domain = options.domain ? '; domain=' + (options.domain) : '';
        var secure = options.secure ? '; secure' : '';
        document.cookie = [name, '=', encodeURIComponent(value), expires, path, domain, secure].join('');
    } else { // only name given, get cookie
        var cookieValue = null;
        if (document.cookie && document.cookie != '') {
            var cookies = document.cookie.split(';');
            for (var i = 0; i < cookies.length; i++) {
                var cookie = jQuery.trim(cookies[i]);
                // Does this cookie string begin with the name we want?
                if (cookie.substring(0, name.length + 1) == (name + '=')) {
                    cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                    break;
                }
            }
        }
        return cookieValue;
    }
};