package com.synaptix.toast.core.adapter;

import com.synaptix.toast.core.annotation.craft.FixMe;

@FixMe(todo = "see if the driverLess kind shouldn't be replaced by an annotation @DriverLessActionAdapter")
public enum ActionAdapterKind {
	swing, web, service, driverLess
}
