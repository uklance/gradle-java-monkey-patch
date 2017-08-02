# gradle-java-monkey-patch [![Build Status](https://travis-ci.org/uklance/gradle-java-monkey-patch.svg?branch=master)](https://travis-ci.org/uklance/gradle-java-monkey-patch)

Create a monkey patch jar by combining classes and resources from a target dependency (jar) with classes and resources from the project. The project will inherit the transitive dependencies from the target so it's a drop-in replacement for the target jar.

## Usage:

```groovy
plugins {
    id "com.lazan.java-monkey-patch" version "1.0"
}


monkeyPatch {
    target = 'org.springframework:spring-context:4.3.9.RELEASE'
}
```

The resultant jar will be a patch of the [spring-context](https://search.maven.org/#artifactdetails|org.springframework|spring-context|4.3.9.RELEASE|jar) jar with class overrides from `src/main/java` and resource overrides from `src/main/resources`. The transitive dependencies will be the same as `spring-context` (eg `spring-expression`, `spring-core`, `spring-beans` etc)
