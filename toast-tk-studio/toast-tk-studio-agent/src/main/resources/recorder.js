function recorder(){
    var events = [];
	var eventQueue = [];
	var eventHistory = {};
	var processing = false;
	var port = location.protocol === 'https:' ? 4445 : 4444;
	var host = location.protocol + '//localhost:'+port+'/record';
	
	var xmlhttp = new XMLHttpRequest();

	var nativeEvents = {
		'submit': 'HTMLEvents',
		'keypress': 'KeyEvents',
		'click': 'MouseEvents',
		'dblclick': 'MouseEvents',
		'dragstart': 'MouseEvents',
		'dragend': 'MouseEvents'
	};

	function initEvent(){ 
		if(document.body.getAttribute('recording') == undefined){
			for(eventName in nativeEvents) {
				document.addEventListener(eventName, processEvent, true);
			}
			document.body.setAttribute('recording', true);
		}
	};
	
	function processEvent(event) {
		if(event.triggeredManually) {
			return true;
		}
		if(event.type in nativeEvents) {
			storeEvent(event);
			return true;
		}
	}
	
	function storeEvent(event) {
		ev = convertEvent(event);
		setTimeout(publishEvent(ev), 10);
		if(processing) {
			eventQueue.push(ev);
		} else {
			events.push(ev);
		}
	}
	
	function publishEvent(event){
		console.log(event);
		xmlhttp.open('POST', host + '/event');
		xmlhttp.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
		xmlhttp.send(JSON.stringify(event));
	}

	function extractLocatorFromEvent(target){
		var selector = target.nodeName.toLowerCase();
		var id = document.body.getAttribute('id');
		if (id) { 
			selector += '#'+ id;
		}
		var classNames = document.body.getAttribute('class');
		console.log('classes: ' + classNames);
		if (classNames) {
			selector += '.' + classNames.trim().replace(/\\s/gi, '.');
		}
		return selector;
	}
	
	function convertEvent(event) {
		var ev = {};
		var id = new Date().getTime() + ':' + Math.random();
		ev['id'] = id;
		ev['type'] = event.type;
		ev['target'] = extractLocatorFromEvent(event.target);
		ev['button'] = event.button;
		ev['charCode'] = event.charCode;
		ev['keyCode'] = event.keyCode;
		ev['altKey'] = event.altKey;
		ev['ctrlKey'] = event.ctrlKey;
		ev['shiftKey'] = event.shiftKey;
		ev['clientX'] = event.clientX;
		ev['clientY'] = event.clientY;
		ev['offsetX'] = event.offsetX;
		ev['offsetY'] = event.offsetY;
		eventHistory[id] = ev;
		return ev;
	}

	function getEvents() {
		processing = true;
		events = events.concat(eventQueue);
		eventQueue = [];
		setTimeout(resetEvents, 10);
		return events;
	};

	function resetEvents() {
		events = [];
		processing = false;
	};

	function triggerEvents(idsAsJson) {
		var ids = JSON.parse(idsAsJson);
		for (var i = 0; i < ids.length; i++) {
			var event = eventHistory[ids[i]];
			if(event) {
				var evObj = null;
				var evObjType = null;
				var bubbling = true;
				var cancelable = false;

				if(event['type'] in nativeEvents) {
					evObjType = nativeEvents[event['type']];
					evObj = document.createEvent(evObjType);
					if(evObjType == 'KeyEvents') {
						evObj.initKeyEvent(event['type'], bubbling, cancelable, window, event['ctrlKey'], event['altKey'], event['shiftKey'], false, event['keyCode'], event['charCode']);
					} else if(evObjType == 'MouseEvents') {
						evObj.initMouseEvent(event['type'], bubbling, cancelable, window, 1, event['offsetX'], event['offsetY'], event['clientX'], event['clientY'], event['ctrlKey'], event['altKey'], event['shiftKey'], false, event['button'], null);
					} else {
						evObj.initEvent(event['type'], bubbling, cancelable);
					}
					evObj.triggeredManually = true;
					event['target'].dispatchEvent(evObj);
				}
			}
		}
	};
	
	initEvent();
}

recorder();