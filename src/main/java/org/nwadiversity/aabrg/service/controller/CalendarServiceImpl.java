package org.nwadiversity.aabrg.service.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.json.JSONObject;
import org.nwadiversity.aabrg.connector.documentdb.ConnectionManager;
import org.nwadiversity.aabrg.model.Event;
import org.nwadiversity.aabrg.service.ICalendarService;
import org.nwadiversity.aabrg.service.exception.ApplicationException;
import org.nwadiversity.aabrg.service.exception.NotPresentException;
import org.nwadiversity.aabrg.service.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.DocumentClientException;
import com.microsoft.azure.documentdb.FeedOptions;
import com.microsoft.azure.documentdb.RequestOptions;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

@RequestMapping("/calendar")
@RestController
public class CalendarServiceImpl implements ICalendarService {

	private final static Logger LOG;

	private final String collectionPath;

	private final FeedOptions feedOptions = new FeedOptions();

	private final RequestOptions requestOptions = new RequestOptions();

	private final ObjectMapper objectMapper = new ObjectMapper();

	private static final MimeTypes DEFAULT_MIME_TYPES;

	private ConnectionManager manager;

	static {
		LOG = LoggerFactory.getLogger(CalendarServiceImpl.class);

		DEFAULT_MIME_TYPES = MimeTypes.getDefaultMimeTypes();
	}

	@Autowired
	public CalendarServiceImpl(ConnectionManager manager, @Value("${azure.documentdb.database}") String database,
			@Value("${aabrg.calendar.database}") String collection) {
		this.manager = manager;

		this.collectionPath = String.format("/dbs/%s/colls/%s", database, collection);
	}

	public Event createEvent(@RequestBody String payload) throws ApplicationException {
		JSONObject eventObject = null;
		Event event = null;
		try (DocumentClient client = manager.getRWDocClient()) {
			eventObject = new JSONObject(payload);

			event = objectMapper.readValue(eventObject.toString(), Event.class);
			event.setId(UUID.randomUUID().toString());

			client.createDocument(collectionPath, event, requestOptions, true);
		} catch (Exception ex) {
			throw new ApplicationException("Cannot process event record.");
		}
		return event;
	}

	@Override
	public Event getEvent(@PathVariable("eventId") String eventId) {
		Optional<Document> opt = null;
		try (DocumentClient client = manager.getRWDocClient()) {
			opt = client.queryDocuments(collectionPath, "select * from e where e.id = '" + eventId + "'", feedOptions)
					.getQueryIterable().toList().stream().findFirst();
		}

		if (null != opt && opt.isPresent()) {
			return opt.get().toObject(Event.class);
		} else {
			throw new NotPresentException("Could not find " + eventId);
		}
	}

	@Override
	public Event updateEvent(@PathVariable("eventId") String eventId, @RequestBody String payload,
			HttpServletResponse response) throws ApplicationException {
		JSONObject eventObject = null;
		Event event;

		Optional<Document> opt = null;
		try (DocumentClient client = manager.getRWDocClient()) {
			Document document;
			eventObject = new JSONObject(payload);
			opt = client.queryDocuments(collectionPath, "select * from e where e.id = '" + eventId + "'", feedOptions)
					.getQueryIterable().toList().stream().findFirst();
			if (null != opt && opt.isPresent()) {
				document = opt.get();
			} else {
				throw new NotPresentException("Could not find " + eventId);
			}
			for (String key : JSONObject.getNames(eventObject)) {
				Object value = eventObject.get(key);
				if (null != value && !JSONObject.NULL.equals(value)) {
					document.set(key, value);
				}
			}
			event = document.toObject(Event.class);
			client.upsertDocument(collectionPath, event, requestOptions, true);
		} catch (DocumentClientException e) {
			throw new ApplicationException("Error updating event.");
		}
		return event;
	}

	@Override
	public void updateImage(@PathVariable("eventId") String eventId, @RequestParam("image") MultipartFile imageFile,
			HttpServletResponse response) throws ApplicationException {
		CloudBlobClient client = manager.getBlobClient();
		CloudBlobContainer container;
		CloudBlockBlob image;
		String location;

		if (null == imageFile || StringUtils.isBlank(FilenameUtils.getExtension(imageFile.getOriginalFilename()))) {
			throw new ValidationException("Error uploading image. File not present or incorrect format.");
		} else {
			try {
				container = client.getContainerReference("webpage-ext");

				MimeType mime = DEFAULT_MIME_TYPES.forName(imageFile.getContentType());
				String imageName = eventId + mime.getExtension();

				image = container.getBlockBlobReference(imageName);

				byte[] imageBytes = imageFile.getBytes();
				long length = Integer.toUnsignedLong(imageBytes.length);

				image.upload(new ByteArrayInputStream(imageFile.getBytes()), length);

				location = image.getUri().toString();

				Event event = new Event();
				event.setImage(location);

				// Update
				updateEvent(eventId, objectMapper.writeValueAsString(event), response);

			} catch (URISyntaxException | StorageException | IOException | MimeTypeException e) {
				throw new ApplicationException("Error uploading image. File not present or incorrect format.");
			}
		}
		response.setStatus(HttpStatus.ACCEPTED.value());
		response.addHeader(HttpHeaders.LOCATION, location);
	}
}
