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

Creation date: 10 mars 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

*/

package com.synaptix.toast.swing.agent.interpret;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.synaptix.toast.automation.config.Config;
import com.synaptix.toast.dao.api.RestMongoWrapper;
import com.synaptix.toast.dao.domain.impl.repository.ElementImpl;
import com.synaptix.toast.dao.domain.impl.repository.RepositoryImpl;

public class MongoRepoManager {

	private static final Logger LOG = LoggerFactory.getLogger(MongoRepoManager.class);
	Collection<RepositoryImpl> cache = null;
	private Config config;
	
	@Inject
	public MongoRepoManager(Config config){
		this.config= config;
	}
	
	public void initCache(){
		cache = RestMongoWrapper.loadRepository(config.getWebAppAddr(), config.getWebAppPort());
	}
	
	public String find(RepositoryImpl container, String type, String locator) {
		for(RepositoryImpl repImpl: cache){
			if(repImpl.getName().equals(container.getName())){
				if(repImpl.rows != null){
					for(ElementImpl element: repImpl.rows){
						if(element.locator.equalsIgnoreCase(locator.toLowerCase())){
							return "".equals(element.name) || element.name == null ? element.locator : element.name;
						}
					}	
				}
			}
		}
		ElementImpl impl = extractElement(type, locator);
		container.rows.add(impl);
		return impl.name;
	}

	private ElementImpl extractElement(String type, String locator) {
		ElementImpl impl = new ElementImpl();
		impl.locator = locator;
		if(locator.contains(":")){
			impl.name = locator.split(":")[1];
		}else{
			impl.name = locator.contains(".") ? impl.type + "-" + UUID.randomUUID().toString() : locator;
		}
		impl.name = formatLabel(impl.name);
		impl.type = type;
		return impl;
	}

	public RepositoryImpl findContainer(String lastKnownContainer) {
		lastKnownContainer = formatLabel(lastKnownContainer);
		for(RepositoryImpl repImpl: cache){
			if(repImpl.getName().equals(lastKnownContainer)){
				return repImpl;
			}
		}
		RepositoryImpl repImpl = new RepositoryImpl();
		repImpl.setName(lastKnownContainer);
		repImpl.type = "swing page";	
		cache.add(repImpl);
		return repImpl;
	}

	private String formatLabel(String name) {
		return name.trim().replace(" ", "_").replace("'", "_").replace("°", "_");
	}

	public boolean saveCache() {
		boolean saveRepository = RestMongoWrapper.saveRepository(cache, config.getWebAppAddr(), config.getWebAppPort());
		initCache();
		return saveRepository;
	}

	public String getWikiFiedRepo() {
		if(cache == null){
			initCache();
		}
		String res = "";
		for(RepositoryImpl page: cache){
			res += "#Page id:" + page.getId().toString() + "\n";
			res += "|| auto setup ||\n";
			res += "| " + page.type + " | " + page.name + " |\n";
			res += "| name | type | locator |\n";
			if(page.rows != null){
				for (ElementImpl row: page.rows) {
					res += "|" + row.name + "|" + row.type + "|" + row.locator + "|\n";
				}
			}
			res += "\n";
		}
		return res;
	}
}
