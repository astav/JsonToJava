package test;

import org.codehaus.jackson.annotate.JsonProperty;
import java.util.*;

public class Rewards {
	@JsonProperty("additionalRewards") private List<Object> additionalRewards;
	@JsonProperty("goods") private Goods goods;
}
