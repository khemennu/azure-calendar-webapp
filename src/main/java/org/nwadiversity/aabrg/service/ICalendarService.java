package org.nwadiversity.aabrg.service;

import javax.servlet.http.HttpServletResponse;

import org.nwadiversity.aabrg.model.Event;
import org.nwadiversity.aabrg.service.exception.ApplicationException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

public interface ICalendarService {

	@RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = { MediaType.APPLICATION_JSON_VALUE }, value = "/events")
	@ResponseBody
	Event createEvent(String payload) throws ApplicationException;

	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE }, value = "/events/{eventId}")
	@ResponseBody
	Event getEvent(String eventId);

	@RequestMapping(method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE }, value = "/events/{eventId}/image")
	@ResponseBody
	void updateImage(String eventId, MultipartFile file, HttpServletResponse response) throws ApplicationException;

	@RequestMapping(method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE }, value = "/events/{eventId}")
	@ResponseBody
	Event updateEvent(String eventId, String payload, HttpServletResponse response) throws ApplicationException;
}
