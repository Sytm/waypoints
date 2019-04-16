package de.md5lukas.wp.util;

public class StringHelper {

	public static String generateDirectionIndicator(String indicator, String normal, String arrowLeft, String arrowRight,
	                                                String section, double angle, int amountOfSections, double range) {
		if (amountOfSections % 2 == 0)
			++amountOfSections;
		if (angle > range) {
			return indicator + arrowLeft + normal + repeatString(section, amountOfSections) + arrowRight;
		}
		if (angle < (-range)) {
			return normal + arrowLeft + repeatString(section, amountOfSections) + indicator + arrowRight;
		}
		double percent = -(angle / range);
		int nthSection = (int) Math.round(((double) (amountOfSections - 1) / 2) * percent);
		nthSection += Math.round((double) amountOfSections / 2);
		return normal + arrowLeft + repeatString(section, nthSection - 1) + indicator + section + normal + repeatString(section, amountOfSections - nthSection) + arrowRight;
	}

	public static String repeatString(String s, int amount) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < amount; ++i)
			sb.append(s);
		return sb.toString();
	}
}
