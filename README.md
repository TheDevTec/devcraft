![logo](https://raw.githubusercontent.com/TheDevTec/fang/origin/resources/logo.png "Logo Title Text 1")

# Fang

### What is Fang?

Fang is an implementation of [Minestom](https://github.com/Minestom/Minestom). <br/>
We aim to create playable multi-player minecraft server software <br/>
that is fully multi-threaded and thus performs hundreds of times <br/>
better than 'traditional' Bukkit forks, but which can also be expanded <br/>
upon with "extensions" *(basically plugins)*

## Installation

To get the .jar compile it yourself. Instructions to do so are in Compile header

Create a run script OR run it from the terminal using 

`java -Xms2000m -Xmx2000m -jar Fang.jar`

With `-Xms` being the minimum amount of ram you want to use,

`-Xmx` being the max amount of ram you want to use.

## Compile

Create a folder, then
Clone the repository using:

`git clone https://github.com/TheDevTec/fang.git`

Once it is cloned, make sure you have gradle installed, and run

`./gradlew shadowJar` on Mac or Linux, and

`gradlew shadowJar` on Windows.

This will output the jar to `build/libs` in the project directory.
