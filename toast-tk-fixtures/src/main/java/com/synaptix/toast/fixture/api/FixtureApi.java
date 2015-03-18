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

Creation date: 17 mars 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

*/

package com.synaptix.toast.fixture.api;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import com.synaptix.toast.core.annotation.Check;
import com.synaptix.toast.core.annotation.Fixture;

public class FixtureApi {

	/**
	 * List sentences defined in the framework
	 * 
	 * @return
	 */
	public static List<FixtureDescriptor> listAvailableSentences(){
		final List<FixtureDescriptor> out = new ArrayList<FixtureDescriptor>();
		final Reflections ref = new Reflections(new MethodAnnotationsScanner());
		final Set<Method> methodsAnnotatedWith = ref.getMethodsAnnotatedWith(Check.class);
		for (Method method : methodsAnnotatedWith) {
			Check annotation = method.getAnnotation(Check.class);
			Class<?> declaringClass = method.getDeclaringClass();
			Fixture docAnnotation = declaringClass.getAnnotation(Fixture.class);
			final String fixtureKind;
			if(docAnnotation != null){
				fixtureKind = docAnnotation.value().name();
			}else{
				fixtureKind = "undefined";
			}
			out.add(new FixtureDescriptor(declaringClass.getSimpleName(), fixtureKind,annotation.value()));
		}
		return out;
	}
}
