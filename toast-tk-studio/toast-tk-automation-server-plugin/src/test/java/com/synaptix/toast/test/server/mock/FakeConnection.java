/*******************************************************************************
 *******************************************************************************/
package com.synaptix.toast.test.server.mock;

import com.esotericsoftware.kryonet.Connection;

public class FakeConnection extends Connection {

	public Object result;

	@Override
	public int sendTCP(
		Object o) {
		result = o;
		return 0;
	}
}
