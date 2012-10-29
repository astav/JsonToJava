package test;

import org.codehaus.jackson.annotate.JsonProperty;
import java.util.*;

public class Test {
	@JsonProperty("thisRange") private LevelRange thisRange;
	@JsonProperty("messageOfTheDay") private String messageOfTheDay;
}
