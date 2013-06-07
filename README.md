JsonToJava
----------
A JsonToJava source class file generator that deduces the schema based on supplied sample json data and generate the necessary java data structures.

It encourages teams to think in Json first, before writing actual code.

Features
----------
Can generate classes for an arbitrarily complex hierarchy (recursively)
Can read your existing Java classes and if it can deserialize into those structures, will do so
Will prompt for user input when ambiguous cases exist
Tested on java version 1.6

Tips
----------
Specify all values for json fields with non-null values. By doing so, the generator will try to re-use classes it already generated in other structures as long as the class can be parsed back from json into the generated class.

Limitations
----------
Uses Jackson and generates @JsonProperty annotations for json fields
Can't discover and create abstract types
Can't collate unspecified fields across different structures into the same class when missing information
Doesn't support byte, short and char types yet
Running the sample

$ ./sample-run.sh
