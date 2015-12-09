# twitter-lang-classifier: A large-scale language classifier for Tweets

## Installation
Our project uses Maven to import dependencies and create an executable .jar file to run.  First, clone the repo to a new directory, then traverse to the Language Identification folder:

    git clone https://github.com/jaycaz/twitter-lang-classifier.git
    cd twitter-lang-classifier/Language\ identification

Use Maven to install:

    mvn clean install -DskipTests -Dmaven.javadoc.skip=true

Then, execute the .jar file with the full classpath you would like to run.  For example, to run the RNN classifier example code, use:

    java -cp target/twitter-lang-classifier-{VersionNumber}.jar org.classifier.RNN

where VersionNumber is the current version number.

