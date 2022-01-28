/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode.PixelFormat;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import java.lang.System;
import java.text.DecimalFormat;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.commands.ballFollow;
import frc.robot.commands.barrelRace;
import frc.robot.commands.pathTest;
import java.util.concurrent.TimeUnit;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Sendable;
import frc.robot.commands.goHome;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */

public class Robot extends TimedRobot {

  public static Integer autonStage = 0;

  public static DecimalFormat df = new DecimalFormat("##.##");
  public static DecimalFormat wn = new DecimalFormat("#");

  public static NetworkTableInstance NTinst;
  public NetworkTable NNinterface;
  public static NetworkTableEntry NNturn;
  public static NetworkTableEntry NNcertainty;
  public static NetworkTableEntry NNradius;
  public static NetworkTableEntry NNlost;
  public static NetworkTableEntry NNblanks;
  ballFollow ballFollow = new ballFollow();
  barrelRace barrelRace = new barrelRace();
  pathTest pathTest = new pathTest();


  Boolean curveDriving = false;

  public static Integer ballsGrabbed = 0;

  public static final WPI_TalonSRX frontLeft = new WPI_TalonSRX(15);
  public static final WPI_TalonSRX frontRight = new WPI_TalonSRX(2);
  public static final WPI_TalonSRX backLeft = new WPI_TalonSRX(14);
  public static final WPI_TalonSRX backRight = new WPI_TalonSRX(1);
  public static final SpeedControllerGroup leftTalon = new SpeedControllerGroup(frontLeft, backLeft);
  public static final SpeedControllerGroup rightTalon = new SpeedControllerGroup(frontRight, backRight);

  public static final DifferentialDrive differentialDrive = new DifferentialDrive(leftTalon, rightTalon);

