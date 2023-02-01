package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

public class Drivetrain {
    private DcMotor fL;
    private DcMotor fR;
    private DcMotor bL;
    private DcMotor bR;
    IMU imu;
    YawPitchRollAngles ypr;

    LinearOpMode opMode;

    public Drivetrain(LinearOpMode opMode, HardwareMap hardwareMap) {
        fL = hardwareMap.get(DcMotor.class, "frontLeft");
        fR = hardwareMap.get(DcMotor.class, "frontRight");
        bL = hardwareMap.get(DcMotor.class, "backLeft");
        bR = hardwareMap.get(DcMotor.class, "backRight");
        IMU.Parameters parameters = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.UP,
                        RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
                )
        );
        imu = hardwareMap.get(IMU.class, "imu");
        ypr = imu.getRobotYawPitchRollAngles();
        imu.initialize(parameters);
        imu.resetYaw();

        this.opMode = opMode;

        fL.setDirection(DcMotor.Direction.REVERSE);
        fR.setDirection(DcMotor.Direction.FORWARD);
        bL.setDirection(DcMotor.Direction.REVERSE);
        bR.setDirection(DcMotor.Direction.FORWARD);

        startEncoders();

        fL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        fL.setPower(0);
        fR.setPower(0);
        bL.setPower(0);
        bR.setPower(0);
    }
    public void drive(int forward, double power) {
        fL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        fL.setTargetPosition(forward);
        fR.setTargetPosition(forward);
        bL.setTargetPosition(forward);
        bR.setTargetPosition(forward);

        fL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        fR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        fL.setPower(power);
        fR.setPower(power);
        bL.setPower(power);
        bR.setPower(power);

        while (fL.isBusy() && fR.isBusy()) {
            opMode.telemetry.addData("FR Target", fR.getTargetPosition());
            opMode.telemetry.addData("FR Position", fR.getCurrentPosition());
            opMode.telemetry.addData("FL Target", fL.getTargetPosition());
            opMode.telemetry.addData("FL Position", fL.getCurrentPosition());
            opMode.telemetry.update();
        }

        fL.setPower(0);
        fR.setPower(0);
        bL.setPower(0);
        bR.setPower(0);
    }
    public void strafe(int amount, double power, String direction) {
        fL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        if (direction.equals("right")) {
            fL.setTargetPosition(amount);
            fR.setTargetPosition(-amount);
            bL.setTargetPosition(-amount);
            bR.setTargetPosition(amount);
        } else if (direction.equals("left")) {
            fL.setTargetPosition(-amount);
            fR.setTargetPosition(amount);
            bL.setTargetPosition(amount);
            bR.setTargetPosition(-amount);
        }

        fL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        fR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        fL.setPower(power);
        fR.setPower(power);
        bL.setPower(power);
        bR.setPower(power);

        while (fL.isBusy() && fR.isBusy()) {
            opMode.telemetry.addData("FR Target", fR.getTargetPosition());
            opMode.telemetry.addData("FR Position", fR.getCurrentPosition());
            opMode.telemetry.addData("FL Target", fL.getTargetPosition());
            opMode.telemetry.addData("FL Position", fL.getCurrentPosition());
            opMode.telemetry.update();
        }

        fL.setPower(0);
        fR.setPower(0);
        bL.setPower(0);
        bR.setPower(0);
    }
    public void turn(double amount, double power, String direction) {
        fL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        imu.resetYaw();
        ypr = imu.getRobotYawPitchRollAngles();

        if (direction.equals("left")) power *= -1;

        fL.setPower(power);
        fR.setPower(-power);
        bL.setPower(power);
        bR.setPower(-power);

        while (opMode.opModeIsActive() && Math.abs(ypr.getYaw(AngleUnit.DEGREES)) < amount) {
            opMode.telemetry.addData("Current Angle", ypr.getYaw(AngleUnit.DEGREES));
            opMode.telemetry.addData("Target Angle", amount);
            opMode.telemetry.addData("Direction", direction);
            opMode.telemetry.update();

            ypr = imu.getRobotYawPitchRollAngles(); // Keep updating YPR until we reach ideal angle
        }

        fL.setPower(0);
        fR.setPower(0);
        bL.setPower(0);
        bR.setPower(0);
        startEncoders();
    }
    public void startEncoders() {
        fL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        fL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    public void piddrive(int forward) {
        fL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        fL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        double kp = 1 / 300.0;

        fL.setTargetPosition(forward);
        fR.setTargetPosition(forward);
        bL.setTargetPosition(forward);
        bR.setTargetPosition(forward);

        double error1 = forward - fL.getCurrentPosition();
        double error2 = forward - fR.getCurrentPosition();

        while (Math.abs(error1) > 15 && Math.abs(error2) > 15) {
            error1 = forward - fL.getCurrentPosition();
            error2 = forward - fR.getCurrentPosition();
            fL.setPower(kp * (forward - fL.getCurrentPosition()));
            fR.setPower(kp * (forward - fR.getCurrentPosition()));
            bL.setPower(kp * (forward - bL.getCurrentPosition()));
            bR.setPower(kp * (forward - bR.getCurrentPosition()));
        }

        fL.setPower(0);
        fR.setPower(0);
        bL.setPower(0);
        bR.setPower(0);
    }
    public void pidstrafe(int amount, String direction) {
        fL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        fL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        double kp = 1 / 300.0;

        if (direction.equals("right")) {
            fL.setTargetPosition(amount);
            fR.setTargetPosition(-amount);
            bL.setTargetPosition(-amount);
            bR.setTargetPosition(amount);
        } else if (direction.equals("left")) {
            fL.setTargetPosition(-amount);
            fR.setTargetPosition(amount);
            bL.setTargetPosition(amount);
            bR.setTargetPosition(-amount);
        }

        double error1 = fL.getTargetPosition() - fL.getCurrentPosition();
        double error2 = fR.getTargetPosition() - fR.getCurrentPosition();

        while (Math.abs(error1) > 15 && Math.abs(error2) > 15) {
            error1 = fL.getTargetPosition() - fL.getCurrentPosition();
            error2 = fR.getTargetPosition() - fR.getCurrentPosition();
            fL.setPower(kp * (fL.getTargetPosition() - fL.getCurrentPosition()));
            fR.setPower(kp * (fR.getTargetPosition() - fR.getCurrentPosition()));
            bL.setPower(kp * (bL.getTargetPosition() - bL.getCurrentPosition()));
            bR.setPower(kp * (bR.getTargetPosition() - bR.getCurrentPosition()));
        }

        fL.setPower(0);
        fR.setPower(0);
        bL.setPower(0);
        bR.setPower(0);
    }
    public void wait(int ms) {
        ElapsedTime t = new ElapsedTime();
        t.startTime();
        while (t.milliseconds() < ms) {}
        t.reset();
    }
}
