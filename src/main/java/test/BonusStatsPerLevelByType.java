package test;

import org.codehaus.jackson.annotate.JsonProperty;
import java.util.*;

public class BonusStatsPerLevelByType {
	@JsonProperty("Horde") private Horde Horde;
	@JsonProperty("Solo") private Horde Solo;
	@JsonProperty("Leaders") private Horde Leaders;
}
