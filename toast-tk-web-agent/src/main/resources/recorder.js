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
		'blur': 'HTMLEvents',
		'focus': 'HTMLEvents',
		'change': 'HTMLEvents',
		'keypress': 'KeyEvents',
		'click': 'MouseEvents',
		'dblclick': 'MouseEvents',
		'dragstart': 'MouseEvents',
		'dragend': 'MouseEvents'
	};

	var blacklistNodes = [
		'#document',
		'HMTL',
		'body'
	];

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
		if(event.type in nativeEvents 
			&& !blacklistNodes.includes(event.target.nodeName )) {
			storeEvent(event);
			return true;
		}
	};
	
	function storeEvent(event) {
		var target= 'target' in event? event.target : event.srcElement; 
		ev = convertEvent(event);
		ev.value = getTargetValue(target);
		ev.component=getTargetComponent(target);
		ev.parent=document.location.hostname.replace(/\\./g, '_');
		ev.componentName=getTargetComponentName(target);
		setTimeout(publishEvent(ev), 10);
		if(processing) {
			eventQueue.push(ev);
		} else {
			events.push(ev);
		}
	};
	
	
	function getTargetComponentName(node){
		return node.getAttribute('aria-label') 
			|| node.getAttribute('alt') 
			|| node.getAttribute('title') 
			|| node.getAttribute('name');
	};
	
	function getTargetComponent(node){
		if(node.nodeName.toLowerCase() === 'input'){
			return node.getAttribute('type');
		}
		return node.nodeName.toLowerCase();	
	};
	
	function getTargetValue(node){
		if(node.nodeName.toLowerCase() === 'input'){
			var type = node.getAttribute('type');
			if(type){
				if (type === 'text'){
					return node.value;
				}
				if (type === 'password'){ 
					return node.value;
				}
				if (type === 'date'){
					return node.value;
				}
				if(type === 'button'){
					return node.value;
				}
				if(type === 'submit'){
					return node.value;
				}
				if (type === 'date'){
					return node.value;
				}
				if (type === 'email'){
					return node.value;
				}
				if (type === 'radio'){
					return node.checked;
				}
				if (type === 'range'){
					return node.value;
				}
				if (type === 'number'){
					return node.value;
				}
				if (type === 'search'){
					return node.value;
				}
				if (type === 'time'){
					return node.value;
				}
				if (type === 'tel'){
					return node.value;
				}
				if (type === 'week'){
					return node.value;
				}
				if (type === 'month'){
					return node.value;
				}
				if (type === 'datetime'){
					return node.value;
				}
				if (type === 'url'){
					return node.value;
				}
				if (type === 'checkbox'){
					return node.checked;
				}
			}
		}
		if(node.nodeName.toLowerCase() === 'select'){
			return node.value;
		}
		if(node.nodeName.toLowerCase() === 'a'){
			return node.innerText;
		}
		if(node.nodeName.toLowerCase() === 'button'){
			return node.value;
		}

	};
	
	function publishEvent(event){
		console.log(event);
		xmlhttp.open('POST', host + '/event');
		xmlhttp.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
		xmlhttp.send(JSON.stringify(event));
	};

	function extractLocatorFromEvent(target){
		var selector = target.nodeName.toLowerCase();
		if(target.id){
			return selector += '#'+ target.id;
		}
		var id = document.body.getAttribute('id');
		if (id) { 
			selector += '#'+ id;
		}
		var classNames = target.className;
		if (classNames) {
			selector += '.' + classNames.trim().replace(/\\s/gi, '.');
		}
		return selector;
	};
	
	function convertEvent(event) {
		var target= 'target' in event? event.target : event.srcElement;
		var ev = {};
		var id = new Date().getTime() + ':' + Math.random();
		ev['id'] = id;
		ev['eventType'] = event.type;
		ev['target'] = extractLocatorFromEvent(target);
		ev['button'] = event.button;
		ev['charCode'] = event.charCode;
		ev['keyCode'] = event.keyCode;
		ev['altKey'] = event.altKey;
		ev['ctrlKey'] = event.ctrlKey;
		ev['shiftKey'] = event.shiftKey;
		var absolutePos = getPageXY(target);
		ev['offsetX'] = absolutePos[0];
		ev['offsetY'] = absolutePos[1];
		ev['path'] = getPathTo(target);
		eventHistory[id] = ev;
		return ev;
	};

	function getPathTo(element) {
	    if (element.id!=='')
	        return 'id(\"'+element.id+'\")';
	    if (element===document.body)
	        return element.tagName;

	    var ix= 0;
	    var siblings= element.parentNode.childNodes;
	    for (var i= 0; i<siblings.length; i++) {
	        var sibling= siblings[i];
	        if (sibling===element)
	            return getPathTo(element.parentNode)+'/'+element.tagName+'['+(ix+1)+']';
	        if (sibling.nodeType===1 && sibling.tagName===element.tagName)
	            ix++;
	    }
	}

	function getPageXY(element) {
	    var x= 0, y= 0;
	    while (element) {
	        x+= element.offsetLeft;
	        y+= element.offsetTop;
	        element= element.offsetParent;
	    }
	    return [x, y];
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