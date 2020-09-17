package net.minecraft.client;

/**
 * This is a vanilla class, which is unmapped in the actual jar file
 * Directly referencing this is safe, due to {@link com.tfc.bytecode.compilers.Javassist_Compiler}, and class loading stuff
 * This is an almost exact replica of client_brand_retriever.java, meaning the ide will see this, use it, java will see the compiled client_brand_retriever.java
 * Only difference between this and client_brand_retriever.java is this javadoc, and when it is compiled
 */
public class ClientBrandRetriever {
	public static String brand = "flamemc";
	
	/**
	 * Referencing this might be useful for detecting what mod loaders are installed
	 * I plan to make it possible to install flame on forge, which would make this rather useful for detecting forge and fabric
	 * Anything provided by this API will be compatible with forge and fabric, but if you're making your own hacky class stuff, this will be useful
	 *
	 * @return The "brand" or name of the mod loader
	 */
	public static String getClientModName() {
		return brand;
	}
}
