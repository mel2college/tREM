#include <PlainProtocol.h>
#include <Servo.h>

Servo mServo;
PlainProtocol comm(Serial, 115200);

void setup() {
    comm.init();
    Serial.begin(115200);
    
    mServo.attach(5);
    mServo.write(0);
}

void loop()
{
  if(Serial.available())
  {
    if(comm.receiveFrame()){
      if(comm.receivedCommand == "position"){
        mServo.write(comm.receivedContent[0]);
      }
      else if(comm.receivedCommand == "stop") {
        mServo.write(0);
      }
      else if(comm.receivedCommand == "force") {
        int force = analogRead(A0);
        Serial.println(String(force));
      }
    }
  }
}

