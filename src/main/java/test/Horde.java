package test;

import org.codehaus.jackson.annotate.JsonProperty;
import java.util.*;

public class Horde {
	@JsonProperty("bonusStatsPerLevel") private Map<String, BonusStatsPerLevelEntry> bonusStatsPerLevel;
}
