package mixins.FlameAPI;

import com.tfc.API.flame.AppendField;
import com.tfc.API.flame.Hookin;

public class ClientBrandRetriever {
	@AppendField(
			targetClass = "net.minecraft.client.ClientBrandRetriever",
			defaultVal = "flamemc",
			type = "java.lang.String"
	)
	public static String brand = "flamemc";
	
	@Hookin(
			targetClass = "net.minecraft.client.ClientBrandRetriever",
			targetMethod = "getClientModName",
			point = "TOP"
	)
	public String getClientModName() {
		return brand;
	}
}
