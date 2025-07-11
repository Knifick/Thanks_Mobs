package net.knifick.praporupdate.util.ironkin;

public class ScreenShakeUtil {
	private static int shakeDuration;
	private static float shakeStrength;

	public static void startShake(int duration, float strength) {
		shakeDuration = duration;
		shakeStrength = strength;
	}

	public static int getShakeDuration() {
		return shakeDuration;
	}

	public static float getShakeStrength() {
		return shakeStrength;
	}

	public static void decreaseShake() {
		if (shakeDuration > 0) {
			shakeDuration--;
			shakeStrength *= 0.85F; // Плавное затухание
		}
	}
}
