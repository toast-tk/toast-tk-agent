package io.toast.tk.agent.web.record.gobbler.loop;

import java.util.Objects;

import io.toast.tk.core.agent.interpret.IEventInterpreter.EventType;
import io.toast.tk.agent.web.record.EventStackGobbler;
import io.toast.tk.agent.web.record.TargetIdentifier;
import io.toast.tk.core.agent.interpret.WebEventRecord;

public abstract class LoopEventStackGobbler extends EventStackGobbler {

	WebEventRecord finalEvent = null;
	
	TargetIdentifier currentTarget = null;
	
	boolean isDirty = false;
	
	@Override
	public boolean isInterestedIn(
			WebEventRecord capturedEvent) {
		return super.isInterestedIn(capturedEvent) && !isProcessing();
	}
			

	private boolean isProcessing() {
		return Objects.nonNull(currentTarget);
	}


	public LoopEventStackGobbler digest(
			WebEventRecord capturedEvent) {
		if(!isProcessing()){
			this.currentTarget = TargetIdentifier.FromEvent(capturedEvent);
		}
		if(!isCompleted()
			&& isTargetUnchanged(capturedEvent)
			&& getStopEvents().contains(capturedEvent.getEventType())){
			this.finalEvent = capturedEvent;
		}
		if(!isCompleted()
		&& !isTargetUnchanged(capturedEvent)){
			this.isDirty = true;
		}
		return this;
	}


	private boolean isTargetUnchanged(WebEventRecord capturedEvent) {
		return this.currentTarget.equals(TargetIdentifier.FromEvent(capturedEvent));
	}

	public boolean isCompleted() {
		return !Objects.isNull(finalEvent);
	}
	
	public abstract EventType getInterpretedEventType(
			WebEventRecord capturedEvent);

	public boolean isLooper(){
		return true;
	}
	
	public boolean isDirty(){
		return this.isDirty;
	}

	@Override
	public void reset() {
		this.finalEvent = null;
		this.currentTarget = null;
		this.isDirty = false;
	}
	@Override
	public WebEventRecord getAdjustedEvent() {
		return this.finalEvent;
	}

}
