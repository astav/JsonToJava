package test;

import org.codehaus.jackson.annotate.JsonProperty;
import java.util.*;

public class InventoryEntry {
	@JsonProperty("icon") private Object icon;
	@JsonProperty("completedBestId") private Object completedBestId;
	@JsonProperty("levelRange") private LevelRange levelRange;
	@JsonProperty("rewards") private Rewards rewards;
	@JsonProperty("recommended") private Boolean recommended;
	@JsonProperty("identifier") private String identifier;
	@JsonProperty("cost") private Cost cost;
	@JsonProperty("discount") private Integer discount;
}
