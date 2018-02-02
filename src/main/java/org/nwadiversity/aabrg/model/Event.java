package org.nwadiversity.aabrg.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import org.nwadiversity.aabrg.model.databind.DateDeserializer;
import org.nwadiversity.aabrg.model.databind.DateSerializer;
import org.nwadiversity.aabrg.model.databind.TimeDeserializer;
import org.nwadiversity.aabrg.model.databind.TimeSerializer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Event implements Serializable {
	
	private static final long serialVersionUID = -2766663587467176956L;
	
	private String id;
	
	private String title;
	
	@JsonSerialize(using=DateSerializer.class)
	@JsonDeserialize(using=DateDeserializer.class)
	private LocalDate date;
	
	@JsonSerialize(using=TimeSerializer.class)
	@JsonDeserialize(using=TimeDeserializer.class)
	private LocalTime startTime;
	
	@JsonSerialize(using=TimeSerializer.class)
	@JsonDeserialize(using=TimeDeserializer.class)
	private LocalTime endTime;
	
	private String contact;
	
	private String description;
	
	private String location;
	
	private String image;
	
	private Admin admin;
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}
	
}
