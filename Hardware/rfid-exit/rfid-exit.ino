#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include <SPI.h>
#include "MFRC522.h"
#include <RBD_Timer.h>
#include <NTPtimeESP.h>

#define RST_PIN  5  // RST-PIN for RC522 - RFID - SPI - Module GPIO5 
#define SS_PIN  4  // SDA-PIN for RC522 - RFID - SPI - Module GPIO4 

#define FIREBASE_HOST "bus-loading-system.firebaseio.com"
#define FIREBASE_AUTH "zdirbaAI1lf6WiDNKGQHgwWx7dAMl3ZDdIXsx53G"

#define WIFI_SSID "lenovo"     // change according to your Network - cannot be longer than 32 characters!
#define WIFI_PASS "password123" // change according to your Network

NTPtime NTPph("ph.pool.ntp.org");

RBD::Timer nocarddetection_timer(5000); //if no bus is read again for 5 seconds, it means bus has left

MFRC522 mfrc522(SS_PIN, RST_PIN); 
bool wasacardpresent = false;
String previousUID = "";
String key = "";
String loading_bay = "E1";
String exit_status[] = {"loaded", "didnt' load"};

void setup() {
  Serial.begin(115200);   
  SPI.begin();          
  mfrc522.PCD_Init();
  
  delay(250);
  init_wifi();

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
}

void init_wifi() {
  Serial.println("Booting....");
  
  WiFi.begin(WIFI_SSID, WIFI_PASS);
  while ((WiFi.status() != WL_CONNECTED)) {
    delay(500);
    Serial.print(".");
  }
  Serial.println();
  
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("WiFi connected");
    
    Serial.println("Ready!");
    Serial.println("======================================================"); 
    Serial.println("Scan for Card and print UID:");
  }else{
    Serial.println("WiFi not connected");
  }
}

void loop() {
  if (mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial()) {
    nocarddetection_timer.restart();

    String currUID = rfidDetails(mfrc522.uid.uidByte, mfrc522.uid.size);
    if(!previousUID.equals(currUID)) {
      
      if(wasacardpresent) {
        send_timestamp();
        nocarddetection_timer.restart();
      }
      
      previousUID = currUID;
      send_data(currUID);
      nocarddetection_timer.restart();
      wasacardpresent = true;
    }
  } else {
    if(wasacardpresent) {
      if(nocarddetection_timer.onExpired()) {
        send_timestamp();
        wasacardpresent = false;
        previousUID = "";  
      }
    }
  }
}

// Helper routine to dump the UID as hex values to Serial
String rfidDetails(byte *buffer, byte bufferSize) {
  String strID = "";
  for (byte i = 0; i < bufferSize; i++) {
    strID +=
    (buffer[i] < 0x10 ? "0" : "") +
    String(buffer[i], HEX) +
    (i!=3 ? ":" : "");
  }
  strID.toUpperCase();
  return strID;
}

void send_timestamp() {
    String path = "Exit/" + key + "/timestamp";
    String timestamp = get_time_stamp();
    
    Firebase.setString(path, timestamp);
    delay(3000);    
}

String get_time_stamp() {
  strDateTime dateTime = NTPph.getNTPtime(8, 1);

  while(!dateTime.valid) {
    dateTime = NTPph.getNTPtime(8, 1);    
  }  
  
  String year = String(dateTime.year);
  String month = String(dateTime.month);
  String day = String(dateTime.day);
  String hour = String(dateTime.hour);
  String minute = String(dateTime.minute);
  String second = String(dateTime.second);
  return year + "-" + month + "-" + day + " " + hour + ":" + 
         minute + ":" + second;
}

void send_data(String rfid) {
    StaticJsonBuffer<200> jsonBuffer;
    String data_json = "{\"rfid\":\"" + rfid + "\",\"" + 
                       "status\":\"" + exit_status[random(0, 2)] + "\"}";


    JsonObject& data_object = jsonBuffer.parseObject(data_json);
    if(!data_object.success()) {
      Serial.println("parseObject() failed");
      return;
    }
  
    JsonVariant data_variant = data_object; 
    key = Firebase.push("Exit", data_variant);
    delay(3000);  
}
