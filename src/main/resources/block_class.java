public class Block extends %block_class% {
	public final com.tfc.API.flamemc.blocks.Block thisBlock;
	
	private static String[] argsRemoved = null;
	
	public Block(com.tfc.API.flamemc.blocks.Block thisBlock, %properties_class% properties) {
		super(properties);
		this.thisBlock = thisBlock;
	}
	
	public void onRemoved(Object[] args) {
		com.tfc.API.flamemc.abstraction.CallInfo info = new com.tfc.API.flamemc.abstraction.CallInfo(argsRemoved, args);
		this.thisBlock.onRemoved(info);
	}
	
	public void %removedMethod% {
		this.onRemoved(%argsRemoved%);
	}
}