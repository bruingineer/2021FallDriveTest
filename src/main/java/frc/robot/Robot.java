// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.SlewRateLimiter;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import java.util.List;

public class Robot extends TimedRobot {
  private final XboxController m_controller = new XboxController(0);

  // Slew rate limiters to make joystick inputs more gentle; 1/3 sec from 0
  // to 1.
  private final SlewRateLimiter m_speedLimiter = new SlewRateLimiter(6);
  private final SlewRateLimiter m_rotLimiter = new SlewRateLimiter(6);

  private final Drivetrain m_drive = new Drivetrain();
  private final RamseteController m_ramsete = new RamseteController();
  private final Timer m_timer = new Timer();
  private Trajectory m_trajectory;

  @Override
  public void robotInit() {
    // Flush NetworkTables every loop. This ensures that robot pose and other values
    // are sent during every iteration.
    setNetworkTablesFlushEnabled(true);
    m_trajectory =
        TrajectoryGenerator.generateTrajectory(
            new Pose2d(2, 2, new Rotation2d()),
            List.of(),
            new Pose2d(6, 4, new Rotation2d()),
            new TrajectoryConfig(1, 1));
  }

  @Override
  public void robotPeriodic() {
    m_drive.periodic();
  }

  @Override
  public void autonomousInit() {
    // m_timer.reset();
    // m_timer.start();
    // m_drive.resetOdometry(m_trajectory.getInitialPose());
  }

  @Override
  public void autonomousPeriodic() {
    // double elapsed = m_timer.get();
    // Trajectory.State reference = m_trajectory.sample(elapsed);
    // ChassisSpeeds speeds = m_ramsete.calculate(m_drive.getPose(), reference);
    // m_drive.drive(speeds.vxMetersPerSecond, speeds.omegaRadiansPerSecond);
  }

  @Override
  @SuppressWarnings("LocalVariableName")
  public void teleopPeriodic() {
    // Get the x speed. We are inverting this because Xbox controllers return
    // negative values when we push forward.
    double temp_y_left = util.deadband(m_controller.getY(GenericHID.Hand.kLeft), 0.1);
    double xSpeed =
        -m_speedLimiter.calculate(temp_y_left) * Drivetrain.kMaxSpeed;

    // Get the rate of angular rotation. We are inverting this because we want a
    // positive value when we pull to the left (remember, CCW is positive in
    // mathematics). Xbox controllers return positive values when you pull to
    // the right by default.
    double temp_x_right = util.deadband(m_controller.getX(GenericHID.Hand.kRight), 0.1);
    double rot =
        -m_rotLimiter.calculate(temp_x_right) * Drivetrain.kMaxAngularSpeed;
    m_drive.drive(xSpeed, rot);
  }

  @Override
  public void simulationPeriodic() {
    m_drive.simulationPeriodic();
  }
}
