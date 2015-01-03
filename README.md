maven-image-gallery-plugin
==========================

Maven Plugin for creating HTML image gallery

# Usage
Insert in your pom.xml something like this (you may have to can the path to your image root directory)
```
<build>
  <plugins>
    <plugin>
      <groupId>com.github.jegr78</groupId>
      <artifactId>image-gallery-maven-plugin</artifactId>
      <version>1.2.0</version>
      <configuration>
        <imagesRootDirectory>${basedir}/images</imagesRootDirectory>
      </configuration>
      <executions>
        <execution>
          <id>create-gallery</id>
          <phase>package</phase>
          <goals>
            <goal>create</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```
# Frameworks/Libraries
## Java
### imgscalr
http://www.thebuzzmedia.com/software/imgscalr-java-image-scaling-library/
### metadata-extractor
https://drewnoakes.com/code/exif/
### zip4j
http://www.lingala.net/zip4j/
### commons-io
http://commons.apache.org/proper/commons-io/
### json
http://www.json.org/java/
### slf4j
http://www.slf4j.org/
### mockito
http://mockito.org/
### junit
http://junit.org/
## Javascript
### galleria
http://galleria.io/
### jquery
http://jquery.com/
### bootstrap
http://getbootstrap.com/
