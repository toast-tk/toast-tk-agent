package com.synaptix.toast.plugin.synaptix.runtime.interpreter;

import com.synaptix.toast.core.interpret.InterpretedEvent;
import com.synaptix.toast.core.record.RecordedEvent;

public interface EventInterpreter {

	InterpretedEvent interpreteEvent(final RecordedEvent recordedEvent);
}