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

Creation date: 11 mai 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

*/

package com.synpatix.toast.runtime.core.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.synaptix.toast.dao.domain.impl.test.block.BlockLine;

public class TestPageBlock {
	protected enum BlockType {
		TEST, COMMENT, VARIABLE
	}

	public final List<BlockLine> lines;
	public final BlockType blockType;

	public TestPageBlock(BlockType type) {
		lines = new ArrayList<BlockLine>();
		blockType = type;
	}

	public void addLine(List<String> cells) {
		getLines().add(new BlockLine(cells));
	}

	public void addLine(String cell) {
		getLines().add(new BlockLine(Arrays.asList(cell)));
	}

	public BlockType getBlockType() {
		return blockType;
	}

	public List<BlockLine> getLines() {
		return lines;
	}

	public BlockLine getLineAt(int i) {
		return lines.get(i);
	}
}
