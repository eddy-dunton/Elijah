#include <SoftwareSerial.h>

const int RXPin = 3, TXPin = 4;
const int GPSBaud = 38400;

SoftwareSerial ss(RXPin, TXPin);

void setup() {
  Serial.begin(115200);
  while (! Serial)  {}

  ss.begin(GPSBaud);
}

void loop() {
  while (ss.available()) {
    Serial.write(ss.read());
  }

  while (Serial.available()) {
    ss.write(Serial.read());
  }
}
