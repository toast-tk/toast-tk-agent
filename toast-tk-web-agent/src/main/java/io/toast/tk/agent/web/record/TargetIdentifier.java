package io.toast.tk.agent.web.record;

import io.toast.tk.core.agent.interpret.WebEventRecord;

public class TargetIdentifier {

	private final int x;
	
	private final int y;
	
	private final String path;
	
	private TargetIdentifier(String path, int offsetX, int offsetY) {
		this.path = path;
		this.x = offsetX;
		this.y = offsetY;
	}

	public static TargetIdentifier FromEvent(WebEventRecord record){
		return new TargetIdentifier(record.getPath(), record.getOffsetX(), record.getOffsetY());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TargetIdentifier other = (TargetIdentifier) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
}
