# TODO

- algorithm
    1. look up key
    2. dimension present?
    3. yes
        - find current value for dimension, e.g. "now"
        - select *best* match (depends on dimension)
        - best match found? (falls back to default key "", if present)
        - yes
            - decend into it and repeat at item 2.
        - no
            - is "value" present?
                - yes
                    - return it
                - no
                    - bubble up and let upper level handle it
    4. no
        - return "value" (which should be present)
        
- should dimension+values and value be mutually exclusive?
    - that would require to always have a default key for to have a default value
    - but would make the tree look consistent all the time
    - i.e. a node is either a branch or a leaf, but never both

- structure
    - default value
    - unspecified dimension value, e.g. ""

- technical vs. functional?
- Spring concept
    - interoperability with
        - @Value
        - PropertySource
        - Spring Cloud Config Server
    - can it be done in a library?
- client side *plugins*
    - DimensionSelector
        - <T> Node<T> select(String key, Map<String, Node<T>> values)
        - @FunctionalInterface
     - DimensionProvider
        - String get()?
        - @FunctionalInterface
    - custom Node implementation, e.g. sorted map for optimized lookup
    - custom dimension
    - SPI, via ServiceLoader?
    - special contexts, i.e. not easily comparable with ==
        - version range
        - date: before and after
    - compass-plugin-time
        - before/after (names to be choosen by clients)
        - https://github.com/zalando/compass/wiki/after
        - https://github.com/zalando/compass/wiki/before
        - before: 2015-01-01T00:00:00Z
        - after: 2015-04-01T00:00:00+01:00
    - compass-plugin-version
        - zalando.github.io/compass/version
        - version range(-set)
        - [1.0,2.0)
    - compass-plugin-pattern
        - zalando.github.io/compass/pattern
        - patterns: regex, wildcard, ...
        - *-test@example.org
- server side *plugins* for validation?
    - overlapping time ranges
    - overlapping version ranges
    - invalid patterns