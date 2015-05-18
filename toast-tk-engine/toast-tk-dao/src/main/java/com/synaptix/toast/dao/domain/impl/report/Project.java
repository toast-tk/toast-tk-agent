package com.synaptix.toast.dao.domain.impl.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.Index;
import com.github.jmkgreen.morphia.annotations.Indexes;
import com.github.jmkgreen.morphia.annotations.PrePersist;
import com.github.jmkgreen.morphia.annotations.Reference;
import com.synaptix.toast.core.dao.ICampaign;
import com.synaptix.toast.core.dao.IProject;
import com.synaptix.toast.core.dao.ITestPage;
import com.synaptix.toast.dao.domain.impl.common.BasicTaggableMongoBean;
import com.synaptix.toast.dao.domain.impl.test.TestPage;

@Entity(value = "report.projects")
@Indexes({ @Index(value = "name"), @Index("version") })
public class Project extends BasicTaggableMongoBean implements IProject {

	@Id
	private ObjectId id;
	private short iteration;
	
	@Reference
	private List<ICampaign> campaigns;

	public String version;

	private Date startDate;

	private Date demoDate;

	private Date prodDate;
	
	private boolean last;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<ICampaign> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(List<ICampaign> campaigns) {
		this.campaigns = campaigns;
	}

	public void setCampaignsImpl(List<Campaign> campaigns) {
		if(campaigns != null){
			this.campaigns = new ArrayList<ICampaign>();
			for (Campaign campaign : campaigns) {
				this.campaigns.add(campaign);
			}
		}else {
			this.campaigns = null;
		}
	}
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getDemoDate() {
		return demoDate;
	}

	public void setDemoDate(Date demoDate) {
		this.demoDate = demoDate;
	}

	public Date getProdDate() {
		return prodDate;
	}

	public void setProdDate(Date prodDate) {
		this.prodDate = prodDate;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public short getIteration() {
		return iteration;
	}

	public void setIteration(short iteration) {
		this.iteration = iteration;
	}

	public int getTotalOk() {
		int total = 0;
		for (ICampaign campaign : getCampaigns()) {
			for (ITestPage testPage : campaign.getTestCases()) {
				if (testPage.isSuccess()) {
					total++;
				}
			}
		}
		return total;
	}

	public int getTotalKo() {
		int total = 0;
		for (ICampaign campaign : getCampaigns()) {
			for (ITestPage testPage : campaign.getTestCases()) {
				if (!testPage.isSuccess()) {
					total++;
				}
			}
		}
		return total;
	}

	@Override
	@PrePersist
	public void prePersist() {
		// iteration++;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

}
