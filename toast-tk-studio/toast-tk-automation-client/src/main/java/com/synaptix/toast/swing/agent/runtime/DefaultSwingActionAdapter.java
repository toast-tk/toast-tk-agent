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

Creation date: 16 févr. 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

*/

package com.synaptix.toast.swing.agent.runtime;

import com.google.inject.Inject;
import com.synaptix.toast.adapter.swing.ToastSwingActionAdapter;
import com.synaptix.toast.automation.driver.swing.SwingClientDriver;
import com.synaptix.toast.core.adapter.ActionAdapterKind;
import com.synaptix.toast.core.annotation.ActionAdapter;
import com.synaptix.toast.core.runtime.IRepositorySetup;

@ActionAdapter(value = ActionAdapterKind.swing, name = "ToastSwingClientAdapter")
public class DefaultSwingActionAdapter extends ToastSwingActionAdapter{

	@Inject
	public DefaultSwingActionAdapter(IRepositorySetup repo, SwingClientDriver driver) {
		super(repo, driver);
	}


}
