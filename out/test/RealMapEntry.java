package test;

import org.codehaus.jackson.annotate.JsonProperty;
import java.util.*;

public class RealMapEntry {
	@JsonProperty("foo") private Integer foo;
	@JsonProperty("bar") private String bar;
}
