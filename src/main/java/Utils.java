import java.io.InputStream;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Utils {
	public static void forAllFiles(JarFile file, BiConsumer<Scanner, JarEntry> textConsumer, Function<String, Boolean> fileValidator) {
		file.stream().forEach(f -> {
			if (fileValidator.apply(f.getName())) {
				try {
					InputStream stream = file.getInputStream(f);
					Scanner sc = new Scanner(stream);
					textConsumer.accept(sc, f);
					sc.close();
					stream.close();
				} catch (Throwable ignored) {
				}
			}
		});
	}
}
