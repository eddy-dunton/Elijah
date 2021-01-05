# Elijah

Automobile track telemetry sytem and lap tracking tool

Designed to be used on the track, although also works on the street

Uses a lightweight Arduino system to on the car / bike in order to live track the vehicle, then a Java program to read the data 

## Installation

### Java

Simply download 'Elijah.jar' from the latest release and run with Java 8 or above

### Arduino

#### Provided Same components are used as described here:

1. Wire the arduino up as descirbed in the schematic
2. Load 'tracker.ino' onto the board
- It is recommended to run it at least once in debug mode (See below)
3. Power Arduino

The schematic can be opened using Fritzing

Often it can seem like the system is not working if only plugged in for a few moments, however it may just be taking its time getting GPS Lock

#### If other components or boards are used:
1. Wire SD module to SPI pins
2. Wire GPS to UART pins
3. Edit pins in code to the values of the new pins
4. Load editted 'tracker.ino'
- It is definitely recommended to run it in debug mode at least once here
5. Power Arduino

## Usage

The tracking program should start on it's own, there is an optional status LED, this will light up if there is an issue in setup (normally an incorrectly wired component or the SD card is missing), this will then start blinking when the tracker starts getting GPS locks

To view the laps afterwards, simply copy the session files off the SD card then run client.jar, and open the output files

There is an open option actually in the client or the filepath can be passed as a command line argument in the format:
```/path/to/client.jar <path of the session>```

## Debug Mode

Enabled by changing the following line found at the top of tracker.ino, the editted program then has to be loaded onto the Arduino
```#define DEBUG <true/false>```

Debug mode is designed to be used when the Arduino is connected to a computer, it prints out a large amount of useful info to the Serial Monitor

Once functioning correctly it will print out all the current position data that it is writing to the SD card

Debug Mode comes at the cost of performance, size and power as extra code has to be added including addition serial writes to the Monitor, thus it is strongly recommended that it is turned off before deployment, on some high speed GPSes it can even cause the board to miss GPS updates

## Requirements

- Java (JRE 6+ Recommended)
- Arduino:
  - SDFat
  - TinyGPS++
  - Board
    - Tested on Uno and Nano, however should work with any ARM board
    - GPS Module 
        - UART (SPI pins are normally used by SD modules, although you could get away with it on larger boarder with multiple SPI outs)
        - Recommended >= 5 Hz and reasonably accurate, although this will depend on usage
    - SD Module
        - SPI most likely

Arduino libs can be found from the Arduino Library Manager

##License
Licensed under GNU GPLv3, see LICENSE for details
