# JavaClassReloader
The Reloader class allows to dynamically load and reload java classes.

# Features

1 - In tools like mutation testing tools, a class A will be mutated and several versions of class A will be generated.
    The Reloader class allows to reload the A class variants to achive the following :
    
    `for (String testClass : tests) {`
    ` reloader.setSpecificClassPath("<fully qualified class name of A>", "<path to a variant of A class>");`
    ` Class<?> testToRun = reloader.rload(testClass);`
    ` JUnitCore.runClasses(testToRun);`
    `}`

2 - Class variants from other paths can easily be loaded without moving or copying class files by using `reloader.setSpecificClassPath(<class>, <path>)`

3 - The Reloader have the ability to clean it self and the previsouly loaded classes but this depend on whether or not the classes can be
    garbage collected (they can be since java 1.8).

# Notes

The Reloader was initially developed for muJava++ and this repository is meant to avoid having to copy and paste the Reloader to any other
project when it's needed.

The Reloader was not originally meant to share and only to be used inside muJava++ so the documentation is lacking and some features are
not present (like loading a class from a source other than a local file).

It's better to use Java 1.8+ since from 1.8 the classes can be garbage collected.
