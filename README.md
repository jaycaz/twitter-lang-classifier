# twitter-lang-classifier: A large-scale language classifier for Tweets

## Installation
Our project uses Maven to import dependencies and create an executable .jar file to run.  First, clone the repo to a new directory, then traverse to the Language Identification folder:

    git clone https://github.com/jaycaz/twitter-lang-classifier.git
    cd twitter-lang-classifier/Language\ identification

Use Maven to install:

    mvn clean install -DskipTests -Dmaven.javadoc.skip=true

Then, execute the .jar file with the full classpath you would like to run.  For example, to run the RNN classifier example code, use:

    java -cp target/twitter-lang-classifier-{VersionNumber}.jar {ClassPath}

where VersionNumber is the current version number and ClassPath is a fully qualified classpath to one of the classes in our project.

Here is a list of all the classes you can call to run examples:

| ClassPath                         | Description                                                              |
|-----------------------------------|--------------------------------------------------------------------------|
| org.classifier.NGramClassifier    | Run ngram classifier training and scoring                                |
| org.classifier.LogisticRegression | Run Logistic Regression classifier training and scoring                  |
| org.classifier.RNN                | Run RNN classifier trainer (no scorer implemented)                       |
| org.main.RunClassifier            | Pass in a string (between double quotes) as a command line argument, and it will classify        |
| org.main.ClassifierGUI            | Loads a JavaFX GUI that you can type a string into, and it will classify |

So, here is a full example using version 0.1 and the NGramClassifier class:

    java -cp target/twitter-lang-classifier-0.1.jar org.classifier.NGramClassifier