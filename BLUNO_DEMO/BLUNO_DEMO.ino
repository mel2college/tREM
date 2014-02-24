#include <Servo.h>

Servo mServo;

void setup() {
    Serial.begin(115200);               //initial the Serial
    
    mServo.attach(5);
}

void loop()
{
    if(Serial.available())
    {
        char command[128];  
        char buf;
        int index = 0;
        int pos = 0;
        
        while(true){
          buf = Serial.read();
          if(buf == ';')
            break;
          command[index] = buf;
          ++index;
        }
        //command[index]='\0';
        --index;
        
        for(int i = 0; i < index; ++i){
          pos += (command[index-i]-'0')*(int)pow(10,i);
        }
        
        mServo.write(pos);
        Serial.write("Servo to position ");
    }
}

