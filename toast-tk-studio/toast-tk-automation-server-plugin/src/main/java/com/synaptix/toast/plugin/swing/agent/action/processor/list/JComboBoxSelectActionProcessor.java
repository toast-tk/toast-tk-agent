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

Creation date: 29 juin 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

*/

package com.synaptix.toast.plugin.swing.agent.action.processor.list;

import javax.swing.JComboBox;

import org.apache.commons.lang3.StringUtils;
import org.fest.swing.fixture.JComboBoxFixture;

import com.synaptix.toast.adapter.swing.utils.FestRobotInstance;
import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.core.report.TestResult;
import com.synaptix.toast.plugin.swing.agent.action.processor.ActionProcessor;

class JComboBoxSelectActionProcessor  implements ActionProcessor<JComboBox>{

	@Override
	public String processCommandOnComponent(CommandRequest command, JComboBox target) {
		JComboBoxFixture fixture = new JComboBoxFixture(FestRobotInstance.getRobot(), target);
		if (StringUtils.isNumeric(command.value)) {
			int indexToSelect = Integer.parseInt(command.value);
			if(indexToSelect >= 0 && indexToSelect < fixture.component().getItemCount()){
				fixture.selectItem(indexToSelect);
				return TestResult.ResultKind.SUCCESS.name();
			}else{
				return TestResult.ResultKind.ERROR.name();
			}
		} else {
			fixture.selectItem(command.value);
			int selectedIndex = fixture.component().getSelectedIndex();
			if(command.value.equalsIgnoreCase(fixture.valueAt(selectedIndex))){
				return TestResult.ResultKind.SUCCESS.name();
			}else{
				return TestResult.ResultKind.ERROR.name();
			}
		}
	}

}
