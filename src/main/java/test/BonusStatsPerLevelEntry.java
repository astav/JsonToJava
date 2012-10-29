package test;

import org.codehaus.jackson.annotate.JsonProperty;
import java.util.*;

public class BonusStatsPerLevelEntry {
	@JsonProperty("maxHP") private Float maxHP;
	@JsonProperty("baseDamage") private Float baseDamage;
	@JsonProperty("armorRating") private Float armorRating;
	@JsonProperty("xp") private Integer xp;
}
