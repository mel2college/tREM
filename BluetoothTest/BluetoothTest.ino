#include <SoftwareSerial.h>
#define RxD 0
#define TxD 1
#define EN 2
SoftwareSerial blueToothSerial(RxD, TxD);

void setup() 
{ 
    Serial.begin(9600);
    pinMode(RxD, INPUT);
    pinMode(TxD, OUTPUT);
    pinMode(EN, OUTPUT);
    setupBlueToothConnection();
} 

void setupBlueToothConnection()
{
    Serial.print("Setting up Bluetooth link");  
    blueToothSerial.begin(38400); //Set BluetoothBee BaudRate to default baud rate 38400
    delay(1000);
    sendBlueToothCommand("\r\n+STWMOD=0\r\n");
    sendBlueToothCommand("\r\n+STNA=ArduinoBluetooth\r\n");
    sendBlueToothCommand("\r\n+STAUTO=1\r\n");
    sendBlueToothCommand("\r\n+STOAUT=1\r\n");
    sendBlueToothCommand("\r\n +STPIN=0000\r\n");
    delay(2000); // This delay is required.
    sendBlueToothCommand("\r\n+INQ=1\r\n");
    delay(2000); // This delay is required.
    Serial.print("Setup complete");
}

void loop() {
  //Typical Bluetoth command - response simulation:
 
  //Type 'a' from PC Bluetooth Serial Terminal
  //See Bluetooth Bee - Wiki for instructions
  
  digitalWrite(EN, LOW);
  if(blueToothSerial.read() == 'a')
  {
    digitalWrite(EN, HIGH);
    blueToothSerial.println("You are connected");
    //You can write BT communication logic here
  }
}

//Checks if the response "OK" is received
void CheckOK()
{
  char a,b;
  digitalWrite(EN, LOW);
  while(1)
  {
    if(blueToothSerial.available())
    {
    a = blueToothSerial.read();
    Serial.print(a);
   
    if('O' == a)
    {
      // Wait for next character K. available() is required in some cases, as K is not immediately available.
      while(blueToothSerial.available()) 
      {
         b = blueToothSerial.read();
         Serial.print(b);
         break;
      }
      if('K' == b)
      {
        break;
      }
    }
   }
  }
   
  while( (a = blueToothSerial.read()) != -1)
  {
    //Wait until all other response chars are received
  }
}

void sendBlueToothCommand(char command[])
{
    digitalWrite(EN, HIGH);
    Serial.print(command);
    blueToothSerial.print(command);
    CheckOK();   
}
