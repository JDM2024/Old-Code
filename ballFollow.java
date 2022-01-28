// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
//import frc.robot.commands.goHome;

public class ballFollow extends Command {

  goHome goHome = new goHome();

  Double turnPower = .3;
  Double aheadPower = .4;
  Double goPower = .4;
  Double spinPower = .2;
  Double lineUpPower = .126;
  Double lateLineUp = .127;
  Double pauseAfterBlanks = 4.0;
  Double forwardsWhenTurning = .4;
  // Double overshootCompensation = .03;
  Double sprintPower = .5;

  // Double lineUpCoefficient = lineUpPower / turnPower;
  Double ballRadius = 0.0;
  Boolean goCleared = false;
  Boolean lostBall = false;
  Double rawQuotient = 0.0;
  Double flooredQuotient = 0.0;
  Double finalLUP = 0.0;
  // Double finalTurnMult = 1.0;
  Double forwardsTurningFinal = 0.0;
  // Integer finalLineUpAttempts = 0;
  Boolean onFinalLineUp = false;
  Double turnDouble = 0.0;
  Boolean condition = false;
  // Double finalTankPower = 0.0;
  // Integer ballsGrabbed = 0;

  public ballFollow() {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
  }

  void correctedMotors(Double leftPower, Double rightPower) {
    Robot.leftTalon.set(leftPower * .92435);
    Robot.rightTalon.set(-rightPower);
    Robot.differentialDrive.feed();
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    Robot.ingest.set(1);
    Robot.differentialDrive.setSafetyEnabled(false);
    Robot.pauseLoop(.25);
    Robot.ingest.stopMotor();
    Robot.differentialDrive.feed();
    Robot.differentialDrive.feedWatchdog();
    Robot.differentialDrive.setSafetyEnabled(true);
    Robot.ballsGrabbed = 0;
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    System.out.println("Running command");
    if (Robot.ballsGrabbed > 1) {
      finalLUP = lateLineUp;
    } else {
      finalLUP = lineUpPower;
    }
    lostBall = Robot.NNlost.getBoolean(false);
    ballRadius = Robot.NNradius.getDouble(1.0);
    turnDouble = Robot.NNturn.getDouble(0.0);
    // System.out.println(Robot.NNturn.getString("turn"));
    /*
     * if (Robot.NNblanks.getDouble(0) == 10.0) {
     * Robot.differentialDrive.setSafetyEnabled(false); Robot.haltMotors();
     * Robot.pauseLoop(1); Robot.differentialDrive.feed();
     * Robot.differentialDrive.feedWatchdog();
     * Robot.differentialDrive.setSafetyEnabled(true);
     * Robot.differentialDrive.feed(); Robot.differentialDrive.feedWatchdog(); }
     */
    // Double rawBlanks = Robot.NNblanks.getDouble(0.0);

    rawQuotient = Robot.NNblanks.getDouble(0.0) / pauseAfterBlanks;
    flooredQuotient = java.lang.Math.floor(rawQuotient);

    /*
     * System.out.println("NNblanks actual: " + rawBlanks);
     * System.out.println("NNblanks over " + pauseAfterBlanks + " raw: " +
     * rawQuotient); System.out.println("NNblanks over " + pauseAfterBlanks +
     * " rounded: " + flooredQuotient);
     */

    if (ballRadius > 28) {
      onFinalLineUp = true;
    } else {
      onFinalLineUp = false;
    }
    if (ballRadius < 25) {
      forwardsTurningFinal = sprintPower;
    } else {
      forwardsTurningFinal = forwardsWhenTurning;
    }
    if ((flooredQuotient > 0.0) && !(flooredQuotient < rawQuotient)) {
      System.out.println("Pausing because multiple of 10 reached");
      // finalLineUpAttempts = finalLineUpAttempts + 1;
      Robot.differentialDrive.arcadeDrive(0.0, 0.0);
      Robot.ingest.stopMotor();
      Robot.differentialDrive.setSafetyEnabled(false);
      Robot.pauseLoop(1.0);
      Robot.differentialDrive.feed();
      Robot.differentialDrive.feedWatchdog();
      Robot.differentialDrive.setSafetyEnabled(true);
      goCleared = false;
    } else {
      /*
       * System.out.println("forwardsTurningFinal = " +
       * forwardsTurningFinal.toString()); System.out.println("turnPower = " +
       * turnPower.toString()); System.out.println("finalTurnMult = " +
       * finalTurnMult.toString()); System.out.println("finalLineUpAttempts = " +
       * finalLineUpAttempts.toString()); //System.out.println("spinPower = " +
       * spinPower.toString());
       */
      System.out
          .println("forwardsTurningFinal = " + forwardsTurningFinal.toString() + ", finalLUP = " + finalLUP.toString()
              + ", onFinalLineUp = " + onFinalLineUp.toString() + ", turnDouble = " + turnDouble.toString() + ", ballsGrabbed = " + Robot.ballsGrabbed.toString());
      if (Robot.NNturn.getDouble(0.0) == 0.0) {
        if (ballRadius > 32) {
          if (goCleared == true) {
            System.out.println("Getting the ball");
            Robot.ballsGrabbed = Robot.ballsGrabbed + 1;
            //Robot.ballGotten = true;
            Robot.ingest.set(.4);
            Robot.differentialDrive.setSafetyEnabled(false);
            correctedMotors(-goPower, -goPower);
            Robot.pauseLoop(1.0);
            Robot.haltMotors();
            Robot.pauseLoop(.5);
            Robot.ingest.stopMotor();
            /*
            Robot.pauseLoop(.4);
            Robot.ingest.set(.8);
            Robot.pauseLoop(.05);
            Robot.ingest.stopMotor();
            */
            Robot.differentialDrive.feed();
            Robot.differentialDrive.feedWatchdog();
            Robot.differentialDrive.setSafetyEnabled(true);
            Robot.differentialDrive.feed();
            Robot.differentialDrive.feedWatchdog();
          } else {
            // finalLineUpAttempts = finalLineUpAttempts + 1;
            System.out.println("Pausing for final line up calculation");
            goCleared = true;
            Robot.differentialDrive.setSafetyEnabled(false);
            Robot.haltMotors();
            Robot.pauseLoop(1.0);
            Robot.differentialDrive.feed();
            Robot.differentialDrive.feedWatchdog();
            Robot.differentialDrive.setSafetyEnabled(true);
            Robot.differentialDrive.feed();
            Robot.differentialDrive.feedWatchdog();
          }
        } else {
          System.out.println("Going forwards");
          goCleared = false;
          Robot.differentialDrive.arcadeDrive(-aheadPower, 0.0);
        }
      } else if (turnDouble == 1.0 || turnDouble == -1.0) {
        goCleared = false;
        if (onFinalLineUp == true) {
          Robot.differentialDrive.tankDrive(finalLUP * turnDouble, -finalLUP * turnDouble, false);
          // Robot.differentialDrive.arcadeDrive(-forwardsTurningFinal, finalLUP *
          // turnDouble);
          // System.out.println("Tank driving using finalLUP");
        } else if (lostBall == false) {
          // System.out.println("Arcade driving using turnPower");
          Robot.differentialDrive.arcadeDrive(-forwardsTurningFinal, turnPower * turnDouble);
        } else {
          // System.out.println("Tank driving using spinPower");
          Robot.differentialDrive.tankDrive(spinPower * turnDouble, -spinPower * turnDouble, false);
        }
      } else {
        System.out.println("Doing nothing");
        goCleared = false;
      }
    }
    /*
     * if (Robot.NNlost.getBoolean(false) == true) { turnMult = .4; }
     */
    if (Robot.ballsGrabbed == 4) {
      System.out.println("Grabbed three. ballsGrabbed = " + Robot.ballsGrabbed.toString());
      condition = true;
      goHome.start();
    }
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    //return Robot.ballGotten;
    return condition;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
