package test;

import org.codehaus.jackson.annotate.JsonProperty;
import java.util.*;

public class Quantities {
	@JsonProperty("doors") private Integer doors;
	@JsonProperty("trinkets") private Integer trinkets;
}
