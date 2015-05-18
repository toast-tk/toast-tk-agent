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

Creation date: 15 avr. 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

 */

package com.synpatix.toast.runtime.core.runtime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.synaptix.toast.core.adapter.ActionAdapterSentenceRef;
import com.synaptix.toast.core.runtime.IRepositorySetup;

public class ToastRunnerHelper {

	/**
	 * @param repoSetup
	 * @param group
	 * @return
	 */
	public static Object buildArgument(IRepositorySetup repoSetup, String group) {
		group = group.replaceAll("\\*", "");
		if (group.startsWith("$$")) {
			return group.substring(1);
		} else if (group.startsWith("$") && !group.substring(1).contains("$")) {
			Object object = repoSetup.getUserVariables().get(group);
			if (object != null && object instanceof String) {
				String value = (String) object;
				Pattern p = Pattern.compile(ActionAdapterSentenceRef.VAR_IN_REGEX, Pattern.MULTILINE);
				Matcher m = p.matcher(value);
				int pos = 0;
				while (m.find()) {
					String varName = m.group(pos + 1);
					if (repoSetup.getUserVariables().containsKey(varName)) {
						Object varValue = repoSetup.getUserVariables().get(varName);
						value = value.replaceFirst("\\"+varName+"\\b", (String) varValue);
					}
				}
				object = value;
			}
			return object;
		}
		return group;
	}

	public static void main(String[] args) {

		String text = "Replaced Exact String Replace In Java";

		text = text.replaceAll("\\bReplace\\b", "Replaced");

		System.out.println(text);
	}

}
