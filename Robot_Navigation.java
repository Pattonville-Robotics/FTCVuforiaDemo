package org.firstinspires.ftc.teamcode.FTCVuforiaDemo;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.robot.Robot;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.angle;
import static android.R.attr.targetName;
import static android.view.View.X;

/**
 * This is NOT an opmode.
 *
 * This class is used to define all the specific navigation tasks for the Target Tracking Demo
 * It focuses on setting up and using the Vuforia Library, which is part of the 2016-2017 FTC SDK
 *
 * Once a target is identified, its information is displayed as telemetry data.
 *
 */

public class Robot_Navigation
{
    // Select which camera you want use.  The FRONT camera is the one on the same side as the screen.  Alt. is BACK
    public enum CameraType {Front, Back};

    // Select orientation of the camera. LandscapeLeft is camera on left from behind the robot.
    public enum CameraRotation {Portrait, LandscapeLeft, LandscapeRight};

    // Constants
    private static final int     MAX_TARGETS    =   4;
    private static final double  MM_TO_INCHES   =   1.0 / 25.4;

    /* Private class members. */
    private LinearOpMode        myOpMode;       // Access to the OpMode object
    private VuforiaTrackables   targets;        // List of active targets

    // Navigation data is only valid if targetFound == true;
    private boolean             targetFound;    // set to true if Vuforia is currently tracking a target
    private String              targetName;     // Name of the currently tracked target
    private double              robotX;         // X displacement from target center
    private double              robotY;         // Y displacement from target center
    private double              robotBearing;   // Robot's rotation around the Z axis (CCW is positive)
    private double              targetRange;    // Range from robot's center to target in mm
    private double              targetBearing;  // Heading of the target , relative to the robot's unrotated center
    private double              relativeBearing;// Heading to the target from the robot's current bearing.
                                                //   eg: a Positive RelativeBearing means the robot must turn CCW to point at the target image.

    /* Constructor */
    public Robot_Navigation(){

        targetFound = false;
        targetName = null;
        targets = null;

        robotX = 0;
        robotY = 0;
        targetRange = 0;
        targetBearing = 0;
        robotBearing = 0;
        relativeBearing = 0;
    }

    /***
     * Send telemetry data to indicate navigation status
     */
    public void addNavTelemetry() {
        if (targetFound)
        {
            // Display the current visible target name, robot info, target info, and required robot action.
            myOpMode.telemetry.addData("Visible", targetName);
            myOpMode.telemetry.addData("Robot Range", "%3.0f in", robotX * MM_TO_INCHES);
            myOpMode.telemetry.addData("Robot Offset", "%3.0f in", robotY * MM_TO_INCHES);
            myOpMode.telemetry.addData("Robot Rel Angle to Picture", "%4.0f°", robotBearing);
            myOpMode.telemetry.addData("Target Range", "%4.0f in", targetRange * MM_TO_INCHES);
            myOpMode.telemetry.addData("Target Bearing", "%4.0f°", targetBearing);
            myOpMode.telemetry.addData("Relative Bearing", "%4.0f°", relativeBearing);
        }
        else
        {
            myOpMode.telemetry.addData("Visible", "- - - -" );
        }
    }

    /***
     * Start tracking Vuforia images
     */
    public void activateTracking() {

        // Start tracking any of the defined targets
        if (targets != null)
            targets.activate();
    }

    // X displacement from target center
    public double getRobotX() {
        return robotX;
    };

    // Y displacement from target center
    public double getRobotY() {
        return robotY;
    };

    // Robot's rotation around the Z axis (CCW is positive)
    public double getRobotBearing() {
        return robotBearing;
    };

    // Range from robot's center to target in mm
    public double getTargetRange() {
        return targetRange;
    };

    // Heading of the target , relative to the robot's unrotated center
    public double getTargetBearing() {
        return targetBearing;
    };

    // Heading to the target from the robot's current bearing.
    public double getRelativeBearing() {
        return relativeBearing;
    };

    /***
     * Initialize the Target Tracking and navigation interface
     * @param opMode          pointer to OpMode
     * @param cameraType      using front or back camera
     * @param cameraRotation  orientation of phone
     */
    public void initVuforia(LinearOpMode opMode, CameraType cameraType, CameraRotation cameraRotation) {

        VuforiaLocalizer.CameraDirection cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        float Yrotation = -90; // Back camera
        float Xrotation = 0;   // Landscape Left

        if (cameraType == CameraType.Front)
        {
            cameraDirection = VuforiaLocalizer.CameraDirection.FRONT;
            Yrotation = 90;
        }

        if (cameraRotation == CameraRotation.Portrait)
        {
            Xrotation = 90;
        }
        else if (cameraRotation == CameraRotation.LandscapeRight)
        {
            Xrotation = 180;
        }

        // Save reference to OpMode and Hardware map
        myOpMode = opMode;

        /**
         * Start up Vuforia, telling it the id of the view that we wish to use as the parent for
         * the camera monitor.
         * We also indicate which camera on the RC that we wish to use.
         */

        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);  // Use this line to see camera display
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();                             // OR... Use this line to improve performance

        // Get your own Vuforia key at  https://developer.vuforia.com/license-manager
        // and paste it here...
        parameters.vuforiaLicenseKey = "Afbu2Uv/////AAAAGVouNdSAD0P8la+sq37vCdQ6uLVH8NWrBLnfZ1R5rObJQpVVHJzqvIgMZO5gTqXG6DYJZcgwtSVZXU2g20FAJobxCog9Wc5vtqgJJmrsJ0NOABRbi9vy4Y9IzBVfaDoRsQTmjxxFf62Z9slttsb44KopGpVGTQ83iHnTo/wDvnZBWRhmckG6IKuqkbRYCFD+w1hHvVLuDoIYLgfpa1Rw1Pc7rszP/CDzUfeO9KwodFpEsfZHIZI8KHIYzfRIOhg1Tg0T4eRsLCO8s9vfZd6vfTuUA/sZkID3N7BsrlLaL6vUqheGPvsbPuQQsMqgPNYTqbhvv3KI/SR5WxUaccuVHnpVMhAjkdpruWVliCCZqp1t";

