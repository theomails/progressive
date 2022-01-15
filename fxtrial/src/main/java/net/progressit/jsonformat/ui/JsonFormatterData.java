package net.progressit.jsonformat.ui;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class JsonFormatterData{
	private final String inputJson;
	private final boolean prettyPrint;
	private final boolean serializeNulls;
}