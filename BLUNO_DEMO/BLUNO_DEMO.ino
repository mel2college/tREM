#include <Servo.h>
#include <PlainProtocol.h>

Servo mServo;
PlainProtocol comm(Serial, 115200);
int servo_position;

void setup() {
    comm.init();
    Serial.begin(115200);               //initial the Serial
    
    mServo.attach(5);
    mServo.write(0);
    servo_position = 0;
}

void loop()
{
  if(Serial.available())
  {
    if(comm.receiveFrame()){
      if(comm.receivedCommand == "position"){
        mServo.write(comm.receivedContent[0]);
        servo_position = comm.receivedContent[0];
        Serial.println("<success>;");
      }
      else if(comm.receivedCommand == "start"){
        switch(comm.receivedContent[0]){
         case 0: // strength soft - 10N (~101 from analog read)
         squeeze(101);
         break;
         case 1: // strength medium - 40N (~409 from analog read)
         squeeze(409);
         break;
         case 2: // strength hard - 70N (~716 from analog read)
         squeeze(716);
         break;
        }
      }
      else if(comm.receivedCommand == "stop") {
        mServo.write(0);
        servo_position = 0;
        Serial.println("<success>;");
      }
      else{
        Serial.println("command not available"); 
      }
    }
  }
}

void squeeze(int force_max){
  int force;
  while((force = analogRead(A0)) < force_max){
    Serial.println("<read_force>" + String(force) + ";");
    Serial.println("<current_position>" + String(servo_position) + ";");
    
    if((servo_position + 3) > 180){
      Serial.println("<failure>;");
      return; 
    }
    
    // increment motor position by 3 degrees until finding stopping point
    mServo.write(servo_position + 3);
    servo_position += 3;
  }
  Serial.println("<success>;");
}

