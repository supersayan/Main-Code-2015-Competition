package org.usfirst.frc.team2642.robot;

import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends IterativeRobot {
	
	Joystick driveStick; Joystick auxStick; Joystick auxCard; 
	RobotDrive drive;
	Gyro gyro;
	DigitalInput liftUpperLimit; DigitalInput liftLowerLimit;
	Talon rightPicker; Talon leftPicker; Talon lift;
	Encoder liftEncoder;
	DigitalInput toteInRobot;
	Encoder backRightEncoder; Encoder backLeftEncoder;
	final int lowerSetPoint = 80;
	final int upperSetPoint = 1250;
	final int autoTimeout = 1900;
	
	Compressor compressor;
	Solenoid dogs;
	Solenoid pusher;
	//Solenoid flipper;
	Solenoid arm; 
	
	boolean liftUp;
	boolean liftDown;
	boolean liftWait;
	int autoLoopCounter;
	int autoLiftCounter;
	final double Kp = 0.04;
	double angle;
    int unloadCounter;
    boolean flipperToggle;
	
    int autoMode;
    int autoPhase;
    boolean autoIsOn;
    
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	driveStick = new Joystick(0);
    	auxStick = new Joystick(1);
    	auxCard = new Joystick(2);
    	gyro = new Gyro(0);
    	drive = new RobotDrive(2,0,1,3); 
    	drive.setInvertedMotor(MotorType.kFrontLeft, true);
    	drive.setInvertedMotor(MotorType.kRearRight, true);
    	
    	rightPicker = new Talon(6);
    	leftPicker = new Talon(5);
    	lift = new Talon(4);
    	liftEncoder = new Encoder(6,7);
    	liftUpperLimit = new DigitalInput(8);
    	liftLowerLimit = new DigitalInput(9);
    	toteInRobot = new DigitalInput(4);
    	backLeftEncoder = new Encoder(2, 3);
    	
    	compressor = new Compressor(0);
    	dogs = new Solenoid(0);
    	pusher = new Solenoid(1);
    	//flipper = new Solenoid(2);
    	arm = new Solenoid(2);
    }
    
    /**
     * This function is run once each time the robot enters autonomous mode
     */
    public void autonomousInit() {
    	
    	autoLoopCounter = 0;
    	gyro.reset();
        //backRightEncoder.reset();
    	backLeftEncoder.reset();
    	liftEncoder.reset();
    	compressor.start();
    	
    	//if (button)
    	//	3 tote auto
    	if(auxCard.getRawButton(8))
    		autoMode = 2; //3 tote stack
    	else if(auxCard.getRawButton(10))
    		autoMode = 1; //drive forward
    	else
    		autoMode = 0; //do nothing
    	
    }
    
    public void autonomousPeriodic() {
    	/*drive.arcadeDrive(0, 0);
    	rightPicker.set(0);
    	leftPicker.set(0);
    	lift.set(0);
    	dogs.set(false);
    	pusher.set(false);
    	System.out.println("stop");
    	Timer.delay(0.05);*/
    	
    	if (autoLoopCounter > autoTimeout)
    		autoMode = 0;
    	
    	switch (autoMode) {
    		case 0: //do nothing
    			drive.arcadeDrive(0, 0);
    	    	rightPicker.set(0);
    	    	leftPicker.set(0);
    	    	lift.set(0);
    	    	dogs.set(false);
    	    	pusher.set(false);
    	    	Timer.delay(0.05);
    		case 1: //drive forward
    			drive.arcadeDrive(-0.7, 0);
        		rightPicker.set(0);
        		leftPicker.set(0);
        		lift.set(0);
        		dogs.set(false);
        		pusher.set(false);
        		autoLoopCounter++;
        		Timer.delay(0.005);
    			if (backLeftEncoder.getDistance() < 2000 && autoLoopCounter < autoTimeout)
    				autoMode = 0;
    		case 2: //1 tote
    			switch (autoPhase) {
    				case 0:
    					backLeftEncoder.reset();
    					gyro.reset();
    					autoPhase++;
    				case 1: //collect tote
    					drive.arcadeDrive(-0.4, 0);
    					rightPicker.set(0.6);
    					leftPicker.set(-0.6);
    					lift.set(0);
    					dogs.set(false);
    					pusher.set(false);
    					autoLoopCounter++;
    					Timer.delay(0.005);
    					if (toteInRobot.get())
    						autoPhase++;
    				case 2: //lift up
    					drive.arcadeDrive(0, 0);
						rightPicker.set(0);
						leftPicker.set(0);
						lift.set(0.8);
						dogs.set(false);
						pusher.set(false);
						autoLoopCounter++;
						Timer.delay(0.005);
    					if (liftEncoder.getDistance() > 400)
    						autoPhase++;
    				case 3: //turn right 90 degrees
    					drive.arcadeDrive(0, 0.7);
						rightPicker.set(0);
						leftPicker.set(0);
						lift.set(0);
						dogs.set(false);
						pusher.set(false);
						autoLoopCounter++;
						Timer.delay(0.005);
    					if (gyro.getAngle() > 85) {
    						autoPhase++;
    						backLeftEncoder.reset();
    					}
    				case 4: //drive 4ward
    					drive.arcadeDrive(-0.7, 0);
						rightPicker.set(0);
						leftPicker.set(0);
						lift.set(0);
						dogs.set(false);
						pusher.set(false);
						autoLoopCounter++;
						Timer.delay(0.005);
    					if (backLeftEncoder.getDistance() < 1450 && autoLoopCounter < autoTimeout) {
    						autoPhase++;
    						backLeftEncoder.reset();
    					}
    				case 5: //turn right 90
						drive.arcadeDrive(0, 0.7);
						rightPicker.set(0);
						leftPicker.set(0);
						lift.set(0);
						dogs.set(false);
						pusher.set(false);
						autoLoopCounter++;
						Timer.delay(0.005);
    					if (gyro.getAngle() < 175 && autoLoopCounter < autoTimeout) {
    						autoPhase++;
    						backLeftEncoder.reset();
    					}
    				case 6: //lift down
						drive.arcadeDrive(0, 0);
						rightPicker.set(0);
						leftPicker.set(0);
						lift.set(-0.8);
						dogs.set(false);
						pusher.set(false);
						autoLoopCounter++;
						Timer.delay(0.005);
    					if (!liftLowerLimit.get() && autoLoopCounter < autoTimeout)
    						autoPhase++;
    				case 7: //unload
						drive.arcadeDrive(0.5, 0);
						rightPicker.set(-0.5);//reverse rollers
						leftPicker.set(0.5);
						lift.set(0);
						dogs.set(true);
						pusher.set(true);
						autoLoopCounter++;
						Timer.delay(0.005);
    					if (backLeftEncoder.getDistance() > -400 && autoLoopCounter < autoTimeout)
    						autoPhase++;
    				case 8:
    					autoMode = 0;
    			} //end autoMode 2
    		case 3: //3 tote stack
    			switch (autoPhase) {
    				case 0:
    					backLeftEncoder.reset();
    					autoPhase++;
    				case 1: //drive forward to tote					1ST TOTE
    					drive.arcadeDrive(-0.4, 0);
    					rightPicker.set(0.6);
    					leftPicker.set(-0.6);
    					lift.set(0);
    					dogs.set(false);
    					pusher.set(false);
    					autoLoopCounter++;
    					if (toteInRobot.get()) {
    						autoPhase++;
    						liftUp = true;
    					}
    				case 2: //auto set tote
    					if (liftEncoder.getDistance() > upperSetPoint && liftUp == true)
    			    		liftUp = false;
    			    	if (liftUp)
    			        	lift.set(0.8);
    			    	else
    			    		lift.set(-0.8);
    					if (!liftUp && (liftLowerLimit.get() || (liftEncoder.getDistance() < lowerSetPoint + 10))) {
    						gyro.reset();
    						autoPhase++;
    					}
    				case 3: // turn to 25 degrees
    					drive.arcadeDrive(0, 0.7);
						rightPicker.set(0);
						leftPicker.set(0);
						lift.set(0);
						dogs.set(false);
						pusher.set(false);
						autoLoopCounter++;
    					if (gyro.getAngle() > 20) {
    						autoPhase++;
    						backLeftEncoder.reset();
    					}
    				case 4: //drive 4ward
    					drive.arcadeDrive(-0.7, 0);
						rightPicker.set(-0.6);
						leftPicker.set(0.6);
						lift.set(0);
						dogs.set(false);
						pusher.set(false);
						autoLoopCounter++;
						Timer.delay(0.005);
    					if (backLeftEncoder.getDistance() > 1000) {
    						autoPhase++;
    						backLeftEncoder.reset();
    					}
    				case 5: //turn to -25 degrees
    					drive.arcadeDrive(0, 0.7);
						rightPicker.set(0);
						leftPicker.set(0);
						lift.set(0);
						dogs.set(false);
						pusher.set(false);
						autoLoopCounter++;
    					if (gyro.getAngle() < -20) {
    						autoPhase++;
    						backLeftEncoder.reset();
    					}
    				case 6: //drive 4ward
    					drive.arcadeDrive(-0.7, 0);
						rightPicker.set(-0.6);
						leftPicker.set(0.6);
						lift.set(0);
						dogs.set(false);
						pusher.set(false);
						autoLoopCounter++;
						Timer.delay(0.005);
    					if (backLeftEncoder.getDistance() > 1000) {
    						autoPhase++;
    						backLeftEncoder.reset();
    					}
    				case 7: //turn to 0 degrees
    					drive.arcadeDrive(0, 0.7);
						rightPicker.set(0);
						leftPicker.set(0);
						lift.set(0);
						dogs.set(false);
						pusher.set(false);
						autoLoopCounter++;
    					if (gyro.getAngle() > -5) {
    						autoPhase++;
    						backLeftEncoder.reset();
    					}
    				case 8: //drive 4ward to tote					2ND TOTE
    					drive.arcadeDrive(-0.4, 0);
    					rightPicker.set(0.6);
    					leftPicker.set(-0.6);
    					lift.set(0);
    					dogs.set(false);
    					pusher.set(false);
    					autoLoopCounter++;
    					if (toteInRobot.get()) {
    						autoPhase++;
    						liftUp = true;
    					}
    				case 9: //auto set tote
    					if (liftEncoder.getDistance() > upperSetPoint && liftUp == true)
    			    		liftUp = false;
    			    	if (liftUp)
    			        	lift.set(0.8);
    			    	else
    			    		lift.set(-0.8);
    					if (!liftUp && (liftLowerLimit.get() || (liftEncoder.getDistance() < lowerSetPoint + 10))) {
    						gyro.reset();
    						autoPhase++;
    					}
    				case 10: // turn to 25 degrees
    					drive.arcadeDrive(0, 0.7);
						rightPicker.set(0);
						leftPicker.set(0);
						lift.set(0);
						dogs.set(false);
						pusher.set(false);
						autoLoopCounter++;
    					if (gyro.getAngle() > 20) {
    						autoPhase++;
    						backLeftEncoder.reset();
    					}
    				case 11: //drive 4ward
    					drive.arcadeDrive(-0.7, 0);
						rightPicker.set(-0.6);
						leftPicker.set(0.6);
						lift.set(0);
						dogs.set(false);
						pusher.set(false);
						autoLoopCounter++;
						Timer.delay(0.005);
    					if (backLeftEncoder.getDistance() > 1000) {
    						autoPhase++;
    						backLeftEncoder.reset();
    					}
    				case 12: //turn to -25 degrees
    					drive.arcadeDrive(0, 0.7);
						rightPicker.set(0);
						leftPicker.set(0);
						lift.set(0);
						dogs.set(false);
						pusher.set(false);
						autoLoopCounter++;
    					if (gyro.getAngle() < -20) {
    						autoPhase++;
    						backLeftEncoder.reset();
    					}
    				case 13: //drive 4ward
    					drive.arcadeDrive(-0.7, 0);
						rightPicker.set(-0.6);
						leftPicker.set(0.6);
						lift.set(0);
						dogs.set(false);
						pusher.set(false);
						autoLoopCounter++;
						Timer.delay(0.005);
    					if (backLeftEncoder.getDistance() > 1000) {
    						autoPhase++;
    						backLeftEncoder.reset();
    					}
    				case 14: //turn to 0 degrees
    					drive.arcadeDrive(0, 0.7);
						rightPicker.set(0);
						leftPicker.set(0);
						lift.set(0);
						dogs.set(false);
						pusher.set(false);
						autoLoopCounter++;
    					if (gyro.getAngle() > -5) {
    						autoPhase++;
    						backLeftEncoder.reset();
    					}
    				case 15: //drive 4ward to tote					3RD TOTE
    					drive.arcadeDrive(-0.4, 0);
    					rightPicker.set(0.6);
    					leftPicker.set(-0.6);
    					lift.set(0);
    					dogs.set(false);
    					pusher.set(false);
    					autoLoopCounter++;
    					if (toteInRobot.get()) {
    						autoPhase++;
    						liftUp = true;
    					}
    				case 16: //turn to 90 degrees
    					drive.arcadeDrive(0, 0.7);
						rightPicker.set(0);
						leftPicker.set(0);
						lift.set(0);
						dogs.set(false);
						pusher.set(false);
						autoLoopCounter++;
    					if (gyro.getAngle() > 85) {
    						autoPhase++;
    						backLeftEncoder.reset();
    					}
    				case 17: //drive to autozone
    					drive.arcadeDrive(-0.7, 0);
						rightPicker.set(-0.6);
						leftPicker.set(0.6);
						lift.set(0);
						dogs.set(false);
						pusher.set(false);
						autoLoopCounter++;
    					if (backLeftEncoder.getDistance() > 1000) {
    						autoPhase++;
    						backLeftEncoder.reset();
    						unloadCounter = 0;
    					}
    				case 18:
    					if(auxStick.getRawButton(1) && unloadCounter <= 25){ //open dogs
    		        		dogs.set(true);
    		        		pusher.set(false);
    		        		arm.set(false);
    		        		rightPicker.set(0);
    		        		leftPicker.set(0);
    		        		unloadCounter++;
    		        	}else if(auxStick.getRawButton(1) && unloadCounter >= 25){ //open dogs push and reverses pickers
    		        		dogs.set(true);
    		        		pusher.set(true);
    		        		arm.set(true);;
    		        		rightPicker.set(-0.4);
    		        		leftPicker.set(0.4);
    		        		unloadCounter++;
    		        	}
    					if (unloadCounter > 100)
    						autoPhase++;
    				case 19:
    					autoMode = 0;
    			} //end autoMode 3
    	} //end autoMode switch
    	
    }
    
    public void teleopInit(){
    	gyro.reset();
    	backLeftEncoder.reset();
    	//compressor.start();
    	liftEncoder.reset();
    	liftUp = false;
    	liftWait = false;
    	liftDown = false;
    	unloadCounter = 0;
    	autoLiftCounter = 0;
    	flipperToggle = true;
    	
    }

    public void teleopPeriodic() {
	
        //arcade drive
        if(driveStick.getRawButton(1)){
        	drive.arcadeDrive(driveStick.getY(), driveStick.getX());
        	
        }else if(driveStick.getRawButton(2)){
        	drive.arcadeDrive(0, 0);
        }else{
        	drive.arcadeDrive(driveStick.getY()*0.7, driveStick.getX()*0.7);
        }
       
        /*
        if(driveStick.getRawButton(3)){ //flipper 
        	flipper.set(true);
        }else{
        	flipper.set(false);
        }
        */
 //==============================================================================================
        //lift
        /*
        //testy test mode
        if(auxStick.getRawButton(3) && !liftUpperLimit.get()){  //up
        	lift.set(1);
        }else if(auxStick.getRawButton(2) && !liftLowerLimit.get()){  //down
        	lift.set(-0.9);
        }else{
        	lift.set(0);                            
        }
        */
        
        //human overide
        if(auxStick.getRawButton(3) || auxStick.getRawButton(2) || auxStick.getRawButton(10) || auxCard.getRawButton(12)) {
        	autoLiftCounter = 0;
        	liftUp = false; //stop auto lift
        	liftDown = false;
        	liftWait = false;
        	
        	if(auxStick.getRawButton(3) && !liftUpperLimit.get() && liftEncoder.getDistance() < upperSetPoint){  //up
            	lift.set(1);
            	System.out.println("up");
            }else if(auxStick.getRawButton(2) && !liftLowerLimit.get() && liftEncoder.getDistance() > lowerSetPoint){  //down
            	lift.set(-0.9);
            System.out.println("down");
            }else if(auxStick.getRawButton(10) && !liftLowerLimit.get()){ //zero lift encoder
            	lift.set(-0.5); //see if statement below for encoder reset
            }else{
            	lift.set(0);                            
            }
        }else{ //auto lift
        	
        	if(liftLowerLimit.get() && !toteInRobot.get()){//stop at upper limits
        		liftUp = false;
        		liftWait = false;
        		liftDown = false;
        		
        	}else{
        		if(toteInRobot.get() && !liftUp && !liftDown && !liftWait) {//auto load up to dogs
        			liftUp = true;
            		liftWait = false;
        			liftDown = false;
				
        		}else if(liftUp){ //go up to dogs
        			if(liftEncoder.getDistance() > (upperSetPoint + 20) || liftUpperLimit.get()){
        				liftUp = false;
        				liftWait = true;
        				liftDown = false;
        				
        			}
        			
        			autoLiftCounter = 0;
        			lift.set(0.75);
        			
        		}else if(liftWait){
        			autoLiftCounter++;
        			if(autoLiftCounter > 50){
        				liftUp = false;
        				liftWait = false;
        				liftDown = true;
        			}
        			
        			lift.set(0.0);
        			
        		}else if(liftDown){//go down to bottom
        			if(liftEncoder.getDistance() < (lowerSetPoint) ){
        				liftUp = false;
                		liftWait = false;
        				liftDown = false;
        			}
        			
            		lift.set(-0.75);
        			
        		}else{
        			lift.set(0);
        			autoLiftCounter = 0;
        		}	
        	}
        }
        
        if(auxStick.getRawButton(10) && liftLowerLimit.get()){ //reset lift encoder
        	liftEncoder.reset();
        	//System.out.println("encoder reset");
        }
//===================================================================================================
        //picker //unloading
        if(auxStick.getRawButton(1)){ //auto unload
        	if(auxStick.getRawButton(1) && unloadCounter <= 25){ //open dogs
        		dogs.set(true);
        		pusher.set(false);
        		arm.set(false);
        		rightPicker.set(0);
        		leftPicker.set(0);
        		unloadCounter++;
        	}else if(auxStick.getRawButton(1) && unloadCounter >= 25){ //open dogs push and reverses pickers
        		dogs.set(true);
        		pusher.set(true);
        		arm.set(true);;
        		rightPicker.set(-0.4);
        		leftPicker.set(0.4);
        		unloadCounter++;
        	}else{ //do nothing
        		dogs.set(false);
        		arm.set(false);
        		pusher.set(false);
        		rightPicker.set(0);
        		leftPicker.set(0);
        	}
        }else if(!auxStick.getRawButton(1) && unloadCounter > 0){ //reset counter
    		unloadCounter = 0;
    		dogs.set(false);
    		arm.set(false);
    		pusher.set(false);
    		rightPicker.set(0);
    		leftPicker.set(0);
        	
        }else{
        
        	if(auxCard.getRawButton(9)){ //in
        		rightPicker.set(0.7);
        		leftPicker.set(-0.7); //left is opposite
        	}else if(auxCard.getRawButton(7)){ //out
        		rightPicker.set(-0.7);
        		leftPicker.set(0.7);
        	}else{
        		leftPicker.set(0);
        		rightPicker.set(0);
        	}
        	
        	if(liftEncoder.getDistance() > (upperSetPoint + 90)){
        		dogs.set(true);
        		System.out.println("open da dogs");
        	}else if(auxCard.getRawButton(11)){//these are wired backwards
        		dogs.set(false);
        	}else{
        		dogs.set(true);
        	}
        
        	if(auxCard.getRawButton(4)){
        		pusher.set(false);
        	}else{
        		pusher.set(true);
        	}
        	if(auxStick.getRawButton(7)){
        		arm.set(true);
        	}else{
        		arm.set(false);
        	}
        }
        /*
        if (auxStick.getRawButton(6)) {
            if (flipperToggle) {
            	// toggle arm
            	flipperToggle = false;
            }
        } else {
        	flipperToggle = true;
        }
        System.out.println(flipperToggle);
          */      
        /*if(auxStick.getRawButton(7) && !flipperToggle){
        	flipperToggle = true;
        	System.out.println("on");
        }else if(auxStick.getRawButton(7) && flipperToggle){
        	flipperToggle = false;
        	System.out.println("off");

        }else{
        	System.out.println("else");
        }*/
        
        
        
//============================================================================================ 
        //drivers station
        
        
        
        
        
//============================================================================================
        Timer.delay(0.005);
        //System.out.println(autoLiftCounter);
		//System.out.println(toteInRobot.get());
        //System.out.println(liftUpperLimit.get());
        //System.out.println(liftEncoder.getDistance());
        //System.out.println(liftUpperLimit.get() + " upper");
        //System.out.println(liftLowerLimit.get() + " lower");
        //System.out.println(liftEncoder.getDistance());
        //System.out.println(backLeftEncoder.getDistance());
        System.out.println(gyro.getAngle());

    }

    public void testPeriodic() {
    	LiveWindow.run();
    }
    
}
