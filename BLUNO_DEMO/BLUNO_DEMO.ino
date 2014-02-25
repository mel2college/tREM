#include <Servo.h>
#include <PlainProtocol.h>

Servo mServo;
PlainProtocol comm(Serial, 115200);

void setup() {
    comm.init();
    Serial.begin(115200);               //initial the Serial
    
    mServo.attach(5);
}

void loop()
{
  if(Serial.available())
  {
    if(comm.receiveFrame()){
      if(comm.receivedCommand == "position"){
        mServo.write(comm.receivedContent[0]);
        Serial.println("<success>;");
      }
      else{
        Serial.println("command not available"); 
      }
    }
  }
}

