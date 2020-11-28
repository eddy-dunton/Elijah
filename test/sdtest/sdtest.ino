#include <SPI.h>
#include "SdFat.h"

SdFat sd;
SdFile file;

void setup() {
  Serial.begin(9600);
  
  if (!sd.begin(8)) {
    Serial.println("SD Failed");
    sd.initErrorHalt();
    while (1);
  }

  if (!file.open("test.txt", O_WRITE | O_CREAT)) {
    Serial.println("File Failed");
    while (1);
  }

  Serial.println("Passed");   
}

void loop() {
  while (Serial.available()) {
    int rec = Serial.read();
    file.write(rec);
  }
  file.flush();
}
