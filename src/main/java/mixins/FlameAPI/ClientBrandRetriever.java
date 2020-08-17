package mixins.FlameAPI;

import com.tfc.API.flame.AppendField;
import com.tfc.API.flame.Replace;

public class ClientBrandRetriever {
	@AppendField(targetClass = "net.minecraft.client.ClientBrandRetriever")
	public static String brand = "flamemc";
	
	@Replace(
			targetClass = "net.minecraft.client.ClientBrandRetriever",
			targetMethod = "getClientModName"
	)
	public String getClientModName() {
		return brand;
	}
}