  public static Gyro gyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);

  public static Double getGyroAngle() {
    Double gyroData = gyro.getAngle();
    Double correctedData = gyroData - Math.floor(gyroData / 360) * 360;
    if (correctedData > 180) {
      correctedData = correctedData - 360;
    }
    return correctedData;
  }

  public static Double Heading = getGyroAngle();

  public Double speed = .75;

  goHome goHome = new goHome();

  public Double shooterPower = 0.61; // Power level for shooting
  public Double defaultIntakePowerMultiplier = 0.7; // Multiplier for power for intake triggers
  // public static Double drivetrainPowerMultiplier = 0.8; // Multiplier for
  // drivetrain joystick

  // public static ExampleSubsystem subsystem = new ExampleSubsystem();
  // public static Drivetrain drivetrain = new Drivetrain();
  // public static OI oi = new OI();

  // WPI_VictorSPX ingest = new WPI_VictorSPX(1);
  public static final Talon ingest = new Talon(0);
  Double autoTankSpeed = 0.33;
  Double turnPower = .6;

  private final VictorSPX shooterL = new VictorSPX(4);
  private final VictorSPX shooterR = new VictorSPX(3);

  public static Joystick controller = new Joystick(0);

  String autonomousCommand = null;
  // SendableChooser<String> chooser = new SendableChooser<>();
  SendableChooser<Double> shootingPowers = new SendableChooser<>();
  SendableChooser<Double> drivePowers = new SendableChooser<>();
  SendableChooser<Double> ingestPowers = new SendableChooser<>();
  SendableChooser<String> autoCourse = new SendableChooser<>();
  SendableChooser<Double> turnMult = new SendableChooser<>();

  private double BatVoltage = 0.0;
  //public static Boolean ballGotten = false;
  public static Double correctHeading = 0.0;
  public static Double headingError = 0.0;
  //public static Boolean doneGrabbing = false;
  public static Boolean doingGS = false;

  public static void manualMotors(Double leftValue, Double rightValue) {
    leftTalon.set(leftValue);
    rightTalon.set(rightValue * -1 * .933);
    differentialDrive.feed();
  }

  /*
   * public static void manualMotors(Double leftValue, Double rightValue) {
   * leftTalon.set(leftValue); rightTalon.set(-rightValue);
   * differentialDrive.feed(); }
   */

  public static void haltMotors() {
    leftTalon.stopMotor();
    rightTalon.stopMotor();
  }

  public static void autoHalt() {
    System.out.println("Halt");
    haltMotors();
    pauseLoop(.25);
  }

  public static void pauseLoop(Integer time) {
    doPause(time.longValue() * 1000);
  }

  public static void pauseLoop(Double time) {
    Double product = time * 1000;
    doPause(product.longValue());
  }

  public static void pauseLoop(Long time) {
    doPause(time * 1000);
  }

  public static void pauseLoop(Float time) {
    Float product = time * 1000;
    doPause(product.longValue());
  }

  static void doPause(Long time) {
    try {
      TimeUnit.MILLISECONDS.sleep(time);
    } catch (InterruptedException ex) {
      System.out.println(ex.toString());
    }
  }

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    // differentialDrive.setDeadband(.1);
    gyro.calibrate();
    Heading = getGyroAngle();
    Shuffleboard.getTab("Compass").add((Sendable) gyro);
    correctHeading = Math.floor(getGyroAngle() * 100) / 100;
    SmartDashboard.putNumber("North", correctHeading);

    UsbCamera theCamera = CameraServer.getInstance().startAutomaticCapture(0);
    theCamera.setVideoMode(PixelFormat.kMJPEG, 640, 360, 7);

    NTinst = NetworkTableInstance.getDefault();

    NNinterface = NTinst.getTable("coprocessor");
    NNturn = NNinterface.getEntry("turn");
    NNradius = NNinterface.getEntry("radius");
    NNcertainty = NNinterface.getEntry("certainty");
    NNlost = NNinterface.getEntry("guessing");
    NNblanks = NNinterface.getEntry("blanks");

    System.out.println("Init routined called");
    shootingPowers.addOption("100%", 1.0);
    shootingPowers.addOption("90%", 0.9);
    shootingPowers.addOption("80%", 0.8);
    shootingPowers.addOption("75%", .75);
    shootingPowers.addOption("70%", .7);
    shootingPowers.addOption("65%", .65);
    shootingPowers.setDefaultOption("61%", 0.61);
    shootingPowers.addOption("55%", .55);
    shootingPowers.addOption("105%", 1.05);
    shootingPowers.addOption("110%", 1.1);
    SmartDashboard.putData("Shooter power", shootingPowers);
    drivePowers.addOption("100%", 1.0);
    drivePowers.addOption("97%", 0.97);
    drivePowers.addOption("95%", .95);
    drivePowers.addOption("90%", 0.9);
    drivePowers.addOption("85%", 0.85);
    drivePowers.setDefaultOption("80%", 0.8);
    drivePowers.addOption("75%", 0.75);
    drivePowers.addOption("70%", 0.7);
    drivePowers.addOption("65%", 0.65);
    drivePowers.addOption("62%", 0.62);
    drivePowers.addOption("60%", 0.6);
    drivePowers.addOption("55%", 0.55);
    drivePowers.addOption("50%", 0.5);
    SmartDashboard.putData("Driving power", drivePowers);
    ingestPowers.addOption("100%", 1.0);
    ingestPowers.addOption("80%", .8);
    ingestPowers.addOption("75%", .75);
    ingestPowers.setDefaultOption("70%", .7);
    ingestPowers.addOption("65%", .65);
    ingestPowers.addOption("60%", .6);
    ingestPowers.addOption("55%", .55);
    ingestPowers.addOption("50%", .5);
    ingestPowers.addOption("10%", .1);
    SmartDashboard.putData("Intake power", ingestPowers);
    autoCourse.setDefaultOption("Galactic Search", "gs");
    autoCourse.addOption("Barrel Racing Path", "br");
    autoCourse.addOption("Slalom Path", "sl");
    autoCourse.addOption("Bounce Path", "bp");
    autoCourse.addOption("Test", "test");
    autoCourse.addOption("Go home", "gh");
    SmartDashboard.putData("Autocourse", autoCourse);
    turnMult.addOption("120%", 1.2);
    turnMult.addOption("110%", 1.1);
    turnMult.addOption("100%", 1.0);
    turnMult.addOption("90%", .9);
    turnMult.setDefaultOption("85%", .85);
    turnMult.addOption("80%", .8);
    turnMult.addOption("70%", .7);
    turnMult.addOption("100%", 1.0);
    SmartDashboard.putData("Turn power", turnMult);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    Heading = getGyroAngle();
    BatVoltage = RobotController.getBatteryVoltage();
    SmartDashboard.putNumber("Heading", Heading);
    SmartDashboard.putNumber("Rate", gyro.getRate());
    headingError = Robot.getGyroAngle() - Robot.correctHeading;
    SmartDashboard.putNumber("Orientation error", headingError);
    differentialDrive.feed();
    SmartDashboard.putBoolean("Motor safety", differentialDrive.isSafetyEnabled());
    differentialDrive.feedWatchdog();
    if (RobotController.isBrownedOut() == true) {
      // SmartDashboard.putString("Power status", "Partial failure");
      SmartDashboard.putBoolean("Power available", false);
      SmartDashboard.putString("Power status", "<<Brownout>>");
    } else if (BatVoltage < 10.0) {
      SmartDashboard.putBoolean("Power available", true);
      SmartDashboard.putString("Power status", "Low power");
    } else {
      SmartDashboard.putString("Power status", String.valueOf(df.format(BatVoltage)) + " V");
      SmartDashboard.putBoolean("Power available", true);
    }
    SmartDashboard.putBoolean("Curve driving", curveDriving);
    SmartDashboard.putString("AutonStage", String.valueOf(wn.format(autonStage)));
    SmartDashboard.putNumber("CAN usage%", RobotController.getCANStatus().percentBusUtilization * 100);
    SmartDashboard.updateValues();
    NTinst.flush();
  }

  /**
   * This function is called once each time the robot enters Disabled mode. You
   * can use it to reset any subsystem information you want to clear when the
   * robot is disabled.
   */
  @Override
  public void disabledInit() {
    ingest.set(0);
    differentialDrive.setSafetyEnabled(true);
    differentialDrive.stopMotor();
    // NTinst.flush();
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();
    /*
     * try { System.out.println("tv = " + detections.toString() + "  tx = " +
     * x_point.toString());} catch(Exception e) { System.out.println("Exception: " +
     * e.toString()); }
     */
  }

  @Override
  public void autonomousInit() {
    // differentialDrive.setDeadband(0);
    ballsGrabbed = 0;
    //ballGotten = false;
    //doneGrabbing = false;
    switch (autoCourse.getSelected()) {
    case "test":
      pathTest.start();
      break;
    case "gs":
      ballFollow.start();
      break;
    case "br":
      System.out.println("Starting barrel racing");
      barrelRace.start();
      break;
    case "gh":
      System.out.println("Going home");
      // doingGS = true;
      goHome.start();
      break;
    }
    autonStage = 1;
    // autonPath.start();
    Scheduler.getInstance().run();

  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    // NTinst.flush();
    Scheduler.getInstance().run();

  }

  @Override
  public void teleopInit() {
    autonStage = 99;
    SmartDashboard.putData("Shooter power", shootingPowers);
    differentialDrive.setSafetyEnabled(true);
    Scheduler.getInstance().run();
    ingest.set(0);
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    turnPower = turnMult.getSelected();
    if (controller.getRawButtonPressed(10)) {
      System.out.println("Curve driving toggled");
      if (curveDriving == false) {
        curveDriving = true;
      } else {
        curveDriving = false;
      }
    }
    if (curveDriving == false) {
      differentialDrive.arcadeDrive(-controller.getY(Hand.kLeft) * speed,
          controller.getX(Hand.kLeft) * speed * turnPower);
    } else {
      differentialDrive.curvatureDrive(-controller.getY(Hand.kLeft) * speed,
          controller.getX(Hand.kLeft) * speed * turnPower * 1.3, controller.getRawButton(9));
    }
    shooterPower = shootingPowers.getSelected();
    speed = drivePowers.getSelected();
    defaultIntakePowerMultiplier = ingestPowers.getSelected();
    // Scheduler.getInstance().run();
    // To change any power levels, see the beginning of the public class Robot
    // for the relavent variable definitions
    if (shooterPower > 1.0) {
      shooterL.overrideSoftLimitsEnable(true);
      shooterR.overrideSoftLimitsEnable(true);
    } else {
      shooterL.overrideSoftLimitsEnable(false);
      shooterR.overrideSoftLimitsEnable(false);
    }
    if (controller.getRawButton(5)) {
      shooterL.set(ControlMode.PercentOutput, shooterPower);
      shooterR.set(ControlMode.PercentOutput, shooterPower);
    } else if (controller.getRawButton(6)) {
      shooterL.set(ControlMode.PercentOutput, -shooterPower * .5);
      shooterR.set(ControlMode.PercentOutput, -shooterPower * .5);
    } else {
      shooterL.set(ControlMode.PercentOutput, 0);
      shooterR.set(ControlMode.PercentOutput, 0);
    }
    /*
     * if (controller.getRawButton(6)) { ingest.set(1); } else
     */ if (controller.getRawAxis(2) > 0) {
      ingest.set(defaultIntakePowerMultiplier * controller.getRawAxis(2));
    } else if (controller.getRawAxis(3) > 0) {
      ingest.set(defaultIntakePowerMultiplier * -controller.getRawAxis(3));
    } else {
      ingest.set(0);
    }

  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {

    /*
     * drivetrain.left.set(0.2); drivetrain.right.set(-0.2); try {
     * TimeUnit.SECONDS.sleep(2); } catch (InterruptedException e) {
     * e.printStackTrace(); } drivetrain.left.set(0.0); drivetrain.right.set(0.0);
     * try { TimeUnit.SECONDS.sleep(2); } catch (InterruptedException e) {
     * e.printStackTrace(); }
     */
  }
}
