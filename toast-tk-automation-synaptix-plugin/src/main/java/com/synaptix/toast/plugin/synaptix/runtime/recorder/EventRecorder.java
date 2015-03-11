package com.synaptix.toast.plugin.synaptix.runtime.recorder;

import java.awt.AWTEvent;

public interface EventRecorder {

	boolean isInterestedIn(final AWTEvent awtEvent);

	void recorde(final AWTEvent awtEvent);
}