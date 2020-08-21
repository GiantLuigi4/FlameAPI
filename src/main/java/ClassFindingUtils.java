public class ClassFindingUtils {
	public static boolean checkName(String name) {
		return
				name.endsWith(".class") &&
						!name.startsWith("com/tfc") &&
						!name.startsWith("com/github")
				;
	}
}
