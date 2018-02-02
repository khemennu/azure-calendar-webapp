package org.nwadiversity.aabrg.model;

import java.io.Serializable;
import java.time.ZonedDateTime;

import org.nwadiversity.aabrg.model.databind.DateTimeDeserializer;
import org.nwadiversity.aabrg.model.databind.DateTimeSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Admin implements Serializable {
	
	private static final long serialVersionUID = 2638777945185045511L;

	private Status status;
	
	@JsonSerialize(using=DateTimeSerializer.class)
	@JsonDeserialize(using=DateTimeDeserializer.class)
	private ZonedDateTime lastUpdatedTimestamp;
	
	private String reviewer;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public ZonedDateTime getLastUpdatedTimestamp() {
		return lastUpdatedTimestamp;
	}

	public void setLastUpdatedTimestamp(ZonedDateTime lastUpdatedTimestamp) {
		this.lastUpdatedTimestamp = lastUpdatedTimestamp;
	}

	public String getReviewer() {
		return reviewer;
	}

	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}

}
