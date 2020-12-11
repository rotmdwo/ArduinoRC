
 
#include <SoftwareSerial.h>   //가상 시리얼 통신을 위한 라이브러리 선언

#define BT_RXD 3  // 아두이노의 4번핀을 RX(받는 핀)로 설정
#define BT_TXD 4 // 아두이노 3번핀을 TX(보내는 핀)로 설정

SoftwareSerial bluetooth(BT_RXD,BT_TXD); //블루투스 통신을 위한 설정 

int RightMotor_E_pin = 5;       // 오른쪽 모터의 Enable & PWM
int LeftMotor_E_pin = 6;        // 왼쪽 모터의 Enable & PWM
int RightMotor_1_pin = 8;       // 오른쪽 모터 제어선 IN1
int RightMotor_2_pin = 9;       // 오른쪽 모터 제어선 IN2
int LeftMotor_3_pin = 10;       // 왼쪽 모터 제어선 IN3
int LeftMotor_4_pin = 11;       // 왼쪽 모터 제어선 IN4

int motor_s = 153;              // 최대 속도(0~255)의 60% 

int R_Motor = 0;
int L_Motor = 0;
int mode = 0;

void setup() {
  pinMode(RightMotor_E_pin, OUTPUT);        // 출력모드로 설정
  pinMode(RightMotor_1_pin, OUTPUT);
  pinMode(RightMotor_2_pin, OUTPUT);
  pinMode(LeftMotor_3_pin, OUTPUT);
  pinMode(LeftMotor_4_pin, OUTPUT);
  pinMode(LeftMotor_E_pin, OUTPUT);

  Serial.begin(9600);    //PC와 아두이노간 시리얼 통신 속도를 9600bps로 설정 
  bluetooth.begin(9600); //블루투스와 아두이노간 시리얼 통신 속도를 9600bps로 설정
  
  Serial.println("Welcome Eduino!");
  
}

void loop() { 
  if(bluetooth.available()){      
    char Blue_Val = bluetooth.read();
       
    Serial.print("Input Signal : ");
    Serial.print("Bluetooth[ "); Serial.print(Blue_Val); Serial.print(" ], ");
    
    control_SmartCar(Blue_Val);

    if(mode == 0){
      motor_role(R_Motor, L_Motor, motor_s);
    }
    else if(mode == 1){
      Right_role(R_Motor, L_Motor, motor_s);
    }
    else if(mode == 2){
      Left_role(R_Motor, L_Motor, motor_s);
    }
    else{
      motor_role(R_Motor, L_Motor, 0);
    } 
  } 
}

void control_SmartCar(char Blue_val){ 
  if( Blue_val == '+' ){      // "+" 버튼, 명령 : 속도 증가
    motor_s = motor_s + 20;
    motor_s = min(motor_s, 255);
    Serial.print("Speed Up : "); 
  }
  
  else if( Blue_val == '-' ){ // "-" 버튼, 명령 : 속도 감소
    motor_s = motor_s - 20;
    motor_s = max(motor_s, 50);
    Serial.print("Speed Down : ");
  }
  
  else if( Blue_val == 'g' ){  // "g" 버튼, 명령 : 전진
    R_Motor = HIGH; L_Motor = HIGH; mode = 0;  
    Serial.print("Forward : ");
  }  
  
  else if( Blue_val == 'l' ){ // "l" 버튼, 명령 : 좌회전
    mode = 2;
    Serial.print("Turn Left : "); 
  }
  
  else if( Blue_val == 'r' ){ // "r" 버튼, 명령 : 우회전
    mode = 1;
    Serial.print("Turn Right : "); 
  }
  
  else if( Blue_val == 'b' ){ // "b" 버튼, 명령 : 후진
    R_Motor = LOW; L_Motor = LOW; mode = 0;
    Serial.print("Backward : "); 
  }

  else if( Blue_val == 's' ){ // "s" 버튼, 명령 : 정지
    mode = 3;
    Serial.print("Stop : ");  
  }
  
  else{
    Serial.print("Not Defined : ");  // 지정하지 않은 주소입력.
  }
  
  Serial.print("R_Motor[ ");Serial.print(R_Motor);Serial.print(" ], ");
  Serial.print("L_Motor[ ");Serial.print(L_Motor);Serial.print(" ], ");
  Serial.print("motor_s[ ");Serial.print(motor_s);Serial.print(" ], ");
  Serial.print("Mode[ ");Serial.print(mode);Serial.println(" ]");
}

void motor_role(int R_motor, int L_motor, int Speed){
   digitalWrite(RightMotor_1_pin, R_motor);
   digitalWrite(RightMotor_2_pin, !R_motor);
   digitalWrite(LeftMotor_3_pin, L_motor);
   digitalWrite(LeftMotor_4_pin, !L_motor);
   
   analogWrite(RightMotor_E_pin, Speed);  // 우측 모터 속도값
   analogWrite(LeftMotor_E_pin, Speed);   // 좌측 모터 속도값  
}

void Right_role(int R_motor, int L_motor, int Speed){
   digitalWrite(RightMotor_1_pin, R_motor);
   digitalWrite(RightMotor_2_pin, !R_motor);
   digitalWrite(LeftMotor_3_pin, L_motor);
   digitalWrite(LeftMotor_4_pin, !L_motor);
   
   analogWrite(RightMotor_E_pin, max(Speed*0.4,50));  // 우측 모터 속도값
   analogWrite(LeftMotor_E_pin, min(Speed*1.4,255));   // 좌측 모터 속도값
}

void Left_role(int R_motor, int L_motor, int Speed){
   digitalWrite(RightMotor_1_pin, R_motor);
   digitalWrite(RightMotor_2_pin, !R_motor);
   digitalWrite(LeftMotor_3_pin, L_motor);
   digitalWrite(LeftMotor_4_pin, !L_motor);
   
   analogWrite(RightMotor_E_pin, min(Speed*1.4,255));  // 우측 모터 속도값
   analogWrite(LeftMotor_E_pin, max(Speed*0.2,50));   // 좌측 모터 속도값   
}
