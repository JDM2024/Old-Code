// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class barrelRace extends Command {
  Boolean autonDone = false;

  public barrelRace() {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    Robot.differentialDrive.setSafetyEnabled(false);
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    switch (Robot.autonStage) {
    case 1:
      System.out.println("Forward");
      Robot.manualMotors(.405, .4);
      Robot.pauseLoop(3.38);
      Robot.autoHalt();
      break;
    case 2:
      System.out.println("Right");
      Robot.manualMotors(.34, -.34);
      Robot.pauseLoop(.635);
      Robot.autoHalt();
      break;
    case 3:
      System.out.println("Forward");
      Robot.manualMotors(.4, .4);
      Robot.pauseLoop(1.2);
      Robot.autoHalt();
      break;
    case 4:
      System.out.println("Right");
      Robot.manualMotors(.34, -.34);
      Robot.pauseLoop(.64);
      Robot.autoHalt();
      break;
    case 5:
      System.out.println("Forward");
      Robot.manualMotors(.3, .3);
      Robot.pauseLoop(2.4);
      Robot.autoHalt();
      break;
    case 6:
      System.out.println("Right");
      Robot.manualMotors(.32, -.32);
      Robot.pauseLoop(.5);
      Robot.autoHalt();
      break;
    case 7:
      System.out.println("Curve right");
      Robot.manualMotors(.37, .26);
      Robot.pauseLoop(2);
      Robot.autoHalt();
      break;
    case 8:
      System.out.println("Right");
      Robot.manualMotors(.34, -.34);
      Robot.pauseLoop(.6);
      Robot.autoHalt();
      break;
    case 9:
      System.out.println("Forward");
      Robot.manualMotors(.305, .3);
      Robot.pauseLoop(3);
      Robot.autoHalt();
      break;
    case 10:
      System.out.println("Curve left");
      Robot.manualMotors(.25, .41);
      Robot.pauseLoop(2);
      Robot.autoHalt();
      break;
    case 11:
      System.out.println("Left");
      Robot.manualMotors(.3, .3);
      Robot.pauseLoop(2.1);
      Robot.autoHalt();
      break;
    case 12:
      System.out.println("Curve left");
      Robot.manualMotors(.2, .42);
      Robot.pauseLoop(2);
      Robot.autoHalt();
      break;
    }
    Robot.autonStage = Robot.autonStage + 1;
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if (Robot.autonStage == 13) {
      autonDone = true;
      Robot.differentialDrive.setSafetyEnabled(true);
    } else {
      autonDone = false;
    }
    return autonDone;
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
