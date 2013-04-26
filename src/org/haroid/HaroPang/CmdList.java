package org.haroid.HaroPang;

/**
 * @author kimdh
 * @remarks HaroPang과 Haroid가 주소받는 명령어를 담고 있는 Class
 * @remarks 차후 추가 및 수정 필요.
 * 
 */
public class CmdList {

	// 명령어 HEX 값
	// FF 0E FF 20 01 01 FE 서보0
	// FF 0E FF 20 01 02 FE 서보90
	// FF 0E FF 20 01 03 FE 서보180
	// FF 0E FF 30 02 00 20 FD DC정방향
	// FF 0E FF 30 02 20 00 FD DC역방향

	// #define 처럼 선언하여 사용하는 방식이 없을까?
	public static final int SERVO_0 = 1;
	public static final int SERVO_90 = 2;
	public static final int SERVO_180 = 3;
	public static final int DC_FORWARD = 4;
	public static final int DC_BACKWARD = 5;
	public static final byte[] Servo0 = { (byte) 0xFF, (byte) 0x0E, (byte) 0xFF, (byte) 0x20,
			(byte) 0x01, (byte) 0x01, (byte) 0xFF };
	public static final byte[] Servo90 = { (byte) 0xFF, (byte) 0x0E, (byte) 0xFF, (byte) 0x20,
			(byte) 0x01, (byte) 0x02, (byte) 0xFF };
	public static final byte[] Servo180 = { (byte) 0xFF, (byte) 0x0E, (byte) 0xFF, (byte) 0x20,
			(byte) 0x01, (byte) 0x03, (byte) 0xFF };
	public static final byte[] DCForward = { (byte) 0xFF, (byte) 0x0E, (byte) 0xFF, (byte) 0x30,
			(byte) 0x02, (byte) 0x00, (byte) 0x20, (byte) 0xFD };
	public static final byte[] DCBackward = { (byte) 0xFF, (byte) 0x0E, (byte) 0xFF, (byte) 0x30,
			(byte) 0x02, (byte) 0x20, (byte) 0x00, (byte) 0xFD };

}