        parameters.cameraDirection = cameraDirection;
        parameters.useExtendedTracking = false;
        VuforiaLocalizer vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        /**
         * Load the data sets that for the trackable objects we wish to track.
         * These particular data sets are stored in the 'assets' part of our application
         * They represent the four image targets used in the 2016-17 FTC game.
         */
        targets = vuforia.loadTrackablesFromAsset("FTC_2016-17");
        targets.get(0).setName("Blue Near");
        targets.get(1).setName("Red Far");
        targets.get(2).setName("Blue Far");
        targets.get(3).setName("Red Near");

        /** For convenience, gather together all the trackable objects in one easily-iterable collection */
        List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
        allTrackables.addAll(targets);

        // create an image translation/rotation matrix to be used for all images
        // Essentially put all the image centers 6" above the 0:0:0 origin,
        // but rotate them so they along the -X axis.
        OpenGLMatrix targetOrientation = OpenGLMatrix
                .translation(0, 0, 150)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XYZ,
                        AngleUnit.DEGREES, 90, 0, -90));

        /**
         * Create a transformation matrix describing where the phone is on the robot.
         *
         * The coordinate frame for the robot looks the same as the field.
         * The robot's "forward" direction is facing out along X axis, with the LEFT side facing out along the Y axis.
         * Z is UP on the robot.  This equates to a bearing angle of Zero degrees.
         *
         * The phone starts out lying flat, with the screen facing Up and with the physical top of the phone
         * pointing to the LEFT side of the Robot.  If we consider that the camera and screen will be
         * in "Landscape Mode" the upper portion of the screen is closest to the front of the robot.
         *
         * If using the rear (High Res) camera:
         * We need to rotate the camera around it's long axis to bring the rear camera forward.
         * This requires a negative 90 degree rotation on the Y axis
         *
         * If using the Front (Low Res) camera
         * We need to rotate the camera around it's long axis to bring the FRONT camera forward.
         * This requires a Positive 90 degree rotation on the Y axis
         *
         * Next, translate the camera lens to where it is on the robot.
         * In this example, it is centered (left to right), but 110 mm forward of the middle of the robot, and 200 mm above ground level.
         */

        final int CAMERA_FORWARD_DISPLACEMENT  = 0;   // Camera is 0 mm in front of robot center
        final int CAMERA_VERTICAL_DISPLACEMENT = 0;   // Camera is 0 mm above ground
        final int CAMERA_LEFT_DISPLACEMENT     = 0;     // Camera is ON the robots center line

        OpenGLMatrix phoneLocationOnRobot = OpenGLMatrix
            .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
            .multiplied(Orientation.getRotationMatrix(
                    AxesReference.EXTRINSIC, AxesOrder.YZX,
                    AngleUnit.DEGREES, Yrotation, 0, Xrotation));

        // Set the all the targets to have the same location and camera orientation
        for (VuforiaTrackable trackable : allTrackables)
        {
            trackable.setLocation(targetOrientation);
            ((VuforiaTrackableDefaultListener)trackable.getListener()).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);
        }
    }


    /***
     * See if any of the vision targets are in sight.
     *
     * @return true if any target is found
     */
    public boolean targetsAreVisible()  {

        int targetTestID = 0;

        // Check each target in turn, but stop looking when the first target is found.
        while ((targetTestID < MAX_TARGETS) && !targetIsVisible(targetTestID)) {
            targetTestID++ ;
        }

        return (targetFound);
    }

    /***
     * Determine if specified target ID is visible and
     * If it is, retreive the relevant data, and then calculate the Robot and Target locations
     *
     * @param   targetId
     * @return  true if the specified target is found
     */
    public boolean targetIsVisible(int targetId) {

        VuforiaTrackable target = targets.get(targetId);
        VuforiaTrackableDefaultListener listener = (VuforiaTrackableDefaultListener)target.getListener();
        OpenGLMatrix location  = null;

        // if we have a target, look for an updated robot position
        if ((target != null) && (listener != null) && listener.isVisible()) {
            targetFound = true;
            targetName = target.getName();

            // If we have an updated robot location, update all the relevant tracking information
            location  = listener.getUpdatedRobotLocation();
            if (location != null) {

                // Create a translation and rotation vector for the robot.
                VectorF trans = location.getTranslation();
                Orientation rot = Orientation.getOrientation(location, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);

                // Robot position is defined by the standard Matrix translation (x and y)
                robotX = trans.get(0);
                robotY = trans.get(1);

                // Robot bearing (in +vc CCW cartesian system) is defined by the standard Matrix z rotation
                robotBearing = rot.thirdAngle;

                // target range is based on distance from robot position to origin.
                targetRange = Math.hypot(robotX, robotY);

                // target bearing is based on angle formed between the X axis to the target range line
                targetBearing = Math.toDegrees(-Math.asin(robotY / targetRange));

                // Target relative bearing is the target Heading relative to the direction the robot is pointing.
                relativeBearing = targetBearing - robotBearing;
            }
            targetFound = true;
        }
        else  {
            // Indicate that there is no target visible
            targetFound = false;
            targetName = "None";
        }

        return targetFound;
    }
}

