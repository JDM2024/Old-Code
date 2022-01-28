// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import java.lang.Math;

public class goHome extends Command {
  Boolean autonDone = false;

  public goHome() {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    Robot.ballsGrabbed = 3;
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    // Double kE = .2;

    if (Math.abs(Robot.headingError) < 3) {
      System.out.println("Straight");
      // double error = -Robot.gyro.getRate();
      // Robot.differentialDrive.tankDrive(.2 - kE * error, .2 + kE * error);
      // Robot.differentialDrive.arcadeDrive(-.4, kE * error);
      Robot.differentialDrive.arcadeDrive(-.45, -.19);
    } else {
      if (Robot.Heading > Robot.correctHeading) {
        Robot.differentialDrive.arcadeDrive(-.4, -.35);
        System.out.println("Left");
      } else {
        Robot.differentialDrive.arcadeDrive(-.4, .35);
        System.out.println("Right");
      }
    }
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return false;
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
