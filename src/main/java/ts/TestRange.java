package ts;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * User: Astav
 * Date: 10/21/12
 */
public class TestRange {
    @JsonProperty("min") int min;
    @JsonProperty("max") int max;
}
