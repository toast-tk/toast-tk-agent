package com.synaptix.toast.automation.net;



/**
 * Created by skokaina on 07/11/2014.
 */
public class TableCommandRequest extends CommandRequest {

	public final TableCommandRequestQuery query;
	
	private TableCommandRequest(TableCommandRequestBuilder builder){	
		super(builder);
		this.query = builder.query;
	}
	
	public static class TableCommandRequestBuilder extends CommandRequestBuilder{
		
		private TableCommandRequestQuery query;
		
		public TableCommandRequestBuilder(String id) {
			super(id);
		}

		public TableCommandRequestBuilder count() {
	        this.action = COMMAND_TYPE.COUNT;
	        return this;
		}

		public TableCommandRequestBuilder find(String lookUpColumn, String lookUpValue, String outputColumn) {
			this.action = COMMAND_TYPE.FIND;
			this.value = lookUpValue;
			this.query = new TableCommandRequestQuery(lookUpColumn, lookUpValue, outputColumn);
			return this;
		}

		public TableCommandRequestBuilder doubleClick(String column, String value) {
			this.action = COMMAND_TYPE.DOUBLE_CLICK;
			this.value = value;
			this.query = new TableCommandRequestQuery(column);
			return this;
		}

		public TableCommandRequestBuilder selectMenu(String menu, String column, String value) {
			this.action = COMMAND_TYPE.SELECT_MENU;
			this.value = menu;
			this.query = new TableCommandRequestQuery(column, value);
			return this;
		}
		
		@Override
		public CommandRequest build() {
			return new TableCommandRequest(this);
		}
	}
	
}
