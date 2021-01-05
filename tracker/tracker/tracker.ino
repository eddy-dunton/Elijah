//tells the compiler whether attempt to connect to a computer 
//to display debug info if debug is false all debug related 
//items are not compliled, Increasing speed and reducing size
#define DEBUG false

//Required for GPS
#include <TinyGPS++.h>
#include <SoftwareSerial.h>

//Required for SD
#include <SPI.h>
#include <SdFat.h>

//Constants
const char WRITES_PER_FLUSH = 16;
const int DEBUG_BAUD = 115200;
const int GPS_BAUD = 19200;

//PINS
const int LED_PIN = 2;
const int PIN_RX = 3, PIN_TX = 4;
const int PIN_CS = 8;

//SD card handler
SdFat sd;
//Session file
SdFile file;
//GPS handler object
TinyGPSPlus gps;
//Software serial used to communicate with GPS
//TODO: Move the GPS to regular serial for final version
SoftwareSerial ss(PIN_RX, PIN_TX);
//Name of the session file
//const char stuff seems odd
//But is necessary for sdfat to not throw a fit
char* fileName;

//Writes since last flush
byte writes = 0;

//Used for tracking LED
bool led = false;

void setup() {
  //LED
  pinMode(LED_PIN, OUTPUT);
  digitalWrite(LED_PIN, HIGH);
  
  //Starts communication with computer
  #if DEBUG
    Serial.begin(DEBUG_BAUD);
    Serial.println("Lap Tracker by Eddy Dunton");
  #endif
  //Starts communication with GPS 
  ss.begin(GPS_BAUD);

  //Checks that valid SD is found
  if (!sd.begin(PIN_CS)) {
    //If not: complains and stops
    #if DEBUG
      Serial.println("SD Failed");
    #endif
    sd.initErrorHalt();
    while (1);
  }

  #if DEBUG
    Serial.println("SD Passed");
  #endif

  getFileName();

  //Opens file
  if (! file.open(fileName, O_WRITE | O_CREAT)) {
    #if DEBUG
      Serial.println("File Failed");
    #endif
    while (1);
  }

  #if DEBUG
    Serial.println("File Passed");
  #endif

  digitalWrite(LED_PIN, LOW);
}

void loop() {
  #if DEBUG
    //Restarts if serial in is provided
    if (Serial.available() != 0) restart();
    
    unsigned long tStart = millis();
    unsigned long tFeed, tEnd;
  #endif

  //Feeds GPS handler
  feed();

  #if DEBUG
    tFeed = millis();
  #endif

  //Checks if gps has been updated
  if (gps.location.isUpdated()) {
    writePosition();

    led = !led;
    digitalWrite(LED_PIN, led);

    #if DEBUG
      tEnd = millis();
      Serial.print("DURATION:   FEED:");
      Serial.print(tFeed - tStart);
      Serial.print("ms    WRITE:");
      Serial.print(tEnd - tFeed);
      Serial.print("ms    TOTAL:");
      Serial.print(tEnd - tStart);
      Serial.print("ms\n\n");
    #endif
  }
}

/*
 * Returns file name for session
 * File will always be in the root directory
 * Starts with session_a.tel
 *  then increments the 'a' char, will probably break after about 30 increments
 *  as you'll end up back at the start with an int overflow and I can't imagine 
 *  you're allowed to whack (null) in a filename
 * Returns a char* because sdfat needs a char* for filename
 *  else it cries
 */
void getFileName() { 
  //Starting filename
  fileName = "session_a.tel";
  //Increments if taken
  while (sd.exists(fileName)) {
    fileName[8] ++;
  }
  
  #if DEBUG
    //Prints off name
    Serial.println(fileName);
  #endif
}

/*
 * Writes the current GPS to the SD card
 * In format:
 * (Little Endian)
 * 32 bit uint time
 * 32 bit float lat
 * 32 bit float long
 * 32 bit float mph
 * No delimiters are necessary
 * As every point takes up exactly 16 bytes
 * Opens and closes file in order to force the device to push the buffer
 */
void writePosition() {
  uint32_t currTime = gps.time.value();
  double latitude = gps.location.lat();
  double longitude = gps.location.lng();
  double milesperhour = gps.speed.mph();
  
  //Fills buffer
  file.write(&currTime, 4);
  //file.print(":");
  file.write(&latitude, 4);
  //file.print(":");
  file.write(&longitude, 4);
  //file.print(":");
  file.write(&milesperhour, 4);
  
  //Pushes buffer onto card
  writes ++;
  if (writes >= WRITES_PER_FLUSH) {
    file.flush();
    writes = 0;
  }

  //Prints off if in debug mode
  #if DEBUG
    Serial.print("TIME:");
    Serial.print(currTime);
    Serial.print('\t');
    
    Serial.print("LAT:");
    Serial.print(latitude);
    Serial.print('\t');
    
    Serial.print("LONG:");
    Serial.print(longitude);
    Serial.print('\t');
    
    Serial.print("MPH:");
    Serial.println(milesperhour);
  #endif
}

/**
 * Passes all available serial data to GPS Handler
 */
void feed() {
  while (ss.available()) {
    gps.encode(ss.read()); 
  }
}

#if DEBUG
  /**
   * Restarts the program
   */
  void restart() {
    Serial.end();
    file.close();
    ss.end();
    //Jumps to first line of program
    asm volatile("jmp 0;");
  }
#endif
