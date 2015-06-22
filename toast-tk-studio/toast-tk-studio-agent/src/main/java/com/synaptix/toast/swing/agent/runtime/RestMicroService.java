/**Copyright (c) 2013-2015, Synaptix Labs
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Creation date: 11 juin 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

 */

package com.synaptix.toast.swing.agent.runtime;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

import com.synaptix.toast.constant.Property;

public class RestMicroService extends Verticle {

	static StartCommandHandler startHandler = new StartCommandHandler();

	@Override
	public void start() {
		RouteMatcher matcher = new RouteMatcher();
		
		initRouteMatcher(matcher);
		
		vertx.createHttpServer().requestHandler(matcher).listen(Property.TOAST_AGENT_PORT);
	}

	private void initRouteMatcher(RouteMatcher matcher) {
		matcher.get("/rus/init", new Handler<HttpServerRequest>() {
			@Override
			public void handle(HttpServerRequest req) {
				boolean ok = startHandler.init();
				if(ok){
					req.response().setStatusCode(200).end();
				}else{
					req.response().setStatusCode(404).end();
				}
			}
		});
		
		matcher.get("/rus/start", new Handler<HttpServerRequest>() {
			@Override
			public void handle(HttpServerRequest req) {
				startHandler.start();
				req.response().setStatusCode(200).end();				
			}
			
		});
		
		matcher.get("/rus/stop", new Handler<HttpServerRequest>() {
			@Override
			public void handle(HttpServerRequest req) {
				startHandler.stop();
				req.response().setStatusCode(200).end();				
			}
		});
	}
}
