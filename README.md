## FTCVuforiaDemo

## FTCVuforiaDemo

This linear opmode demonstrates one way to use Vuforia to track target images.
It has been provided as Technology Outreach by FTC Team 2818 G-FORCE from Accident MD.

Updated by Pattonville Robotics Teams 2866/2867/7856 to only require phones and no other hardware.

This Source Code:
- https://github.com/Pattonville-Robotics/FTCVuforiaDemo

Video Tutorial: Code Walk-Through
- https://www.youtube.com/watch?v=AxKrJEtfuaI

FTC Axis Geometry animation.
- https://www.youtube.com/watch?v=T3-F2xpaesg

This code was written to be compatible with FTC_SDK V 2.62 (Dec 2016).
Also compatible with FTC_SDK V3.1 (Aug 2017)

Place any of the four 2016/17 images on the field perimeter. 

To test the OpMode, do the following:

- Init the opmode 
- Manually move the robot to within a three feet of a target.  Point the robot towards the target.
- Verify that the target information appears on the Driver Station, and is correct.

 
### All navigation components are located in the Robot_Navigation class
 
A simplified tracking approach is used, whereby the robot can be made to approach to within a set distance of any visible target.  
Target localization is converted into a set of 6 values which can be used directly to navigate to the target.
  
- robotX: Distance from target along X axis in mm. (will be negative)
- robotY: Offset from target centerline along Y axis in mm. (can be + or -)
- robotBearing: Direction Robot is pointing. (+ is CCW from x axis)
- targetRange: Distance from robot centroid to target center in mm.
- targetBearing: Direction from robot centroid to target center (+ is CCW from x axis)
- relativeBearing: Angle from robot bearing to target bearing in degrees (+ is CCW)

A standoff TARGET_DISTANCE is defined in the TeleopOpMode to indicate how closely the robot should approach the target. 
The standdoff distance is measured from the center of the robot to the target in mm.

### Telemetry Display

Telemetry Data displays robot and target information whenever a target is "visible"
A sample display is shown here:

- Visible     blue near
- Robot       [X]:[Y] (B) [-646mm]:[80mm] (5°)
- Target      [R] (B):(RB) [651mm] (-7f°):(-12°)
- Turn      >>> CW 12°
- Strafe    RIGHT 80mm
- Distance  646mm
- Axes        A[0.42], L[0.22], Y[-0.21]
- Wheels      L[-0.74], R[0.09], B[0.00]

 
 




