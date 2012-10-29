package test;

import org.codehaus.jackson.annotate.JsonProperty;
import java.util.*;

public class TestOutput {
	@JsonProperty("testRegexInMs") private Long testRegexInMs;
	@JsonProperty("realMap") private Map<String, RealMapEntry> realMap;
	@JsonProperty("test") private Test test;
	@JsonProperty("inventory") private List<InventoryEntry> inventory;
	@JsonProperty("bonusStatsPerLevelByType") private BonusStatsPerLevelByType bonusStatsPerLevelByType;
}
