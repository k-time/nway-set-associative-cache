***** N-Way Set-Associative Cache Java Library *****


I.   BUILD
II.  ADDING THE LIBRARY
III. CACHE USAGE


I. BUILD

- This library is a Maven project written in Java 8.
- To build it, cd to the project home dir and run "mvn clean install".
- This produces a .jar file target/my-cache-1.0-SNAPSHOT-jar-with-dependencies.jar
  in the project home dir.
- The .jar includes the dependencies JUnit and Apache Common Collections.


II. ADDING THE LIBRARY

- Simply add the included .jar to your project classpath to use the library.
- Or, if your project is a Maven project, you can also install the library to your 
  Maven home dir and add the library by including this in your pom.xml:

    <dependencies>
        <dependency>
            <groupId>com.ktime</groupId>
            <artifactId>my-cache</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>


III. CACHE USAGE

- Create a cache using any of the constructors. Examples:

	Cache<String, Double> cache = new NWaySetCache<>();
	Cache<Character, MyClass> cache = new NWaySetCache<>(AlternativePolicy.RANDOM);
	Cache<Integer, Integer> cache = new NWaySetCache<>(4, 4, StandardPolicy.LRU);

- Use the put(K key, V val) and get(K key) methods to insert/retrieve from the cache.
- Example:

	cache.put("Kenny", 5);
	int count = cache.get("Kenny");

- To create a custom replacement policy, simply implement the interface ReplacementPolicy.
- It contains one method replace(...) to be overridden.
- When the replace(...) method is called, the linked map will be full.

	- Remove one entry by calling the linked map remove(Integer keyHash) function.
		- ex. blockMap.remove((Integer) removeBlock.getKeyHash());

	- Then, add the new block using linked map put(Integer keyHash, CacheBlock block).
		- ex. blockMap.put(block.getKeyHash(), block);

- The new block may not always need to be added; ex. if the existing blocks are heavily 
  used, then the linked map may remain unchanged according to the replacement policy.

