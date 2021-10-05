#include <SoftwareSerial.h>   // Incluimos la librería  SoftwareSerial  
SoftwareSerial miBT(10,11);    // Definimos los pines RX y TX del Arduino conectados al Bluetooth
 
void setup()
{
  Serial.begin(9600);   // Inicializamos  el puerto serie  
  Serial.println("Listo");
  miBT.begin(38400);       // Inicializamos el puerto serie BT (Para Modo AT 2)velocidad= 38400
}
 
void loop()
{
  if(miBT.available())    // Si llega un dato por el puerto BT se envía al monitor serial
  {
    Serial.write(miBT.read());
  }
 
  if(Serial.available())  // Si llega un dato por el monitor serial se envía al puerto BT
  {
     miBT.write(Serial.read());
  }

  // CUANDO NOS REFERIMOS A SERIAL ES LA COMUNICACION ENTRE ARDUINO Y MI COMPUTADORA
  // CUANDO NOS REFERIMOS A BT ES LA COMUNICACION ENTRE ARDUINO Y MI MODULO HC05
}

// PARA ACCEDER A MODO CONFIGURACIÓN DEL MODULO, LO DESCONECTAS DE VCC
// PRESIONAS EL BOTON 
// MIENTRAS LO TIENES PRESIONADO, CONECTAS VCC Y YA
