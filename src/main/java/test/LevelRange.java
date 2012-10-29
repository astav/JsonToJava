package test;

import org.codehaus.jackson.annotate.JsonProperty;
import java.util.*;

public class LevelRange {
	@JsonProperty("min") private Integer min;
	@JsonProperty("max") private Integer max;
}
