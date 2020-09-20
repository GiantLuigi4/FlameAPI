public class Block extends %block_class% {
	public final com.tfc.API.flamemc.blocks.Block thisBlock;
	
	private static String[] argsRemoved = null;
	private static String[] argsPlaced = null;
	private static String[] argsUpdated = null;
	
	public Block(com.tfc.API.flamemc.blocks.Block thisBlock, %properties_class% properties) {
		super(properties);
		this.thisBlock = thisBlock;
	}
	
	public void %removedMethod% {
		com.tfc.API.flamemc.abstraction.CallInfo info = new com.tfc.API.flamemc.abstraction.CallInfo(
				argsRemoved,
				%argsRemoved%
		);
		this.thisBlock.onRemoved(info);
	}
	
	public void %placedMethod% {
		com.tfc.API.flamemc.abstraction.CallInfo info = new com.tfc.API.flamemc.abstraction.CallInfo(
				argsPlaced,
				%argsPlaced%
		);
		this.thisBlock.onPlaced(info);
	}
	
	public void %updatedMethod% {
		com.tfc.API.flamemc.abstraction.CallInfo info = new com.tfc.API.flamemc.abstraction.CallInfo(
				argsUpdated,
				%argsUpdated%
		);
		this.thisBlock.onUpdated(info);
	}
}