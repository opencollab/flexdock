Building Flexdock
--------------------------

To build this project, obtain a copy of Apache's Maven from
https://maven.apache.org. We recommend to use version 3.0 or later.

From this directory you should be able to build everything by running
the command

    mvn clean install

This will install `flexdock` into your local maven cache. In order to use it in
a `maven` project add

    <dependency>
        <groupId>org.flexdock</groupId>
        <artifactId>flexdock-core</artifactId>
        <version>1.3</version>
        <type>jar</type>
    </dependency>
    <dependency>
        <groupId>com.jgoodies</groupId>
        <artifactId>jgoodies-looks</artifactId>
        <version>2.7.0</version>
        <type>jar</type>
    </dependency>
    <dependency>
        <groupId>org.flexdock</groupId>
        <artifactId>flexdock-view</artifactId>
        <version>1.3</version>
        <type>jar</type>
    </dependency>
    <dependency>
        <groupId>org.flexdock</groupId>
        <artifactId>flexdock-perspective</artifactId>
        <version>1.3</version>
        <type>jar</type>
    </dependency>
    <dependency>
        <groupId>org.flexdock</groupId>
        <artifactId>flexdock-drag</artifactId>
        <version>1.3</version>
        <type>jar</type>
    </dependency>

Running The Demos
--------------------------

Simply run

    cd flexdock-demo
    mvn package
    java -jar flexdock-demo/target/flexdock-demo-1.3.jar

which will run a launcher for all available demos.
