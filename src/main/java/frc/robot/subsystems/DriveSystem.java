package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Devices.Stick;
import frc.robot.Sensors.gyro;

public class DriveSystem extends SubsystemBase{

    int shaft = 6500; //rotations per minute 
    double wheelDiameter = 3.95; // in inches
    int maxRotationRate = 225; //degrees per second
    double gearRatio = 8.1428;
    int encoderPerRotation = 2048; //Number of encoder units per rotation
    int maxUnitsForPosition = 4096;
    int maxMetersPerSecond = 5; //this is an estimation


    double wheelCircumference = (wheelDiameter * Math.PI) /39.37; // Calculates the circumference using the diameter and converts to meters    


    SwerveModule[] module = new SwerveModule[4];

    SwerveDriveKinematics m_kinematics = new SwerveDriveKinematics(
    new Translation2d(0.4064, -0.4064),
    new Translation2d(0.4064, +0.4064),
    new Translation2d(-0.4064, -0.4064),
    new Translation2d(-0.4064, +0.4064));

    



    



    
    Stick joystick;

    public DriveSystem(Stick m_joystick){
        module[0] = new SwerveModule(1,2, 11);
      //  module[1] = new SwerveModule(3,4, 13);
        module[1] = new SwerveModule(5,6, 15);
        module[2] = new SwerveModule(7,8, 17);


        joystick=m_joystick;
    }
    
     gyro Gyro;
     double heading;
     @Override
     public void periodic(){
     heading = Gyro.getAngleRadians();  
    }

    public void controlmotors(){
       
        double stickval[] = joystick.getjoyaxis();
        SmartDashboard.putNumber("stickval0",stickval[0]);      
        SmartDashboard.putNumber("stickval1",stickval[1]);    
        SmartDashboard.putNumber("stickval2",stickval[2]);    
        SmartDashboard.putNumber("stickval3",stickval[3]);
        
        double omega  = (stickval[0]*(maxRotationRate*(Math.PI/180))); //rotation or position of wheel

        double vx = -maxMetersPerSecond * stickval[2];  // velocity x
        double vy = -maxMetersPerSecond * stickval[3];  // velocity y
       
        SmartDashboard.putNumber("omega",omega);
        SmartDashboard.putNumber("vx",vx);
        SmartDashboard.putNumber("xy", vy);

        ChassisSpeeds speeds = ChassisSpeeds.fromFieldRelativeSpeeds(vy, vx, omega,  new Rotation2d(heading));
        double encPosition[] = new double[3];
        SwerveModuleState StatesOptimized[] = new SwerveModuleState[3];


        SwerveModuleState[] moduleStates = m_kinematics.toSwerveModuleStates(speeds);

        SwerveDriveKinematics.desaturateWheelSpeeds(moduleStates,19313);


        int i = 0;
        while (i<3){
        
        encPosition[i]=module[i].getRmotorpos()*Math.PI/180.;
        
        StatesOptimized[i] = module[i].optimize(moduleStates[i],encPosition[i]);
        
        
        //converting position from radians to encoder units
        
        module[i].motormove(StatesOptimized[i].speedMetersPerSecond, StatesOptimized[i].angle.getRadians());

        i++;
        }
        SmartDashboard.putNumber("turnPos 1",encPosition[1]);
        SmartDashboard.putNumber("turnPos 2",encPosition[2]);
        SmartDashboard.putNumber("OptPos 1",StatesOptimized[0].speedMetersPerSecond);
        SmartDashboard.putNumber("OptPos 2",StatesOptimized[1].angle.getRadians());



       

    }
}
