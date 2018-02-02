package org.nwadiversity.aabrg.model.databind;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;


public class TimeSerializer extends JsonSerializer<LocalTime> {


	@Override
	public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider arg2)
			throws IOException, JsonProcessingException {
		gen.writeString(value.format(DateTimeFormatter.ISO_TIME));
	}
}
