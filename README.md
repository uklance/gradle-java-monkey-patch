# gradle-java-monkey-patch

Create a monkey patch jar by combining class and resources from a target dependency with resources and classes in the project.

## Usage:

```groovy
apply plugin: 'com.lazan.java-monkey-patch'

monkeyPatch {
    target = 'org.springframework:spring-context:4.3.9.RELEASE'
}
```

The resultant jar will be a patch of the [spring-context](https://search.maven.org/#artifactdetails|org.springframework|spring-context|4.3.9.RELEASE|jar) jar with class overrides from `src/main/java` and resource overrides from `src/main/resources`. The transitive dependencies will be the same as `spring-context` (eg `spring-expression`, `spring-core`, `spring-beans` etc)
