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

package com.synaptix.toast.adapter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.synaptix.toast.core.annotation.Action;
import com.synaptix.toast.core.annotation.ActionAdapter;

public class ActionAdapterCollector {

	/**
	 * List sentences defined in the framework
	 * 
	 * @return
	 */
	public static List<FixtureDescriptor> listAvailableSentences(){
		final List<FixtureDescriptor> out = new ArrayList<FixtureDescriptor>();
		final Reflections ref = new Reflections(new MethodAnnotationsScanner());
		final Set<Method> methodsAnnotatedWith = ref.getMethodsAnnotatedWith(Action.class);
		for (Method method : methodsAnnotatedWith) {
			Action annotation = method.getAnnotation(Action.class);
			Class<?> declaringClass = method.getDeclaringClass();
			ActionAdapter docAnnotation = declaringClass.getAnnotation(ActionAdapter.class);
			final String fixtureKind;
			if(docAnnotation != null){
				fixtureKind = docAnnotation.value().name();
			}else{
				fixtureKind = "undefined";
			}
			out.add(new FixtureDescriptor(declaringClass.getSimpleName(), fixtureKind,annotation.action()));
		}
		return out;
	}
	
	public static List<FixtureService> listAvailableServicesByReflection(){
		final List<FixtureService> out = new ArrayList<FixtureService>();
		final Reflections ref = new Reflections(new TypeAnnotationsScanner());
		final Set<Class<?>> services = ref.getTypesAnnotatedWith(ActionAdapter.class);
		for (Class<?> service : services) {
			if(!Modifier.isAbstract(service.getModifiers())){
				ActionAdapter docAnnotation = service.getAnnotation(ActionAdapter.class);
				out.add(new FixtureService(service, docAnnotation.value(), docAnnotation.name()));
			}
		}
		return out;
	}
	
	public static List<FixtureService> listAvailableServicesByInjection(Injector injector){
		final List<FixtureService> out = new ArrayList<FixtureService>();
		Map<Key<?>, Binding<?>> allBindings = injector.getAllBindings();
		for (Map.Entry<Key<?>, Binding<?>> bindingEntrySet : allBindings.entrySet()) {
			final Type type = bindingEntrySet.getKey().getTypeLiteral().getType();
			if (type instanceof Class) {
				final Class<?> beanClass = (Class<?>) type;
				if(beanClass.isAnnotationPresent(ActionAdapter.class)){
					ActionAdapter docAnnotation = beanClass.getAnnotation(ActionAdapter.class);
					out.add(new FixtureService(beanClass, docAnnotation.value(), docAnnotation.name()));
				}
			}
		}
		return out;
	}
}
